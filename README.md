# IMF Conversion Utility

* IMF media conversion utility allows to handle flat file creation from a specified CPL within the IMF package.
* Please find Mac OS X installation and usage notes in [Getting Started OS X Guide](getting_started_osx.md).

## Contents
* [Build](#build)
* [JDK requirements](#jdk-requirements)
* [Distribution](#distribution)
* [Usage](#usage)
    * [Conversion to DPP format](#conversion-to-dpp-format)
    * [Conversion to iTunes format](#conversion-to-itunes-format)
* [Project Structure](#project-structure)

## Build

The project can be built very easily by using the included Gradle wrapper. Having downloaded the sources, simply invoke the following commands inside the folder containing the sources:

```
$ ./gradlew clean
$ ./gradlew build
```

## JDK requirements

* The project can be built using JDK-8 only (not JRE!).
* JAVA_HOME must be set and 'java' must be in the PATH.  

## Distribution

After the project is built, the distribution is created in the 'build' folder of the root project
```
./build/imf-conversion-utility-{version}.tar
./build/imf-conversion-utility-{version}.zip
```
as well as in
```
./imf-conversion-main/build/distributions
```

The distribution includes 
1. All necessary .jar files in the _lib_ folder (_lib/imf-conversion-utility-{version}.jar_ contains the Main Class). 
2. Start scripts in the _bin_ folder.
3. Default tools used by the utility in the _tools_ folder.
4. Sample _config.xml_ in _samples_ folder.
5. License.
6. README
7. Sample metadata.xml and audiomap.xml in _samples_ folder.

## Usage

* IMF Conversion utilities uses a number of external tools to perform full conversion cycle.
* The external tools are not distributed with the Utility, and must be prepared by the user.
* The user just specifies the executables in config.xml.
* The utility creates tmp files (which will be deleted when the conversion job finishes). So make sure that there is enough free disk space.

### Conversion to DPP format

1. [Done Once] Prepare external tools used for conversion.
2. [Done Once; edit when needed] Prepare config.xml.
3. Run Utility with appropriate command line arguments to generate metadata.xml to enter DPP metadata values.
    * dpp
    * --mode [-m] metadata
    * --output [-o] metadata.xml
4. Enter required DPP metadata values manually ([DPP_MMS_TechMetadata](http://www.amwa.tv/downloads/specifications/DPP_MMS_TechMetadata_V1.1_2013-10-08.xls)).
5. [Optional] Run Utility with appropriate command line arguments to generate audiomap.xml to map input audio tracks and channels to the output ones depending on the _AudioTrackLayout_ parameter set in metadata.xml.
    * dpp
    * --mode [-m] audiomap
    * --output [-o] audiomap.xml
6. [Optional] Edit audiomap.xml to map input audio streams and channels. 
7. Run conversion job:
    * dpp
    * --mode [-m] convert (may be omitted as it's a default mode)
    * --config [-c] config.xml
    * --metadata metadata.xml
    * --audiomap audiomap.xml (optional)
    * --imp path-to-imp-folder (optional: can be set in config.xml)
    * --cpl CPL.xml (optional: can be set in config.xml)
    * --working-dir [-w] path-to-output-folder (optional: can be set in config.xml)
    * -- output [-o] the output .mxf file name (as well as .stl file name) without extension (optional: if not set, 'output' default name will be used).
8. An output flat file is created under the specified output directory (-w) and is called _output.mxf_.
9. Logs:
    * The current conversion job log file: _/logs/imf-utility.log_.
    * Previous conversion jobs log files: _/logs/archive_.
    * External tools log files: _{output-dir}/logs_.
10. Validation
    * To validate DPP metadata the official [Metadata Application](https://www.digitalproductionpartnership.co.uk/download/metadataappdownload/) tool can be used.
 
Note 1:

 * DPP format requires special Program Layout as defined in Section 4.5 of [BBC](http://dpp-assets.s3.amazonaws.com/wp-content/uploads/specs/bbc/TechnicalDeliveryStandardsBBC.pdf).
 * Currently the Utility doesn't insert additional elements from 09.59.30.00 till 10.00.00.00 (Bars, clocks, lineup tone) and doesn't freezes the last frame for 5 seconds.
 * The Utility just sets the start time to 09.59.30.00.
 * So, in order to have a correct program layout, the user must insert required elements on IMF level (that is add segments with required video and audio data):
     * Start:
         * 20 seconds of 100% Bars on the Video with EBU Lineup tones on the audio; 
         * 7 seconds of black and silence;
         * 5 frames of slate (white text on black background) with silence on audio;
         * 2 seconds and 19 frames of black and silence;
     * End of program:    
         * 5 seconds of freeze frame from last program video frame with silence.

#### Prepare external tools used for conversion

1. Get FFMPEG [FFMPEG](https://ffmpeg.org/)

2. Get x264 Encoder [x264](http://www.videolan.org/developers/x264.html). 10-bit version must be used!

3. Get BMX: bmx2raw and raw2bmx applications from tools directory. tools/bmx directory  contains proper [BMX tools](https://sourceforge.net/projects/bmxlib/)
snapshots because latest BMX tools include MCA implementation and if we have incorect MXF audio file (without MCA
label id property) mxf2raw exits with exception and does not extract audio data.

4. Get ASDCP tool. Please build it from https://github.com/DSRCorporation/asdcplib-as02 repository.
This is a fork from from http://www.cinecert.com/asdcplib/ which is enhanced to work properly with TTML wrapped in MXF.
There is a Windows distribution there which can be used out of the box on Windows (TBD).

#### Prepare config.xml

1. A sample _config.xml_ can be found in the _/samples_ folder.
 
2. Required External Tools
    * Specify external tools to be used.
    * If an executable is added to the PATH, then a short name can be used. Otherwise enter a full absolute path.  
```
    <externalTools>
            <tool id="ffmpeg">ffmpeg</tool>
            <tool id="ffprobe">ffprobe</tool>
            <tool id="bmx">raw2bmx</tool>
            <tool id="mxf2raw">mxf2raw</tool>
            <tool id="x264">x264-10bit</tool>
            <tool id="asdcp-unwrap">as-02-unwrap</tool>
    </externalTools>
```

3. Optional External Tools
    * There is a number of external tools which are distributed with the utility and used by default (no user settings required)
        * IMF validation (_/imf-validation_ project which as a wrapper on Photon tool ([Photon](https://github.com/Netflix/photon))).
        * TTML to EBU STL captions converter (_/ttml-to-stl_).
    * However, it's possible to use another external tools for these tasks.
    * If another tools are used, then either they should expect the same input and output as the default tools, or the code should be modified to support the new tools.
    * To specify custom IMF validation and TTML to STL conversion tools, add the following to the _config.xml_: 
```
    <externalTools>
            ...
            <tool id="ttml-to-stl">ttmlToStl path</tool>
            <tool id="imf-validation">imf validation path</tool>
    </externalTools>
```

4. IMF package and CPL
    * The input IMF package and CPL can be specified either in _config.xml_ or as a command line argument.
    * If it's specified in both places, then values from command line parameters will be used.
    * Including IMF package and CPL into _config.xml_ can be used to reduce the number of command line arguments when using the same IMP and CPL for different conversions.
    * To set IMF package and CPL in _config.xml_, add the following:    
```
    <imp>path to imp</imp>
    <cpl>path of CPL.xml</cpl>
```
    * Please note that the CPL can be specified either as a path to CPL.xml or as a name of CPL.xml within IMP folder (an IMF package may contain several CPLs, so we should select the one to be used for conversion).

5. Output directory
    * The output directory is a folder where the output flat file (_output.mxf_) will be created.
    * The output directory also contains tmp files created during conversion as well as logs of all external tools.
    * It can be controlled via _config.xml_ whether to clear the output directory at the beginning of conversion job, and whether to delete all tmp files at the end of the job.
    * The output directory can be specified either in _config.xml_ or as a command line argument.
    * If it's specified in both places, then values from command line parameters will be used.
    * To set the output directory in _config.xml_, add the following:    
```
    <workingDirectory>path to output directory</workingDirectory>
```

6. Allows/disallows silent conversion 
    * Input audio/video parameters may not match the required output ones (for example, input fps is 30, but the output must be 25).
    * The user can control conversion of a parameter to the required value.
    * By default, all conversions are allowed, and no special settings are needed.
    * To allow/disallow conversion, do the following for each parameter to be controlled:
```
    <conversionParameters>
            <param name="bitDepth">yes</param>
            <param name="frameRate">no</param>
            <param name="size">yes</param>
            <param name="pixelFormat">yes</param>
            <param name="bitsSample">yes</param>
            <param name="sampleRate">no</param>
    </conversionParameters>
```
    * If the parameter is not specified, then conversion is allowed.

7. Clear Output directory
    * It can be controlled via _config.xml_ whether to
        * clear the output directory at the beginning of conversion job
            * false by default
            * be careful with setting it to true to not delete important files!
        * delete all tmp files at the end of the job when the job finished successfully
            * true by default
            * _{output-dir}/logs_ folder with the logs of all external processed is not deleted
        * delete all tmp files at the end of the job when the job finished with an error
            * false by default
            * useful to find the reason of fail
    * To control these options in _config.xml_, add the following:
```
    <cleanWorkingDir>false</cleanWorkingDir>
    <deleteTmpFilesOnExit>true</deleteTmpFilesOnExit>
    <deleteTmpFilesOnFail>false</deleteTmpFilesOnFail>
```

8. It can be configured whether to perform IMF validation of the input IMF package (Photon is used by default for validation).
    * It's true by default
    * It may be set to false for some test reasons
    * To control this option in _config.xml_, add the following:
```
    <validateImf>false</validateImf>
```

#### Generate metadata.xml

* Metadata.xml is needed to enter the DPP metadata values needed for conversion and the output container ([DPP_MMS_TechMetadata](http://www.amwa.tv/downloads/specifications/DPP_MMS_TechMetadata_V1.1_2013-10-08.xls)).
* The following command generates a sample metadata.xml
    
```
imf-conversion-utility dpp -m metadata -o metadata.xml
```

* The user can enter the values and they will be applied for the output MXF.
* The metadata.xml is a required parameter for conversion to DPP format.
* Please note that all timestamps are in SMPTE timecode format (hh:mm:ss:ff), where ff is in range [0,24] for 25 fps.

#### Generate audiomap.xml

* Audiomap.xml is used to map input IMF virtual audio tracks and channels to the output ones depending on the _AudioTrackLayout_ set in metadata.xml.
* Please see Section 4.4.1 in [BBC](http://dpp-assets.s3.amazonaws.com/wp-content/uploads/specs/bbc/TechnicalDeliveryStandardsBBC.pdf) for the description of different layouts.
* If no audiomap.xml is specified, then default mapping will be used:
     * If there is an Essence Descriptor in CPL.xml associated with each audio resource, and the Essence Descriptor defines the same 
     same audio channel layout for each resource within a virtual track, then the audio mapping will be done according to the provided channel layout.
         * R48:2a: there must be at least one stereo virtual track (if there are more than one stereo virtual tracks - the first will be used);
         * R123:4b/c: there must be one or two stereo virtual track (not more);
         * R123:16c: there must be one or two stereo and one or two 5.1 virtual tracks (not more);
         * R123:16d: there must be exactly two 5.1 virtual tracks with different languages;
         * R123:16f: there must be exactly three stereo virtual tracks with different languages;
     * If it's not possible to guess audio mapping as described above (from channel layout defined in Essence Descriptors),
      then each input IMF virtual audio track/channel will be mapped to the output ones subsequently; remaining output tracks will be filled with silence.
         * Example for R123:16c track allocation and 3 stereo audio virtual tracks:
             * VirtualTrack 1 - Left ===> Audio track 1 (St. Final mix L)
             * VirtualTrack 1 - Right ===> Audio track 2 (St. Final mix R)
             * VirtualTrack 2 - Left ===> Audio track 3 (St. M&E L)
             * VirtualTrack 2 - Right ===> Audio track 4 (St. M&E R)
             * VirtualTrack 3 - Left ===> Audio track 5 (5.1 Final Mix L)
             * VirtualTrack 3 - Right ===> Audio track 6 (5.1 Final Mix R)
             * All other audio tracks (7 - 16) - silence 
* The following command generates a sample audiomap.xml
```
   imf-conversion-utility dpp -m audiomap -o audiomap.xml
```
* The number of _EBUTrack_ nodes must correspond to the number of output audio tracks defined by the selected layout 
    * 4 for R48:2a and R123:4b layouts;
    * 16 for other layouts.
* _EBUTrack_ defines an input channel (of a CPL virtual track, or CPL audio sequence) to be used for the output audio track.
* _CPLVirtualTrackId_ must point to a _\<TrackId\>_ attribute of a virtual track (audio sequence) within CPL.xml.
* _CPLVirtualTrackChannel_ is a channel number (starting from 1) within a virtual track specified above.
* If either _CPLVirtualTrackId_ or _CPLVirtualTrackChannel_ is absent, then the output track will be silence. 
* An example of mapping of two Stereo input Audio Virtual tracks tracks to _'R123: 4b'_ layout (4 output tracks, Stereo with M&E):
    * VirtualTrack 1 (urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711) - Right ===> Audio track 1 (St. Final mix L)
    * VirtualTrack 1 (urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711) - Left ===> Audio track 2 (St. Final mix R)
    * VirtualTrack 2 (urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712) - Left ===> Audio track 3 (St. M&E L)
    * VirtualTrack 2 (urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712) - Right ===> Audio track 4 (St. M&E R)
    
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<AudioMap xmlns="http://audiomap.dpp.imfutility.netflix.com">
    <EBUTrack>
        <Number>1</Number>
        <CPLVirtualTrackId>urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711</CPLVirtualTrackId>
        <CPLVirtualTrackChannel>2</CPLVirtualTrackChannel>
    </EBUTrack>
    <EBUTrack>
        <Number>2</Number>
        <CPLVirtualTrackId>urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711</CPLVirtualTrackId>
        <CPLVirtualTrackChannel>1</CPLVirtualTrackChannel>
    </EBUTrack>
    <EBUTrack>
        <Number>3</Number>
        <CPLVirtualTrackId>urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712</CPLVirtualTrackId>
        <CPLVirtualTrackChannel>1</CPLVirtualTrackChannel>
    </EBUTrack>
    <EBUTrack>
        <Number>4</Number>
        <CPLVirtualTrackId>urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712</CPLVirtualTrackId>
        <CPLVirtualTrackChannel>2</CPLVirtualTrackChannel>
    </EBUTrack>
</AudioMap>
```

#### Run Conversion Job

* If IMP, CPL and working directory are set in config.xml, and no custom audiomap is needed, then run the following command:
```
imf-conversion-utility dpp -c path-to/config.xml -m convert --metadata path-to/metadata.xml -o outputName
```
* If IMP, CPL and working directory are set in config.xml, and a custom audiomap is needed, then run the following command:
```
imf-conversion-utility dpp -c path-to/config.xml -m convert --metadata path-to/metadata.xml --audiomap path-to/audiomap.xml -o outputName
```

* A full command if IMP, CPL, and working directory are specified via command line arguments (the values from command line override values from config.xml):
```
imf-conversion-utility dpp -c path-to/config.xml -m convert --imp path-to/imp --cpl CPL.xml -w path-to/working-dir --metadata path-to/metadata.xml --audiomap path-to/audiomap.xml -o outputName
```

#### Output and Logs

* An output flat file and captions files are created under the specified output directory (-w) and called _{outputName}.mxf_ and _{outputName}.stl_, where {outputName} is a value of --output (-o) parameter.
 If --output parameter is not set, then _output.mxf_ and _output.stl_ values are used.
      ```
      {output-directory}/{outputName}.mxf
      ```
      ```
      {output-directory}/{outputName}.stl
      ```
      or
      ```
      {output-directory}/output.mxf
      ```
      ```
      {output-directory}/output.stl
      ```

* The current conversion job log file:
     ```
      /logs/imf-utility.log
     ```
* Previous conversion jobs log files:
      ```
      /logs/archive/
      ```
* External tools log files
       ```
       {output-directory}/logs
       ```
       
### Conversion to iTunes format

1. [Done Once] Prepare external tools used for conversion.
2. [Done Once; edit when needed] Prepare config.xml.
3. [Optional] Run Utility with appropriate command line arguments to generate metadata.xml to enter iTunes metadata values.
    * itunes
    * --mode [-m] metadata
    * --output [-o] metadata.xml
4. Enter required  metadata values manually ([Tunes Film Package](http://help.apple.com/itc/filmspec/) or [Tunes TV Package](https://help.apple.com/itc/tvspec/)) or specify custom 
metadata.xml file. After conversion done metadata.xml of resulting iTunes package can be invalid (missing some required data) and will require manually changes.
5. [Optional] Run Utility with appropriate command line arguments to generate audiomap.xml to map input audio tracks and channels to the output ones.
    * itunes
    * --mode [-m] audiomap
    * --output [-o] audiomap.xml
6. [Optional] Edit audiomap.xml to map input audio streams and channels. If audiomap will not be set then default
   audiomap will be used basis on essence descriptions if exist or by natural order.
7. Run conversion job:
    * itunes
    * --mode [-m] convert (may be omitted as it's a default mode)
    * --config [-c] config.xml
    * --metadata metadata.xml
    * --audiomap audiomap.xml (optional)
    * --imp path-to-imp-folder (optional: can be set in config.xml)
    * --cpl CPL.xml (optional: can be set in config.xml)
    * --working-dir [-w] path-to-output-folder (optional: can be set in config.xml)
    * --vendor-id vendor identifier
    * --package-type [-p] iTunes package type film (default) or tv (optional)
    * --format [-f] video format (optional)
    * --trailer trailer asset location (optional, processed only for "film" package type)
    * --poster poster asset location (optional)
    * --chapters chapters.xml location (optional, processed only for "film" package type)
    * --cc a path to external closed captions (optional)
    * –-sub a paths to external subtitles (optional, processed only for "film" package type) 
    * --fallback-locale main locale for iTunes package (optional, will be used if CPL or metadata locale is not set)    
8. An output iTines package is created under the specified output directory (-w) and is called _vindor-id.itmsp_.
9. Logs:
    * The current conversion job log file: _/logs/imf-utility.log_.
    * Previous conversion jobs log files: _/logs/archive_.
    * External tools log files: _{output-dir}/logs_.
10. Validation
    * To validate iTunes package against XML schema of metadata.xml official iTunes [Transporter](http://help.apple.com/itc/transporteruserguide/) tool can be used. Transporter can 
    validate assets too but need corresponding iTunes Connect account credentials.
 
Note 1:

 * iTunes Package format requires special Audio Layout as defined in [iTunes Asset Guide](https://help.apple.com/itc/videoaudioassetguide/) for corresponding package type.
 * Supported video formats can be found in wiki or documentation (docs/).

#### Prepare external tools used for conversion

iTunes version of the utility on OS X used native Apple ProRes encoder that provided by PrEnc utility that can be found at [GitHub PrEnc pero](https://github.com/DSRCorporation/prores-encoder-mac). Download PrEnc [sources](https://github.com/DSRCorporation/prores-encoder-mac/archive/master.zip) and follow installation instructions from README.md:
 * open the Terminal application
 * download sources
 
 ```
 $ wget https://github.com/DSRCorporation/prores-encoder-mac/archive/master.zip
 ```
 * unzip the archive
 
 ```
 $ unzip master.zip
 ```
 * change dir to unpacked source directory
 
 ```
 $ cd prores-encoder-mac-master
 ```
 * run make command
 
 ```
 $ make RELEASE=1
 ```
 * install
 
 ```
 sudo make install
 ```

Other tools installation is the same tools as for [DPP tools](#prepare-external-tools-used-for-conversion).

#### Prepare config.xml

The same process as for [DPP config preparation](#prepare-config-xml).

#### Generate metadata.xml

* Metadata.xml is needed to enter the iTunes package metadata.
* Metadata can be fixed in any time when iTunes package done according to correct values
* The following command generates a sample metadata.xml
    
```
imf-conversion-utility itunes -m metadata -o metadata.xml
```

#### Generate audiomap.xml

* Audiomap.xml is used to map input IMF virtual audio tracks and channels to the output ones.
* Please see [iTunes Asset Guide](http://help.apple.com/itc/videoaudioassetguide/) to set correct audio options.
* If no audiomap.xml is specified, then default mapping will be used:
     * If there is an Essence Descriptor in CPL.xml associated with each audio resource, and the Essence Descriptor defines the same 
     same audio channel layout for each resource within a virtual track, then the audio mapping will be done according to the provided channel layout.
     * If it's not possible to guess audio mapping as described above (from channel layout defined in Essence Descriptors),
      then each input IMF virtual audio track/channel will be mapped to the output ones subsequently. In this case only
      main audio track will be created.
* The following command generates a sample audiomap.xml
```
   imf-conversion-utility itunes -m audiomap -o audiomap.xml
```
* _mainAudio_ defines a main audio track and support only the following channel layouts (options):
    * Option1a
    * Option2
    * Option3
    * Option4
    * Option5
    * Option6
* _alternativeAudio_ defines alternative audio tracks. Only one track for each locale and supports only the following
  channel layouts (options):
    * Option5
    * Option6
* _CPLVirtualTrackId_ must point to a _\<TrackId\>_ attribute of a virtual track (audio sequence) within CPL.xml.
* _CPLVirtualTrackChannel_ is a channel number (starting from 1) within a virtual track specified above.
* An example below represents of mapping Option6 for main audio with channel layout from essence descriptor if exist or
  by natural order and one an alternative track with the same Option6 but with directly specified channel mapping:
    * VirtualTrack 1 (urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711) - Left ===> Audio track 1 - Left (L)
    * VirtualTrack 1 (urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711) - Right ===> Audio track 1- Right (R)
    
```
<?xml version="1.0" encoding="UTF-8"?>
<audiomap xmlns="http://netflix.com/imf/itunes/audiomap">
    <mainAudio locale="de-DE" name="main-audio.mov">
        <Option6/>
    </mainAudio>

    <alternativeAudio locale="ja" name="audio_JA.mov">
        <Option6>
            <Track1>
                <L>
                    <CPLVirtualTrackId>urn:uuid:850bd841-5c4a-4b02-b853-5ce1afbb3629</CPLVirtualTrackId>
                    <CPLVirtualTrackChannel>1</CPLVirtualTrackChannel>
                </L>
                <R>
                    <CPLVirtualTrackId>urn:uuid:850bd841-5c4a-4b02-b853-5ce1afbb3629</CPLVirtualTrackId>
                    <CPLVirtualTrackChannel>2</CPLVirtualTrackChannel>
                </R>
            </Track1>
        </Option6>
    </alternativeAudio>
</audiomap>
```

#### Run Conversion Job

The same process as for [DPP run conversion](#run-conversion-job) except that output is a iTunes store package with
<vendor-id>.itmsp name.

#### Output and Logs

The same as for [DPP](output-and-logs).
       
## Project Structure

* The project is a Java project.
* JDK-8 is required.
* The project is a Gradle multi-project.
* Findbugs and Checktyle are used for code quality.
* It contains the following sub-projects:
    * __imf-conversion-main (imf-conversion-utility)__
        * The main project.
        * Contains the main class.
        * Contains conversion.xml which describes external tools to be used for conversion for each format.
        * Contains log4j configuration.
        * Creates a final distribution.
        * Depends on conversion plugins to be included into the distribution.
    * __imf-conversion-common__
        * Contains logic common for all Java projects.
    * __imf-conversion-core__
        * Main IMF utility logic base for all formats.
        * Contains CPL processing logic.
        * Calls IMF validation.
        * Contains the logic of parsing conversion.xml and executing external tools.
        * A base project for all plugins.
    * __imf-essence-descriptors__
        * An independent project that contains JAXB classes of IMF Essence schemas.
    * __dpp-conversion__
        * A plugin to perform conversion to BBC DPP format.
        * Depends on imf-conversion-core and dpp-conversion-input-xsd
    * __dpp-conversion-input-xsd__
        * A plugin containing XSDs for DPP format input XML files (metadata.xml, audiomap.xml).
        * It's separated from _dpp_conversion_ as there are other projects that require DPP metadata.xml (for example, ttml-to-stl).
    * __itunes-conversion__
        * A plugin to perform conversion to iTunes Package format.
        * Depends on imf-conversion-core, itunes-conversion-input-xsd, itunes-metadata-film and itunes-metadata-tv
    * __itunes-conversion-input-xsd__
        * A plugin containing XSDs for iTunes format input XML files (metadata.xml, audiomap.xml).
    * __itunes-metadata-film__
        * An independent project that contains JAXB classes of iTunes Film Package 5.2 XML schema.
    * __itunes-metadata-tv__
        * An independent project that contains JAXB classes of iTunes TV Package 5.2 XML schema.
    * __ttml2itt__
        * An independent project to perform TTML to iTunes iTT subtitle conversion.
        * It's used as a default subtitle conversion tool by iTunes plugin.
        * The project fat jar (ttml2itt.jar) is copied into the 'tools' folder within delivery.
        * Depends on ttml-java
    * __ttml-java__
        * An independent project that contains JAXB classes of ttml-cr-ttaf1-20100223 TTML schemas.
    * __ttml-to-stl__
        * An independent project to perform TTML to EBU STL caption conversion.
        * It's used as a default caption conversion tool by DPP plugin.
        * The project fat jar (ttml-to-stl.jar) is copied into the 'tools' folder within delivery.
    * __imf-validation__
        * An independent project to IMF package validation.
        * Uses Photon under the hood ([Photon](https://github.com/Netflix/photon)).
        * It's used as a default IMF validation tool.
        * The project fat jar (imf-validation.jar) is copied into the 'tools' folder within delivery.

  
