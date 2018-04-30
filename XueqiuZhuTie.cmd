@echo off
title XueqiuZhuTie

set root_dir=%~dp0
set lib_dir=%root_dir%lib

SET CLASSPATH=.
for %%i in (%lib_dir%\*.jar) do call %root_dir%/cpappend.bat %%i
for %%i in (%lib_dir%\webspec\*.jar) do call %root_dir%/cpappend.bat %%i

SET CLASSPATH=%CLASSPATH%;%root_dir%stk.jar
echo %CLASSPATH%

set javaOption=-Xms256m -Xmx512m

@java %javaOption% -cp %CLASSPATH% com.stk123.task.sub.XueqiuZhuTie

@rem set /p choi=Press [Enter] to close ...