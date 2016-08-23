@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes-locale %1 ..\..\CPL\chimera25\CPL-short.xml "%~2\Chimera25-locale" "vendor_id" "ru" %3