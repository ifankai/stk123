@echo off

set root_dir=%~dp0
set lib_dir=%root_dir%web\WEB-INF\lib

SET CLASSPATH=.
for %%i in (%lib_dir%\*.jar) do call %root_dir%/cpappend.bat %%i

SET CLASSPATH=%CLASSPATH%;%root_dir%stk.jar
echo %CLASSPATH%

set javaOption=-Xms256m -Xmx1024m

@java %javaOption% -cp %CLASSPATH% com.stk123.model.AStkTools %*

@rem set /p choi=Press [Enter] to close ...