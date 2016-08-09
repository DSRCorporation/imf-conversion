@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\CPL\chimera50\CPL-no-audio.xml "%~2\Chimera50-no-audio" "vendor_id" %3