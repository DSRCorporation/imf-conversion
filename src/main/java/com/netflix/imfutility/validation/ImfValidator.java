package com.netflix.imfutility.validation;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.netflix.imflibrary.IMFErrorLogger;
import com.netflix.imflibrary.IMFErrorLoggerImpl;
import com.netflix.imflibrary.RESTfulInterfaces.IMPValidator;
import com.netflix.imflibrary.RESTfulInterfaces.PayloadRecord;
import com.netflix.imflibrary.RESTfulInterfaces.PayloadRecord.PayloadAssetType;
import com.netflix.imflibrary.st2067_2.CompositionPlaylist;
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

            // 2. do validate
            List<ErrorObject> result = new ImfValidator().validate(impFolder, cpl);

            // 3. print result in xml
            new ImfErrorXmlPresenter().printErrors(result, workingDir, outputFile);

            System.exit(0);
        } catch (HelpRequestedException e) {
            System.err.println(e.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public List<ErrorObject> validate(String impFolder, String cplFullPath) throws IOException {
        // 1. get the content of the IMP
        File impFile = new File(impFolder);
        File[] files = impFile.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        // 2. prepare the files to be validated
        File assetmap = null;
        List<File> pkls = new ArrayList<>();
        List<File> mxfs = new ArrayList<>();
        File cpl = new File(cplFullPath);
        for (File file : files) {
            if (file.getName().endsWith(".xml")) {
                switch (IMPValidator.getPayloadType(createPayloadRecord(file))) {
                    case AssetMap:
                        assetmap = file;
                        break;
                    case PackingList:
                        pkls.add(file);
                }
            } else if (file.getName().endsWith(".mxf")) {
                mxfs.add(file);
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
        if (shouldCheckCplConformance(cpl)) {
            result.addAll(validateCplConformance(cpl, mxfs));
        }

        return result;
    }

    private List<ErrorObject> validateCpl(File cpl) throws IOException {
        return IMPValidator.validateCPL(
                createPayloadRecord(cpl, PayloadAssetType.CompositionPlaylist));
    }

    private List<ErrorObject> validatePkl(File pkl) throws IOException {
        return IMPValidator.validatePKL(
                createPayloadRecord(pkl, PayloadAssetType.PackingList));
    }

    private List<ErrorObject> validateAssetMap(File assetMap) throws IOException {
        return IMPValidator.validateAssetMap(
                createPayloadRecord(assetMap, PayloadAssetType.AssetMap));
    }

    private List<ErrorObject> validatePklAndAssetMap(File assetMap, List<File> pkls) throws IOException {
        PayloadRecord assetMapPayloadRecord = createPayloadRecord(assetMap, PayloadAssetType.AssetMap);
        List<PayloadRecord> pklPayloadRecords = new ArrayList<>();
        for (File pkl : pkls) {
            pklPayloadRecords.add(createPayloadRecord(pkl, PayloadAssetType.PackingList));
        }
        return IMPValidator.validatePKLAndAssetMap(assetMapPayloadRecord, pklPayloadRecords);
    }


    private PayloadRecord getHeaderPartition(File mxf, List<ErrorObject> errors) throws IOException {
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
        PayloadRecord randomIndexPackPayload = new PayloadRecord(randomIndexPackBytes, PayloadRecord.PayloadAssetType.EssencePartition, rangeStart, rangeEnd);
        List<Long> partitionByteOffsets = IMPValidator.getEssencePartitionOffsets(randomIndexPackPayload, randomIndexPackSize);

        if (partitionByteOffsets.size() < 2) {
            errors.add(new ErrorObject(
                    IMFErrorLogger.IMFErrors.ErrorCodes.IMF_ESSENCE_COMPONENT_ERROR, IMFErrorLogger.IMFErrors.ErrorLevels.FATAL,
                    String.format("Can not get essence component header for '%s'", mxf.getAbsolutePath())));
            return null;
        }
        long headerRangeStart = partitionByteOffsets.get(0);
        long headerRangeEnd = partitionByteOffsets.get(1) - 1;
        byte[] headerPartitionBytes = resourceByteRangeProvider.getByteRangeAsBytes(headerRangeStart, headerRangeEnd);
        return new PayloadRecord(headerPartitionBytes, PayloadRecord.PayloadAssetType.EssencePartition, headerRangeStart, headerRangeEnd);
    }

    private List<ErrorObject> validateMxf(File mxf) throws IOException {
        List<ErrorObject> errors = new ArrayList<>();
        PayloadRecord headerPartitionPayloadRecord = getHeaderPartition(mxf, errors);
        if (!errors.isEmpty()) {
            return errors;
        }

        List<PayloadRecord> headerPartitions = new ArrayList<>();
        headerPartitions.add(headerPartitionPayloadRecord);

        return IMPValidator.validateIMFEssenceComponentHeaderMetadata(headerPartitions);
    }

    private List<ErrorObject> validateCplConformance(File cpl, List<File> mxfs) throws IOException {
        List<ErrorObject> errors = new ArrayList<>();
        List<PayloadRecord> headerPayloadRecords = new ArrayList<>();
        for (File mxf : mxfs) {
            PayloadRecord headerPartitionPayloadRecord = getHeaderPartition(mxf, errors);
            if (!errors.isEmpty()) {
                return errors;
            }
            headerPayloadRecords.add(headerPartitionPayloadRecord);
        }

        PayloadRecord cplPayloadRecord = createPayloadRecord(cpl, PayloadAssetType.CompositionPlaylist);

        return IMPValidator.isCPLConformed(cplPayloadRecord, headerPayloadRecords);
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

    private boolean shouldCheckCplConformance(File cpl) {
        // check for conformance only if the CPL has EssenceDescriptorList
        try {
            return new CompositionPlaylist(cpl, new IMFErrorLoggerImpl())
                    .getCompositionPlaylistType().getEssenceDescriptorList() != null;
        } catch (IOException | JAXBException | URISyntaxException | SAXException e) {
            return false;
        }
    }

}
