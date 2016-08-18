@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes-audiomap %1 ..\..\CPL\chimera25\CPL-short-start-from-10-sec.xml "%~2\Chimera25-audiomap" "vendor_id" ..\chimera25\audiomap.xml %3