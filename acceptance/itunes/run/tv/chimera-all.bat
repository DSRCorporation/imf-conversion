@echo off
pushd %~dp0
if [%1]==[] goto first-missing
if [%2]==[] goto second-missing

SET Chimera23Dir=Chimera23_FTR_C_EN_XG-NR_20_4K_20150603_OV
SET Chimera25Dir=Chimera25_FTR_C_EN_XG-NR_20_4K_20150608_OV
SET Chimera29Dir=Chimera29_FTR_C_EN_XG-NR_20_4K_20150624_OV
SET Chimera50Dir=Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV

@echo on

:: short versions
call .\chimera25-short      "%~1\%Chimera25Dir%" %2 %3
call .\chimera50-short      "%~1\%Chimera50Dir%" %2 %3

:: formats
call .\chimera25-formats      "%~1\%Chimera25Dir%" %2 %3

:: 2016 namespace
:: use only with switched off imf validation
call .\chimera25-2016-namespace     "%~1\%Chimera25Dir%" %2 %3

:: CPLs with tricky segments
call .\chimera25-segments                       "%~1\%Chimera25Dir%" %2 %3
call .\chimera25-segments-audio-essence-desc    "%~1\%Chimera25Dir%" %2 %3

:: custom audiomap, metadata, additional assets, fallback-locale
call .\chimera25-audiomap           "%~1\%Chimera25Dir%" %2 %3
call .\chimera25-metadata           "%~1\%Chimera25Dir%" %2 %3
call .\chimera25-audiomap-metadata  "%~1\%Chimera25Dir%" %2 %3
call .\chimera25-add-assets         "%~1\%Chimera25Dir%" %2 %3
call .\chimera25-locale             "%~1\%Chimera25Dir%" %2 %3

:: no audio
call .\chimera50-no-audio   "%~1\%Chimera50Dir%" %2 %3

:: closed captions
call .\chimera25-cc         "%~1\%Chimera25Dir%" %2 %3


@echo off
exit /b 0

:first-missing
@echo on
@echo Missing argument: a path to the directory containing all default test packages.
@echo off
goto :usage

:second-missing
@echo on
@echo Missing argument: a path to the directory containing the output for all test packages.
@echo off
goto :usage

:usage
@echo on
@echo Convert all default test packages to iTunes Format.
@echo 1st argument - a path to the directory containing all default test packages.
@echo 2d argument - a path to the directory containing the output for all test packages.
@echo 3d argument - a path to imf-conversion-utility (if not set a default one will be used assuming that we're in the source root folder).
@echo off
exit /b 1