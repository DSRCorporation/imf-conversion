# Quick Start Guide for Mac OS X

Please see [README](README.md) for a full documentation.

>NOTE: Lines started with `$` represent commands need to be ran from console (terminal).

## Contents
* [Installation](#installation)
* [Usage](#usage)  
    * [DPP usage](#dpp-usage)
    * [iTunes usage](#itunes-usage)
* [Logs](#logs)

## Installation

1.  Download IMF Utility source code.

    IMF Utility sources hosted on GitHub.
    In order to use utility you can download either latest stable release or most actual source code in master branch.
    
    To download latest release:
    
    *   Go to [releases page](https://github.com/DSRCorporation/imf-conversion/releases)
    *   Find latest release 
    *   Click "Source code.zip" to download package
    
    To download most actual code:
    
    *   Go to [repository page](https://github.com/DSRCorporation/imf-conversion)
    *   Click "Clone or Download" 
    *   Choose "Download ZIP" to download package
    
    >Please ensure that selected branch is `master`.

    Find downloaded source in your _Downloads_ directory:
    ```
    $ cd /Users/{username}/Downloads/
    ```
    If you download archive extract its contents as specified below:
    ```
    $ unzip imf-conversion-{version}.zip
    ```
    
    >Please note, you can either copy or move downloaded sources to specified path for further installation.

2.  Get external tools needed for further installation.

    *   Install Xcode.
        
        Install latest Xcode from App Store.
        >Please note, Xcode version 7.3 or higher is required.

    *   Install Homebrew package manager.
    
        Run the following command to install brew:
        ```
        $ /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
        ```
        You may need to enter password during the installation process.
        
        If you already have brew run command below to update manager:
        ```
        $ brew update
        ```

3.  Build and install external tools needed for conversion.

    *   Install Java.
    
        Run the following command to install latest JDK:
        ```
        $ brew cask install java
        ```
        
        >Please note, required Java version is 8 or higher.
        
        If you already have Java prior to version 8 - remove it before installation by typing command:
        ```
        $ brew cask uninstall --force java
        ```
        To check Java version run command:
        ```
        $ java -version
        ```
        Then see output for details, second number of printed version must be greater than or equals 8.

    *   Install [FFMPEG](https://ffmpeg.org/).
    
        IMF Utility uses ffmpeg and ffprobe tools for video/audio conversion.
        
        Run the following command to install ffmpeg tools:
        ```
        $ brew install ffmpeg
        ```
    
    *   Install [x264](http://www.videolan.org/developers/x264.html) encoder.
    
        x264 encoder is used for DPP video encoding.
        
        Run the following command to install x264:
        ```
        $ brew install x264 --with-10-bit
        ```
        
        >Please note, IMF Utility requires 10-bit version.
        
        If you already have other version of x264 - remove it before installation by typing command:
        ```
        $ brew uninstall x264
        ```
        To check x264 bit depth run command:
        ```
        $ x264 --version
        ```
        Then see output for details, value associated with `--bit-depth` must be equal to 10.

    *   Build and install [Prenc](https://github.com/DSRCorporation/prores-encoder-mac/blob/master/README.md) encoder.
    
        Prenc is a tool providing native ProRes 422HQ encoding support on Mac.
        
        To download the source:
        
        *   Go to Prenc [repository page](https://github.com/DSRCorporation/prores-encoder-mac)
        *   Click "Clone or Download" 
        *   Choose "Download ZIP" to download package

        Find sources in _Downloads_ directory:
        ```
        $ cd /Users/{username}/Downloads/
        ```
        If you download archive - extract its content before the next step:
        ```
        $ unzip prores-encoder-mac-master.zip
        ```

        Go to directory with prenc source code:
        ```
        $ cd {prores-encoder-source}
        ```
        Run the following commands to build and install encoder:
        ```
        $ make
        $ make install
        ```
        
    *   Install [ASDCP](http://www.cinecert.com/asdcplib) tools.
    
        IMF Utility requires as-02-unwrap tool only built from [specified repository](https://github.com/DSRCorporation/asdcplib-as02).
        This is a fork from [ASDCP lib](http://www.cinecert.com/asdcplib/) which is enhanced to work properly with TTML wrapped in MXF.

        ASDCP needs OpenSSL and Expat parser to be installed.
        
        *   Install OpenSSL.
        
            Run the following command to install openssl:
            ```
            $ brew install openssl
            ```
        *   Install expat parser.
        
            Run the following command to install expat:
            ```
            $ brew install expat
            ```

        To download the source:
         
        *   Go to [repository page](https://github.com/DSRCorporation/asdcplib-as02)
        *   Click "Clone or Download"
        *   Choose "Download ZIP" to download package

        Find sources in _Downloads_ directory:
        ```
        $ cd /Users/{username}/Downloads/
        ```
        If you download archive - extract its contents before next step:
        ```
        $ unzip asdcplib-as02-master.zip
        ```

        Go to directory with asdcp source code:
        ```
        $ cd {asdcp-lib-source}
        ```
        Run the following commands to configure, build and install tools:
        ```
        $ ./configure --with-openssl=/usr/local/opt/openssl --with-expat=/usr/local/opt/expat --enable-as-02
        $ make
        $ make install
        ```
        
    *   Install [BMX lib](https://sourceforge.net/projects/bmxlib/) tools.
    
        IMF Utility uses raw2bmx and mxf2raw tools built from [proper snapshot](https://github.com/DSRCorporation/imf-conversion/tree/master/tools/bmx) provided with the utility.

        BMX needs uriparser and pkg-config to be installed.
        
        *   Install uriparser.
        
            Run the following command to install parser:
            ```
            $ brew install uriparser
            ```
        *   Install package config tool.
        
            Run the following command to install pkg-config:
            ```
            $ brew install pkg-config
            ```
            
        Go to directory with imf-conversion provided tools:
        ```
        $ cd {imf-conversion-source}/tools/
        ```
        To extract _tar.gz_ archive with snapshot run command:
        ```
        $ tar -xzvf bmx-snapshot-20150603.tar.gz
        ```
        
        Build libs on which BMX depends.
        
        *   Build libMXF
        
            Go to directory with libMXF sources:
            ```
            $ cd {bmx-snapshot-extracted}/libMXF
            ```
            Run the following commands to configure, build and install libs:
            ```
            $ ./configure
            $ make
            $ make install
            ```
        *   Build libMXF++
        
            Go to directory with libMXF++ sources:
            ```
            $ cd {bmx-snapshot-extracted}/libMXF++
            ```
            Run the following commands to configure, build and install libs:
            ```
            $ ./configure
            $ make
            $ make install
            ```
            
        Go to bmx source directory:
        ```
        $ cd {bmx-snapshot-extracted}/bmx
        ```
        Run the following commands to configure, build and install bmx tools:
        ```
        $ LDFLAGS=-L/usr/local/opt/uriparser/lib CFLAGS=-I/usr/local/opt/uriparser/include CXXFLAGS=-I/usr/local/opt/uriparser/include ./configure
        $ make
        $ make install
        ```
        
4.  Build IMF Utility source code.

    Go to source code directory:
    ```
    $ cd {imf-conversion-source}/
    ```
    Run the following command to build utility:
    ```
    $ ./gradlew clean build
    ```

    Now utility is built and ready to use. Please see usage sections of guide to learn how to execute conversion jobs.

## Usage

### DPP usage

Example below shows a possible usage of IMF Utility to make a simple conversion into DPP format.
>Please see [README](README.md) to find a complete list of available options and facilities.

1.  Go to directory with installed utility:
    ```
    $ cd {imf-conversion-source}/install/imf-conversion-utility
    ```
    
2.  Edit _sample/config.xml_ if needed.

    It can be used as is, if all external tools installed at step 1 are added to the `PATH`.
    >Please see [README](README.md) for a full description of _config.xml_.

3.  Edit _sample/dpp_metadata.xml_ if needed.

    Either enter correct duration for `PartDuration` and `TotalProgrammeDuration` fields, or use `00:00:00:00`.
    >Please note that all timestamps are in SMPTE timecode format (_hh:mm:ss:ff_), where _ff_ is in range [0,24] for 25 fps.
    >Please see [README](README.md) for a full description of _metadata.xml_.

    A sample _metadata.xml_ can be created by running the following command:
    ```
    $ bin/imf-conversion-utility dpp -m metadata -o metadata.xml
    ```
    
4.  Edit _sample/dpp_audiomap.xml_ if needed.
    >Please see [README](README.md) for a full description of _audiomap.xml_.

    A sample _audiomap.xml_ can be created by running the following command:
    ```
    $ bin/imf-conversion-utility dpp -m audiomap -o audiomap.xml
    ```
    
5.  Run conversion by executing the following command:
    ```
    $ bin/imf-conversion-utility dpp -c sample/config.xml --metadata sample/dpp_metadata.xml -o {output-name} --imp {path-to/imp} --cpl {cpl-name} -w {output-dir}
    ```
    If a specific audiomap must be used, then run:
    ```
    $ bin/imf-conversion-utility dpp -c sample/config.xml --metadata sample/dpp_metadata.xml --audiomap sample/dpp_audiomap.xml -o {output-name} --imp {path-to/imp} --cpl {cpl-name} -w {output-dir}
    ```
    
    >Please note, than `-w`, `--imp` and `--cpl` values can be set in _config.xml_ instead, `--cpl` path can be either absolute or relative to `--imp`.

6.  An output flat files are created under the specified `-w` directory and called '_{output-dir}/{output-name}.mxf_' and '_{output-dir}/{output-name}.stl_'.

7.  To get the whole set of possible options and formats, run the following command:
    ```
    $ bin/imf-conversion-utility dpp --help
    ```

### iTunes usage

Example below shows a possible usage of IMF Utility to make a simple conversion into iTunes format.
>Please see [README](README.md) to find a complete list of available options and facilities.

1.  Go to directory with installed utility:
    ```
    $ cd {imf-conversion-source}/install/imf-conversion-utility
    ```

2.  Edit _sample/config.xml_ if needed.

    Sample _config.xml_ doesn't contain path to Prenc tool needed for iTunes conversion, so please specify it manually by adding line
    ```xml
        <tool id="prenc">prenc </tool>
    ```
    in `<externalTools>` section of _config.xml_.
    >Please see [README](README.md) for a full description of _config.xml_.

3.  Generate and edit _metadata.xml_ if needed.
    >Please see [README](README.md) for a full description of _metadata.xml_.

    A sample _metadata.xml_ can be created by running the following command:
    ```
    $ bin/imf-conversion-utility itunes -m metadata -o metadata.xml
    ```
    By default, metadata will be generated for iTunes Film specification.
    Use `-p(--package-type)` option to generate metadata for iTunes Tv specification:
    ```
    $ bin/imf-conversion-utility itunes -m metadata -p tv -o metadata.xml
    ```

4.  Generate and edit _audiomap.xml_ if needed.
    >Please see [README](README.md) for a full description of _audiomap.xml_.
    
    A sample _audiomap.xml_ can be created by running the following command:
    ```
    $ bin/imf-conversion-utility itunes -m audiomap -o audiomap.xml
    ```

5.  Run conversion by executing the following command:
    ```
    $ bin/imf-conversion-utility itunes -c sample/config.xml --vendor-id {vendor-id} --imp {path-to/imp} --cpl {cpl} -w {output-dir}
    ```  
    If definite audiomap must be used, then run:
    ```
    $ bin/imf-conversion-utility itunes -c sample/config.xml --vendor-id {vendor-id} --audiomap {audiomap-path} --imp {path-to/imp} --cpl {cpl} -w {output-dir}
    ```
    If definite metadata must be used, then run:
    ```
    $ bin/imf-conversion-utility itunes -c sample/config.xml --vendor-id {vendor-id} --metadata {metadata-path} --imp {path-to/imp} --cpl {cpl} -w {output-dir}
    ```
    To provide conversion for specific format, use `-f(--format)` option:
    ```
    $ bin/imf-conversion-utility itunes -c sample/config.xml --vendor-id {vendor-id} -f hd1080p25 --imp {path-to/imp} --cpl {cpl} -w {output-dir}
    ```
    By default, conversion uses iTunes Film specification for metadata.
    To provide conversion for iTunes Tv specification use `-p(--package-type)` option:
    ```
    $ bin/imf-conversion-utility itunes -c sample/config.xml --vendor-id {vendor-id} -p tv --imp {path-to/imp} --cpl {cpl} -w {output-dir}
    ```
    
    >Please note, than `-w`, `--imp` and `--cpl` values can be set in _config.xml_ instead, `--cpl` path can be either absolute or relative to `--imp`.
    
6.  An output iTunes package is created under the specified `-w` directory and called '_{output-dir}/{vendor-id}.itmsp_'.

7.  To get the whole set of possible options and formats, run the following command:
    ```
    $ bin/imf-conversion-utility itunes --help
    ```

## Logs

1.  The current conversion job log file: _/logs/imf-utility.log_.
2.  Previous conversion jobs log files: _/logs/archive_.
3.  External tools log files: _{output-dir}/logs_.
