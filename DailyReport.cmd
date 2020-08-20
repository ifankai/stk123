@echo off

set root_dir=%~dp0
set lib_dir=%root_dir%web\WEB-INF\lib

SET CLASSPATH=.
for %%i in (lib/*.jar) do call cpappend.bat lib/%%i

echo %CLASSPATH%

set javaOption=-Xms256m -Xmx1024m

@java %javaOption% -cp %CLASSPATH%;.\stk.jar task.DailyReport

@rem set /p choi=Press [Enter] to close ...