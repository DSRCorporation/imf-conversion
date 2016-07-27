@echo off
pushd %~dp0
call .\usage-one %* && ^
.\dpp %1 ..\..\CPL\chimera25\CPL-2016-namespace.xml "%~2\Chimera25-2016-namespace" ..\chimera25\metadata-short.xml "chimera25-2016-namespace" %3