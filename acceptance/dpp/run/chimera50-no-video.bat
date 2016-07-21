@echo off
pushd %~dp0
.\usage-one %* && ^
.\dpp %1 ..\..\CPL\chimera50\CPL-no-video.xml "%~2\Chimera50-no-video" ..\chimera50\metadata-short.xml "chimera50-no-video" %3