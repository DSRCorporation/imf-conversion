@echo off
pushd %~dp0
if [%1]==[] goto first-missing
if [%2]==[] goto second-missing
if [%3]==[] goto third-missing
if [%4]==[] goto fourth-missing
if [%5]==[] goto fifth-missing

SET PROG=%6
if [%6]==[] SET PROG="..\..\..\..\install\imf-conversion-utility\bin\imf-conversion-utility"

@echo on
%PROG% itunes -c ..\..\..\config.xml -m convert --imp %1 --cpl %2 -w %3 --vendor-id %4 --poster %5 -l debug -p tv
@echo off
exit /b 0

:first-missing
@echo on
@echo Missing argument: a path to IMP directory.
@echo off
goto :usage

:second-missing
@echo on
@echo Missing argument: a path to CPL.
@echo off
goto :usage

:third-missing
@echo on
@echo Missing argument: a path to the directory containing the output for the test packages.
@echo off
goto :usage

:fourth-missing
@echo on
@echo Missing argument: a vendor identifier.
@echo off
goto :usage

:fifth-missing
@echo on
@echo Missing argument: a poster path.
@echo off
goto :usage

:usage
@echo on
@echo Converts to iTunes format.
@echo 1st argument - a path to IMP directory.
@echo 2d argument - a path to CPL.
@echo 3d argument - a path to the directory containing the output for the test packages.
@echo 4th argument - a vendor identifier.
@echo 5th argument - a path to poster.
@echo 6th argument - a path to imf-conversion-utility (if not set a default one will be used assuming that we're in the source root folder).
@echo off
exit /b 1