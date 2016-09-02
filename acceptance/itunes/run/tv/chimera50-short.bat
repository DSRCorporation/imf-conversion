@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\..\CPL\chimera50\CPL-short.xml "%~2\Chimera50-Short" "vendor_id" %3