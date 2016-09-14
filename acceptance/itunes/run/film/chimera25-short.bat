@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\..\CPL\chimera25\CPL-short.xml "%~2\Chimera25-Short" "vendor_id" %3