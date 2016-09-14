@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes %1 CPL_38207de4-35b8-4dbe-a8ad-852149d3bc7b.xml "%~2\Chimera29" "vendor_id" %3
