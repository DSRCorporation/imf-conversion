@echo off
pushd %~dp0
call .\usage-one %* && ^
.\dpp-audiomap %1 ..\..\CPL\chimera25\CPL-audiomap.xml "%~2\Chimera25-audiomap-16C" ..\chimera25\metadata-16C.xml "chimera25-audiomap-16C" ..\chimera25\audiomap-16C.xml %3