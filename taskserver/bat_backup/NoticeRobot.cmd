@echo off
title NoticeRobot

set root_dir=%~dp0
set lib_dir=%root_dir%web\WEB-INF\lib

SET CLASSPATH=.
for %%i in (%lib_dir%\*.jar) do call %root_dir%/cpappend.bat %%i

SET CLASSPATH=%CLASSPATH%;%root_dir%stk.jar
echo %CLASSPATH%

set javaOption=-Xms512m -Xmx2048m

@java %javaOption% -cp %CLASSPATH% com.stk123.task.NoticeRobot

@rem set /p choi=Press [Enter] to close ...