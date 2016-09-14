@echo off
pushd %~dp0
call .\usage-one %* && ^
.\itunes-sub-ext %1 ..\..\..\CPL\chimera29\CPL-CC-short.xml "%~2\Chimera29-Sub-Ext" "vendor_id" ..\..\chimera29\subtitles_en.xml ..\..\chimera29\subtitles_fr.xml ..\..\chimera29\subtitles_es.xml %3