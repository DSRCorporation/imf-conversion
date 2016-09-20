@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 ..\..\..\CPL\chimera25\CPL-segments-audio-essence-desc.xml "%~2\Chimera25-Segments-audio-essence-desc" "vendor_id" %3