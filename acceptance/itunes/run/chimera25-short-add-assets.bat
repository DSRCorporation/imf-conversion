@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes-add-assets %1 ..\..\CPL\chimera25\CPL-short.xml "%~2\Chimera25-Short-additional" "vendor_id" ..\chimera25\poster.jpg ..\chimera25\trailer.mov ..\chimera25\chapters.xml %3