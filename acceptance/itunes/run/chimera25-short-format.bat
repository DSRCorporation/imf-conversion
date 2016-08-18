@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes-format %1 ..\..\CPL\chimera25\CPL-short-start-from-10-sec.xml "%~2\Chimera25-Short-%~3" "vendor_id"  %3 %4