@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\..\CPL\chimera29\CPL-CC-no-audio-no-video.xml "%~2\Chimera29-Sub-no-audio-no-video" "vendor_id" %3