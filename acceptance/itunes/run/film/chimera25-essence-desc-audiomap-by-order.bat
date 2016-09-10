@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes-audiomap %1 ..\..\..\CPL\chimera25\CPL-multiple-audio-essence-desc.xml "%~2\Chimera25-essence-desc-audiomap-by-order" "vendor_id" ..\..\chimera25\5-6-5-fr-audiomap.xml %3