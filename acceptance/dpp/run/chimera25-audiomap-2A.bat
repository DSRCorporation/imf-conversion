@echo off
pushd %~dp0
call .\usage-one %* && ^
.\dpp-audiomap %1 ..\..\CPL\chimera25\CPL-audiomap.xml "%~2\Chimera25-audiomap-2A" ..\chimera25\metadata-2A.xml "chimera25-audiomap-2A" ..\chimera25\audiomap-2A.xml %3