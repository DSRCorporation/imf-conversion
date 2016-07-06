# IMF Conversion Utility

## Build

The project can be built very easily by using the included Gradle wrapper. Having downloaded the sources, simply invoke the
following commands inside the folder containing the sources:

```
$ ./gradlew clean
$ ./gradlew build
```

## JDK requirements

The project can be built using JDK-8 only.

## Distribution

After the project is built, the distribution is created in the 'build' folder of the root project
(as well as in '/imf-conversion/build/distributions' folder)

## Usage

IMF Conversion utilities uses a number of external tools to perform full conversion cycle.
The external tools are not distributed with the Utility, and must be prepared by the user.
The user just specifies the executables in config.xml

### Conversion to DPP format

1. Get FFMPEG (https://ffmpeg.org/)

2. Get x264 Encoder (http://www.videolan.org/developers/x264.html)

3. Get BMX: bmx2raw and raw2bmx applications (https://sourceforge.net/projects/bmxlib/)

4. Get ASDCP tool. Use the one from (...) repository which is enhanced to work properly with TTML wrapped in MXF.
There is a Windows distribution there which can be used out of the box on Windows.

4. Modify the config.xml (a sample one can be found in '/samples' folder or in the distribution archive)
...
    <externalTools>
        <tool id="ffmpeg">ffmpeg</tool>
        <tool id="ffprobe">ffprobe</tool>
        <tool id="bmx">raw2bmxe</tool>
        <tool id="mxf2raw">mxf2raw</tool>
        <tool id="x264">x264</tool>
        <tool id="asdcp-unwrap">as-02-unwrap</tool>
    </externalTools>
...

5. The input IMF package and CPL as well as output directory (working directory) can be specified either in config.xml or as a command line argument.
...
    <imp>absolute path to imp</imp>
    <cpl>relative path of CPL.xml</cpl>
    <workingDirectory>output directory</workingDirectory>
...

6. Generate a sample Metadata.xml for the DPP format.
...
imf-conversion-main -f dpp -m metadata -o metadata.xml
...

7. Fill the Metadata.xml with custom values.

8. [Optional] Generate a sample audiomap.xml to map input audioi tracks and channels to the output ones depending on the AudioTrackLayput set in metadata.xml.
If no audiomap.xml is specified - then default mapping will be used.
   ...
   imf-conversion-main -f dpp -m metadata -o metadata.xml
   ...

9. Run the utility to perform conversion.

If IMP, CPL and working directory are set in config.xml, and no custom audiomap is needed, then run the following command:
...
imf-conversion-main -f dpp -c path-to/config.xml -m convert --metadata path-to/metadata.xml
...

If IMP, CPL and working directory are set in config.xml, and a custom audiomap is needed, then run the following command:
...
imf-conversion-main -f dpp -c path-to/config.xml -m convert --metadata path-to/metadata.xml --audiomap path-to/audiomap.xml
...

A full command if IMP, CPL, and working directory are specified via command line arguments
(the values from command line have higher priority than ones from config.xml):
...
imf-conversion-main -f dpp -c path-to/config.xml -m convert --imp path-to/imp --cpl CPL.xml -w path-to/working-sir --metadata path-to/metadata.xml --audiomap path-to/audiomap.xml
...