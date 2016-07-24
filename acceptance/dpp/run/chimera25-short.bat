@echo off
pushd %~dp0
call .\usage-one %* && ^
.\dpp %1 ..\..\CPL\chimera25\CPL-short.xml "%~2\Chimera25-Short" ..\chimera25\metadata-short.xml "chimera25-short" %3