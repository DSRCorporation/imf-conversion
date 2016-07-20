@echo off
pushd %~dp0
.\usage-one %* && ^
.\dpp %1 ..\..\CPL\chimera29\CPL-CC-short.xml "%~2\Chimera29-CC-short" ..\chimera29\metadata-cc.xml "chimera29-cc-short" %3