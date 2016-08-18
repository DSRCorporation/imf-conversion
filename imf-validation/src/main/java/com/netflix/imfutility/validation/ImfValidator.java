/**
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.imfutility.validation;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.netflix.imflibrary.IMFConstraints;
import com.netflix.imflibrary.IMFErrorLogger;
import com.netflix.imflibrary.IMFErrorLogger.IMFErrors.ErrorCodes;
import com.netflix.imflibrary.IMFErrorLogger.IMFErrors.ErrorLevels;
import com.netflix.imflibrary.IMFErrorLoggerImpl;
import com.netflix.imflibrary.MXFOperationalPattern1A;
import com.netflix.imflibrary.RESTfulInterfaces.IMPValidator;
import com.netflix.imflibrary.RESTfulInterfaces.PayloadRecord;
import com.netflix.imflibrary.RESTfulInterfaces.PayloadRecord.PayloadAssetType;
import com.netflix.imflibrary.exceptions.IMFException;
import com.netflix.imflibrary.exceptions.MXFException;
import com.netflix.imflibrary.st0377.HeaderPartition;
import com.netflix.imflibrary.st0429_8.PackingList;
import com.netflix.imflibrary.st0429_9.AssetMap;
import com.netflix.imflibrary.st2067_2.Composition;
import com.netflix.imflibrary.utils.ByteArrayByteRangeProvider;
import com.netflix.imflibrary.utils.ByteArrayDataProvider;
import com.netflix.imflibrary.utils.ErrorLogger.ErrorObject;
import com.netflix.imflibrary.utils.FileByteRangeProvider;
import com.netflix.imflibrary.utils.ResourceByteRangeProvider;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Validates the provided IMP and CPL using Photon lib.
 * Found validation errors are printed in a specified file in XML format.
 */
public class ImfValidator {

    public static void main(String... args) {
        try {
            // 1. input parameters
            ImfValidationCmdLineArgs imfValidationCmdLineArgs = CliFactory.parseArguments(ImfValidationCmdLineArgs.class, args);
            String impFolder = imfValidationCmdLineArgs.getImpFolder();
            String cpl = imfValidationCmdLineArgs.getCpl();
            String workingDir = imfValidationCmdLineArgs.getOutputDirectory();
            String outputFile = imfValidationCmdLineArgs.getOutputFileName();
            List<String> essenceFiles = imfValidationCmdLineArgs.getEssence();

            // 2. do validate
            List<ErrorObject> result = new ImfValidator().validate(impFolder, cpl, essenceFiles);

            // 3. print result in xml
            new ImfErrorXmlPresenter().printErrors(result, workingDir, outputFile);

            System.exit(0);
        } catch (HelpRequestedException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public List<ErrorObject> validate(String impFolder, String cplFullPath, List<String> essenceFiles) throws IOException {
        // 1. get the content of the IMP
        File impFile = new File(impFolder);
        File[] files = impFile.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        // 2. prepare files to be validated

        // 2.1 CPL
        File cpl = new File(cplFullPath);

        // 2.2 PKLs and Assetmap
        File assetmap = null;
        List<File> pkls = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith(".xml")) {
                switch (IMPValidator.getPayloadType(createPayloadRecord(file))) {
                    case AssetMap:
                        assetmap = file;
                        break;
                    case PackingList:
                        pkls.add(file);
                        break;
                    default:
                        // nothing
                }
            }
        }

        // 2.3 essence resources (either explicitly provided or all .mxf files are validated).
        List<File> mxfs = new ArrayList<>();
        if (essenceFiles != null && !essenceFiles.isEmpty()) {
            for (String essenceResource : essenceFiles) {
                mxfs.add(new File(essenceResource));
            }
        } else {
            for (File file : files) {
                if (file.getName().endsWith(".mxf") || file.getName().endsWith(".mxf.hdr")) {
                    mxfs.add(file);
                }
            }
        }

        // 3. validate
        List<ErrorObject> result = new ArrayList<>();

        // 3.1 validate CPL
        result.addAll(validateCpl(cpl));

        // 3.2 validate ASSETMAP
        if (assetmap != null) {
            result.addAll(validateAssetMap(assetmap));
        }

        // 3.3 validate PKLs
        for (File pkl : pkls) {
            result.addAll(validatePkl(pkl));
        }

        // 3.4 validate ASSETMAP and PKLs
        if (assetmap != null) {
            result.addAll(validatePklAndAssetMap(assetmap, pkls));
        }

        // 3.5 validate MXF essences
        for (File mxf : mxfs) {
            result.addAll(validateMxf(mxf));
        }

        // 3.7 validate CPL conformance
        result.addAll(validateCplConformance(cpl, mxfs));

        return result;
    }

    private List<ErrorObject> doValidate(ErrorCodes errCode, IValidator validator) {
        // IMPValidator.validateXXX just throws IMFException or MXFException, and information about errors is lost.
        IMFErrorLogger imfErrorLogger = new IMFErrorLoggerImpl();
        try {
            return validator.validate(imfErrorLogger);
        } catch (SAXException | JAXBException | URISyntaxException | IOException e) {
            imfErrorLogger.addError(errCode, ErrorLevels.FATAL, e.getMessage());
            return imfErrorLogger.getErrors();
        } catch (IMFException e) {
            if (!imfErrorLogger.getErrors().isEmpty()) {
                return imfErrorLogger.getErrors();
            }
            imfErrorLogger.addError(errCode, ErrorLevels.FATAL, e.getMessage());
            return imfErrorLogger.getErrors();
        } catch (MXFException e) {
            if (!imfErrorLogger.getErrors().isEmpty()) {
                return imfErrorLogger.getErrors();
            }
            imfErrorLogger.addError(errCode, ErrorLevels.FATAL, e.getMessage());
            return imfErrorLogger.getErrors();
        } catch (Exception e) {
            imfErrorLogger.addError(errCode, ErrorLevels.FATAL, e.getMessage());
            return imfErrorLogger.getErrors();
        }
    }

    private List<ErrorObject> validateCpl(File cpl) {
        return doValidate(ErrorCodes.IMF_CPL_ERROR,
                imfErrorLogger -> {
                    PayloadRecord payloadRecord = createPayloadRecord(cpl, PayloadAssetType.CompositionPlaylist);
                    new Composition(new ByteArrayByteRangeProvider(payloadRecord.getPayload()), imfErrorLogger);
                    return imfErrorLogger.getErrors();
                });
    }

    private List<ErrorObject> validatePkl(File pkl) {
        return doValidate(ErrorCodes.IMF_PKL_ERROR,
                imfErrorLogger -> {
                    PayloadRecord payloadRecord = createPayloadRecord(pkl, PayloadAssetType.PackingList);
                    new PackingList(new ByteArrayByteRangeProvider(payloadRecord.getPayload()), imfErrorLogger);
                    return imfErrorLogger.getErrors();
                });
    }

    private List<ErrorObject> validateAssetMap(File assetMap) {
        return doValidate(ErrorCodes.IMF_AM_ERROR,
                imfErrorLogger -> {
                    PayloadRecord payloadRecord = createPayloadRecord(assetMap, PayloadAssetType.AssetMap);
                    new AssetMap(new ByteArrayByteRangeProvider(payloadRecord.getPayload()), imfErrorLogger);
                    return imfErrorLogger.getErrors();
                });
    }

    private List<ErrorObject> validatePklAndAssetMap(File assetMap, List<File> pkls) {
        return doValidate(ErrorCodes.IMF_MASTER_PACKAGE_ERROR,
                imfErrorLogger -> {
                    PayloadRecord assetMapPayload = createPayloadRecord(assetMap, PayloadAssetType.AssetMap);
                    List<PayloadRecord> pklPayloads = new ArrayList<>();
                    for (File pkl : pkls) {
                        pklPayloads.add(createPayloadRecord(pkl, PayloadAssetType.PackingList));
                    }
                    return IMPValidator.validatePKLAndAssetMap(assetMapPayload, pklPayloads);
                });
    }


    private List<ErrorObject> validateMxf(File mxf) {
        return doValidate(ErrorCodes.IMF_ESSENCE_COMPONENT_ERROR,
                imfErrorLogger -> {
                    PayloadRecord headerPartitionPayloadRecord = getHeaderPartition(mxf, imfErrorLogger);
                    if (!imfErrorLogger.getErrors().isEmpty() || (headerPartitionPayloadRecord == null)) {
                        return imfErrorLogger.getErrors();
                    }

                    HeaderPartition headerPartition = new HeaderPartition(
                            new ByteArrayDataProvider(headerPartitionPayloadRecord.getPayload()),
                            0L,
                            (long) headerPartitionPayloadRecord.getPayload().length,
                            imfErrorLogger);
                    MXFOperationalPattern1A.HeaderPartitionOP1A headerPartitionOP1A =
                            MXFOperationalPattern1A.checkOperationalPattern1ACompliance(headerPartition);
                    IMFConstraints.checkIMFCompliance(headerPartitionOP1A);
                    return imfErrorLogger.getErrors();
                });
    }

    private List<ErrorObject> validateCplConformance(File cpl, List<File> mxfs) {
        return doValidate(ErrorCodes.IMF_CPL_ERROR,
                imfErrorLogger -> {
                    List<PayloadRecord> headerPayloadRecords = new ArrayList<>();
                    for (File mxf : mxfs) {
                        PayloadRecord headerPartitionPayloadRecord = getHeaderPartition(mxf, imfErrorLogger);
                        if (headerPartitionPayloadRecord != null) {
                            headerPayloadRecords.add(headerPartitionPayloadRecord);
                        }
                    }
                    if (!imfErrorLogger.getErrors().isEmpty()) {
                        return imfErrorLogger.getErrors();
                    }

                    PayloadRecord cplPayloadRecord = createPayloadRecord(cpl, PayloadAssetType.CompositionPlaylist);

                    return IMPValidator.areAllVirtualTracksInCPLConformed(cplPayloadRecord, headerPayloadRecords);
                });
    }

    private PayloadRecord createPayloadRecord(File inputFile, PayloadAssetType assetType) throws IOException {
        ResourceByteRangeProvider resourceByteRangeProvider = new FileByteRangeProvider(inputFile);
        byte[] bytes = resourceByteRangeProvider.getByteRangeAsBytes(0, resourceByteRangeProvider.getResourceSize() - 1);
        return new PayloadRecord(bytes, assetType, 0L, resourceByteRangeProvider.getResourceSize());
    }

    private PayloadRecord createPayloadRecord(File inputFile) throws IOException {
        ResourceByteRangeProvider resourceByteRangeProvider = new FileByteRangeProvider(inputFile);
        byte[] bytes = resourceByteRangeProvider.getByteRangeAsBytes(0, resourceByteRangeProvider.getResourceSize() - 1);
        return new PayloadRecord(bytes, PayloadAssetType.Unknown, 0L, resourceByteRangeProvider.getResourceSize());
    }

    private PayloadRecord getHeaderPartition(File mxf, IMFErrorLogger imfErrorLogger) throws IOException {
        if (mxf.getName().endsWith(".hdr")) {
            return fromHeaderPartition(mxf, imfErrorLogger);
        }
        ResourceByteRangeProvider resourceByteRangeProvider = new FileByteRangeProvider(mxf);
        long archiveFileSize = resourceByteRangeProvider.getResourceSize();
        long rangeEnd = archiveFileSize - 1;
        long rangeStart = archiveFileSize - 4;
        byte[] bytes = resourceByteRangeProvider.getByteRangeAsBytes(rangeStart, rangeEnd);
        PayloadRecord payloadRecord = new PayloadRecord(bytes, PayloadRecord.PayloadAssetType.EssenceFooter4Bytes, rangeStart, rangeEnd);
        Long randomIndexPackSize = IMPValidator.getRandomIndexPackSize(payloadRecord);

        rangeStart = archiveFileSize - randomIndexPackSize;
        rangeEnd = archiveFileSize - 1;

        byte[] randomIndexPackBytes = resourceByteRangeProvider.getByteRangeAsBytes(rangeStart, rangeEnd);
        PayloadRecord randomIndexPackPayload = new PayloadRecord(
                randomIndexPackBytes, PayloadRecord.PayloadAssetType.EssencePartition, rangeStart, rangeEnd);
        List<Long> partitionByteOffsets = IMPValidator.getEssencePartitionOffsets(randomIndexPackPayload, randomIndexPackSize);

        if (partitionByteOffsets.size() < 2) {
            imfErrorLogger.addError(
                    IMFErrorLogger.IMFErrors.ErrorCodes.IMF_ESSENCE_COMPONENT_ERROR, IMFErrorLogger.IMFErrors.ErrorLevels.FATAL,
                    String.format("Can not get essence component header for '%s'", mxf.getAbsolutePath()));
            return null;
        }
        long headerRangeStart = partitionByteOffsets.get(0);
        long headerRangeEnd = partitionByteOffsets.get(1) - 1;
        byte[] headerPartitionBytes = resourceByteRangeProvider.getByteRangeAsBytes(headerRangeStart, headerRangeEnd);
        return new PayloadRecord(headerPartitionBytes, PayloadRecord.PayloadAssetType.EssencePartition, headerRangeStart, headerRangeEnd);
    }

    private PayloadRecord fromHeaderPartition(File mxfHdr, IMFErrorLogger imfErrorLogger) throws IOException {
        ResourceByteRangeProvider resourceByteRangeProvider = new FileByteRangeProvider(mxfHdr);
        byte[] headerPartitionBytes = resourceByteRangeProvider.getByteRangeAsBytes(0, resourceByteRangeProvider.getResourceSize() - 1);
        return new PayloadRecord(headerPartitionBytes, PayloadRecord.PayloadAssetType.EssencePartition, 0L, 0L);
    }

    private interface IValidator {

        List<ErrorObject> validate(IMFErrorLogger imfErrorLogger) throws IOException, SAXException, JAXBException, URISyntaxException;

    }

}
