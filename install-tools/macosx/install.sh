#!/bin/sh

# 1. install brew
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

# 2. install java (JDK-8 is required)
brew cask install java

# 3. install ffmpeg
brew install ffmpeg

# 4. install x264-10bit
brew reinstall x264 --with-10-bit

# 5.1 install uriparser (needed for bmx)
brew install uriparser

# 5.2 install open ssl (needed for as-02-unwrap)
brew install openssl

# 6. Extract needed tools (raw2bmx, mxf2raw, as-02-unwrap)
tar -xzvf tools.tar.gz -C /

