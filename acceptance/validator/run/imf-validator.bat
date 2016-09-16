@echo off
pushd %~dp0
if [%1]==[] goto first-missing
if [%2]==[] goto second-missing

SET PROG=%3
if [%3]==[] SET PROG="..\..\..\install\imf-conversion-utility\tools\imf-validation.jar"

@echo on
java -jar %PROG% -i %1 -d %2
@echo off

exit /b 0

:first-missing
@echo on
@echo Missing argument: a path to IMP directory.
@echo off
goto :usage

:second-missing
@echo on
@echo Missing argument: a path to working directory.
@echo off
goto :usage

:usage
@echo on
@echo IMF validation
@echo 1st argument - a path to IMP directory.
@echo 2nd argument - a path to working directory.
@echo off
exit /b 1