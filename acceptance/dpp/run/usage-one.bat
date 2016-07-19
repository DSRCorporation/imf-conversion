@echo off
if [%1]==[] goto first-missing
if [%2]==[] goto second-missing

exit /b 0

:first-missing
@echo on
@echo Missing argument: a path to the directory containing the test packages.
@echo off
goto :usage

:second-missing
@echo on
@echo Missing argument: a path to the directory containing the output for the test packages.
@echo off
goto :usage

:usage
@echo on
@echo Converts a test package to DPP format.
@echo 1st argument - a path to the directory containing the test packages.
@echo 2d argument - a path to the directory containing the output for the test packages.
@echo 3d argument - a path to imf-conversion-utility (if not set a default one will be used assuming that we're in the source root folder).
@echo off
exit /b 1