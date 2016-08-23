@echo off
pushd %~dp0

:: hd

:: 1080
call .\chimera25-format %1 %2 "hd1080p30"     %3
call .\chimera25-format %1 %2 "hd1080i2997"   %3
call .\chimera25-format %1 %2 "hd1080i2997"   %3
call .\chimera25-format %1 %2 "hd1080p2997"   %3
call .\chimera25-format %1 %2 "hd1080p25"     %3
call .\chimera25-format %1 %2 "hd1080p24"     %3
call .\chimera25-format %1 %2 "hd1080i23976"  %3
call .\chimera25-format %1 %2 "hd1080p23976"  %3
:: 720
call .\chimera25-format %1 %2 "hd720p30"      %3
call .\chimera25-format %1 %2 "hd720i2997"    %3
call .\chimera25-format %1 %2 "hd720p2997"    %3
call .\chimera25-format %1 %2 "hd720p25"      %3
call .\chimera25-format %1 %2 "hd720p24"      %3
call .\chimera25-format %1 %2 "hd720i23976"   %3
call .\chimera25-format %1 %2 "hd720p23976"   %3

:: sd-tv

:: ntsc
call .\chimera25-format %1 %2 "sdtvntsc480i2997"  %3
call .\chimera25-format %1 %2 "sdtvntsc480p2997"  %3
call .\chimera25-format %1 %2 "sdtvntsc480p24"    %3
call .\chimera25-format %1 %2 "sdtvntsc480p23976" %3
:: pal
call .\chimera25-format %1 %2 "sdtvpal576p25"     %3
call .\chimera25-format %1 %2 "sdtvpal576p24"     %3
call .\chimera25-format %1 %2 "sdtvpal576p23976"  %3

:: sd-film

:: ntsc
call .\chimera25-format %1 %2 "sdfilmntsc480i2997_16_9"   %3
call .\chimera25-format %1 %2 "sdfilmntsc480p2997_16_9"   %3
call .\chimera25-format %1 %2 "sdfilmntsc480p24_16_9"     %3
call .\chimera25-format %1 %2 "sdfilmntsc480p23976_16_9"  %3
call .\chimera25-format %1 %2 "sdfilmntsc480i2997_4_3"    %3
call .\chimera25-format %1 %2 "sdfilmntsc480p2997_4_3"    %3
call .\chimera25-format %1 %2 "sdfilmntsc480p24_4_3"      %3
call .\chimera25-format %1 %2 "sdfilmntsc480p23976_4_3"   %3
:: pal
call .\chimera25-format %1 %2 "sdfilmpal576p25_16_9"      %3
call .\chimera25-format %1 %2 "sdfilmpal576p24_16_9"      %3
call .\chimera25-format %1 %2 "sdfilmpal576p23976_16_9"   %3
call .\chimera25-format %1 %2 "sdfilmpal576p25_4_3"       %3
call .\chimera25-format %1 %2 "sdfilmpal576p24_4_3"       %3
call .\chimera25-format %1 %2 "sdfilmpal576p23976_4_3"    %3
