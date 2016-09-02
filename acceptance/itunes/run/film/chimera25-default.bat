@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 CPL_38f1005f-e24d-4aaa-93a8-2a5676387b17.xml "%~2\Chimera25" "vendor_id" %3
