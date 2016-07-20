@echo off
pushd %~dp0
.\usage-one %* && ^
.\dpp %1 ..\..\CPL\chimera50\CPL-short.xml "%~2\Chimera50-Short" ..\chimera50\metadata-short.xml "chimera50-short" %3