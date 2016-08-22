@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\CPL\chimera29\CPL-CC-segments.xml "%~2\Chimera29-Sub-segments" "vendor_id" %3