@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\..\CPL\eldorado\CPL-essence-desc.xml "%~2\Eldorado" "vendor_id" %3