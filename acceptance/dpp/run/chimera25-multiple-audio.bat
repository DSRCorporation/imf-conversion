@echo off
pushd %~dp0
call .\usage-one %* && ^
.\dpp %1 ..\..\CPL\chimera25\CPL-multiple-audio.xml "%~2\Chimera25-multiple-audio" ..\chimera25\metadata-short.xml "chimera25-multiple-audio" %3