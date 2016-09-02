@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 CPL_6f548f17-48c5-452a-94ea-9bb58c6c5b5b.xml "%~2\Chimera50" "vendor_id" %3
