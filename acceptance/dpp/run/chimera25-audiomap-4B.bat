@echo off
pushd %~dp0
call .\usage-one %* && ^
.\dpp-audiomap %1 ..\..\CPL\chimera25\CPL-audiomap.xml "%~2\Chimera25-audiomap-4B" ..\chimera25\metadata-4B.xml "chimera25-audiomap-4B" ..\chimera25\audiomap-4B.xml %3