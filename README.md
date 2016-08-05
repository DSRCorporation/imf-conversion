# IMF Conversion Utility

* IMF media conversion utility allows to handle flat file creation from a specified CPL within the IMF package.
* Currently conversion to BBC DPP format only is supported ([DPP](https://www.digitalproductionpartnership.co.uk/), [BBC](http://dpp-assets.s3.amazonaws.com/wp-content/uploads/specs/bbc/TechnicalDeliveryStandardsBBC.pdf)).
 
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
 
 

#### Prepare external tools used for conversion

1. Get FFMPEG [FFMPEG](https://ffmpeg.org/)

2. Get x264 Encoder [x264](http://www.videolan.org/developers/x264.html). 10-bit version must be used!

3. Get BMX: bmx2raw and raw2bmx applications [BMX](https://sourceforge.net/projects/bmxlib/).

4. Get ASDCP tool. Use the one from (TBD) repository which is enhanced to work properly with TTML wrapped in MXF.
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

#### Generate audiomap.xml

* Audiomap.xml is used to map input IMF virtual audio tracks and channels to the output ones depending on the _AudioTrackLayout_ set in metadata.xml.
* Please see Section 4.4.1 in [BBC](http://dpp-assets.s3.amazonaws.com/wp-content/uploads/specs/bbc/TechnicalDeliveryStandardsBBC.pdf) for the description of different layouts.
* If no audiomap.xml is specified, then default mapping will be used (each input IMF virtual audio track/channel will be mapped  to the output ones subsequently; remaining output tracks will be filled with silence).
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
    * __dpp-conversion__
        * A plugin to perform conversion to BBC DPP format.
        * Depends on imf-conversion-core and dpp-conversion-input-xsd
    * __dpp-conversion-input-xsd__
        * A plugin containing XSDs for DPP format input XML files (metadata.xml, audiomap.xml).
        * It's separated from _dpp_conversion_ as there are other projects that require DPP metadata.xml (for example, ttml-to-stl).
    * __ttml-to-stl__
       * An independent project to perform TTML to EBU STL caption conversion.
        * It's used as a default caption conversion tool by DPP plugin.
        * The project fat jar (ttml-to-stl.jar) is copied into the 'tools' folder within delivery.
    * __imf-validation__
        * An independent project to IMF package validation.
        * Uses Photon under the hood ([Photon](https://github.com/Netflix/photon)).
        * It's used as a default IMF validation tool.
        * The project fat jar (imf-validation.jar) is copied into the 'tools' folder within delivery.

  
            