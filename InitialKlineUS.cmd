@echo off
title InitialKlineUS

set root_dir=%~dp0
set lib_dir=%root_dir%lib

SET CLASSPATH=.
for %%i in (%lib_dir%\*.jar) do call %root_dir%/cpappend.bat %%i

SET CLASSPATH=%CLASSPATH%;%root_dir%stk.jar
echo %CLASSPATH%

set javaOption=-Xms1024m -Xmx4096m

@java %javaOption% -cp %CLASSPATH% com.stk123.task.InitialKLine US %*


@rem set /p choi=Press [Enter] to close ...