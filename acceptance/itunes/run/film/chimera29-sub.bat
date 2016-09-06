@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\..\CPL\chimera29\CPL-CC-short.xml "%~2\Chimera29-Sub" "vendor_id" %3