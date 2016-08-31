/*
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
package com.netflix.imfutility.itunes.chapters;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.generated.itunes.chapters.InputChapterItem;
import com.netflix.imfutility.generated.itunes.chapters.InputChapterList;
import com.netflix.imfutility.generated.itunes.chapters.ObjectFactory;
import com.netflix.imfutility.itunes.chapters.builder.ChaptersXmlSampleBuilder;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.CHAPTERS_PACKAGE;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.CHAPTERS_XML_SCHEME;

/**
 * Provides functionality to generate empty chapters.xml for iTunes format and to manage chapter assets.
 */
public final class ChaptersXmlProvider {

    private final InputChapterList chapters;
    private final File baseDir;
    private Map<InputChapterItem, File> chaptersAssetMap;


    public ChaptersXmlProvider(File chaptersFile) throws FileNotFoundException, XmlParsingException {
        this.chapters = loadChapters(chaptersFile);
        this.baseDir = new File(chapters.getBasedir());

        mapChaptersAssets();
        checkChaptersAssets();
    }

    private InputChapterList loadChapters(File chaptersFile) throws FileNotFoundException, XmlParsingException {
        if (!chaptersFile.isFile()) {
            throw new FileNotFoundException(String.format(
                    "Invalid chapters.xml file: '%s' not found", chaptersFile.getAbsolutePath()));
        }
        return XmlParser.parse(chaptersFile, new String[]{CHAPTERS_XML_SCHEME}, CHAPTERS_PACKAGE, InputChapterList.class);
    }

    public List<InputChapterItem> getChapters() {
        return chapters.getInputChapter();
    }

    public String getTimecodeFormat() {
        return chapters.getTimecodeFormat();
    }

    public File getChapterFile(InputChapterItem chapter) {
        return chaptersAssetMap.get(chapter);
    }

    private void mapChaptersAssets() {
        chaptersAssetMap = chapters.getInputChapter().stream()
                .limit(99)
                .collect(Collectors.toMap(Function.identity(), this::createChapterFile));
    }

    private void checkChaptersAssets() {
        if (!chaptersAssetMap.values().stream().allMatch(File::isFile)) {
            throw new ConversionException("Chapters.xml contains not existing files");
        }
    }

    private File createChapterFile(InputChapterItem chapter) {
        return new File(baseDir, chapter.getFileName());
    }

    /**
     * Generates a sample chapters.xml file.
     *
     * @param path a path to the output metadata.xml file
     */
    public static void generateSampleXml(String path) {
        generateSampleXml(new File(path));
    }

    /**
     * Generates a sample metadata.xml file.
     *
     * @param file metadata.xml file
     */
    public static void generateSampleXml(File file) {
        marshallChapters(ChaptersXmlSampleBuilder.buildInputChapters(), CHAPTERS_XML_SCHEME, file);
    }

    /**
     * Create marshaller based on schema for iTunes specific metadata format.
     *
     * @param jaxbContext context
     * @param schemaPath  path to schema
     * @return marshaller that supports iTunes schema validation
     * @throws SAXException
     * @throws JAXBException
     */
    private static Marshaller createMarshaller(JAXBContext jaxbContext, String schemaPath) throws SAXException, JAXBException {
        Schema schema = XmlParser.getSchema(new String[]{schemaPath});

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setSchema(schema);
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        return jaxbMarshaller;
    }

    /**
     * Marshall chapters.
     *
     * @param chapters   package to marshall
     * @param schemaPath path to schema
     */
    private static void marshallChapters(InputChapterList chapters, String schemaPath, File file) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(InputChapterList.class);
            Marshaller jaxbMarshaller = createMarshaller(jaxbContext, schemaPath);

            JAXBElement<InputChapterList> chaptersJaxb = new ObjectFactory().createInputChapters(chapters);
            jaxbMarshaller.marshal(chaptersJaxb, file);
        } catch (SAXException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}

