@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes-metadata %1 ..\..\..\CPL\chimera25\CPL-short.xml "%~2\Chimera25-metadata" "vendor_id" ..\..\chimera25\film-metadata.xml %3