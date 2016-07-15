@echo off
if [%1]==[] goto first-missing
if [%2]==[] goto second-missing

@echo on
::call .\chimera23-default "%~1\Chimera23_FTR_C_EN_XG-NR_20_4K_20150603_OV" %2 %3
::call .\chimera25-default "%~1\Chimera25_FTR_C_EN_XG-NR_20_4K_20150608_OV" %2 %3
::call .\chimera50-default "%~1\Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV" %2 %3
::call .\chimera29-default "%~1\Chimera29_FTR_C_EN_XG-NR_20_4K_20150624_OV" %2 %3
call .\chimera25-short "%~1\Chimera25_FTR_C_EN_XG-NR_20_4K_20150608_OV" %2 %3
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