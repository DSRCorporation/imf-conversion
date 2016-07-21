@echo off
pushd %~dp0
.\usage-one %* && ^
.\dpp %1 ..\..\CPL\chimera50\CPL-no-audio.xml "%~2\Chimera50-no-audio" ..\chimera50\metadata-short.xml "chimera50-no-audio" %3