@echo off
pushd %~dp0
call .\usage-one %* && ^
.\dpp %1 ..\..\CPL\chimera25\CPL-segments.xml "%~2\Chimera25-Segments" ..\chimera25\metadata-segments.xml "chimera25-segments" %3