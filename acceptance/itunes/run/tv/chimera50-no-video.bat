@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\..\CPL\chimera50\CPL-no-video.xml "%~2\Chimera50-no-video" "vendor_id" %3