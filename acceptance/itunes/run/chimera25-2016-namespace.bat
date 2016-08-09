@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\CPL\chimera25\CPL-2016-namespace.xml "%~2\Chimera25-2016-namespace" "vendor_id" %3