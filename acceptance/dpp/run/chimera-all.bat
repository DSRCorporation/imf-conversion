@echo off
pushd %~dp0
if [%1]==[] goto first-missing
if [%2]==[] goto second-missing

SET Chimera23Dir=Chimera23_FTR_C_EN_XG-NR_20_4K_20150603_OV
SET Chimera25Dir=Chimera25_FTR_C_EN_XG-NR_20_4K_20150608_OV
SET Chimera29Dir=Chimera29_FTR_C_EN_XG-NR_20_4K_20150624_OV
SET Chimera50Dir=Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV

@echo on
:: default as-is full CPLs for all test packages
call .\chimera23-default "%~1\%Chimera23Dir%" %2 %3
call .\chimera25-default "%~1\%Chimera25Dir%" %2 %3
call .\chimera50-default "%~1\%Chimera50Dir%" %2 %3
call .\chimera29-default "%~1\%Chimera29Dir%" %2 %3

:: short versions
call .\chimera25-short "%~1\%Chimera25Dir%" %2 %3
call .\chimera50-short "%~1\%Chimera50Dir%" %2 %3

:: Closed Captions
call .\chimera29-CC "%~1\%Chimera29Dir%" %2 %3
call .\chimera29-CC-short "%~1\%Chimera29Dir%" %2 %3

:: CPLs with tricky segments
call .\chimera25-segments "%~1\%Chimera25Dir%" %2 %3

:: no audio/video/CC
call .\chimera50-no-audio "%~1\%Chimera50Dir%" %2 %3
call .\chimera50-no-video "%~1\%Chimera50Dir%" %2 %3
call .\chimera29-CC-no-audio-no-video "%~1\%Chimera29Dir%" %2 %3

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
@echo Convert all default test packages to DPP Format.
@echo 1st argument - a path to the directory containing all default test packages.
@echo 2d argument - a path to the directory containing the output for all test packages.
@echo 3d argument - a path to imf-conversion-utility (if not set a default one will be used assuming that we're in the source root folder).
@echo off
exit /b 1