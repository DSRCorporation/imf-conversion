@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\CPL\chimera25\CPL-segments.xml "%~2\Chimera25-Segments" "vendor_id" %3