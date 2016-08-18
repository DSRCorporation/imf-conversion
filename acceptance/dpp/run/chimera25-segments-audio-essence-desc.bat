@echo off
pushd %~dp0
call .\usage-one %* && ^
.\dpp %1 ..\..\CPL\chimera25\CPL-segments-audio-essence-desc.xml "%~2\Chimera25-Segments-audio-essence-desc" ..\chimera25\metadata-segments.xml "chimera25-segments-audio-essence-desc" %3