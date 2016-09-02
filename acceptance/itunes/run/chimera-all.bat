@echo off
pushd %~dp0
if [%1]==[] goto first-missing
if [%2]==[] goto second-missing


@echo on

:: short versions
call .\film\chimera-all %1 "%~2\film" %3
call .\tv\chimera-all   %1 "%~2\tv" %3


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