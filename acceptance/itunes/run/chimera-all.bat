@echo off
pushd %~dp0
if [%1]==[] goto first-missing
if [%2]==[] goto second-missing

SET Chimera25Dir=Chimera25_FTR_C_EN_XG-NR_20_4K_20150608_OV

@echo on

:: short versions
call .\chimera25-short              "%~1\%Chimera25Dir%" %2 %3
call .\chimera25-short-metadata     "%~1\%Chimera25Dir%" %2 %3
call .\chimera25-short-add-assets   "%~1\%Chimera25Dir%" %2 %3

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