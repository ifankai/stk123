@echo off
title InitialKline

set root_dir=%~dp0
set lib_dir=%root_dir%web\WEB-INF\lib

SET CLASSPATH=.
for %%i in (%lib_dir%\*.jar) do call %root_dir%/cpappend.bat %%i

SET CLASSPATH=%CLASSPATH%;%root_dir%stk.jar
echo %CLASSPATH%

set javaOption=-Xms2048m -Xmx6144m

@java %javaOption% -cp %CLASSPATH% com.stk123.task.InitialKLine
rem @java %javaOption% -cp %CLASSPATH% com.stk123.web.monitor.KlineVolumeMonitor

@rem set /p choi=Press [Enter] to close ...