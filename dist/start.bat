@echo off
set LOCAL_JAVA=jre7\bin\javaw.exe
start "HallWay" /B "%LOCAL_JAVA%" -Dfile.encoding=utf-8 -Duser.country=BE -Duser.language=nl -Xmx256m -jar hallway-0.1.0-SNAPSHOT-standalone.jar