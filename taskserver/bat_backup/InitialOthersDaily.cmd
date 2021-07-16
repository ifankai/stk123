@echo off
title InitialOthersDaily

set root_dir=%~dp0
set lib_dir=%root_dir%web\WEB-INF\lib

SET CLASSPATH=.
for %%i in (%lib_dir%\*.jar) do call %root_dir%/cpappend.bat %%i

SET CLASSPATH=%CLASSPATH%;%root_dir%stk.jar
echo %CLASSPATH%

set javaOption=-Xms1024m -Xmx2048m

@java %javaOption% -cp %CLASSPATH% com.stk123.task.sub.Ppi
@java %javaOption% -cp %CLASSPATH% com.stk123.task.InvestRobot
@java %javaOption% -cp %CLASSPATH% com.stk123.model.Forex
@java %javaOption% -cp %CLASSPATH% com.stk123.task.BillBoardReport
@java %javaOption% -cp %CLASSPATH% com.stk123.task.EarningsForecast
@java %javaOption% -cp %CLASSPATH% com.stk123.task.InvestorRelationship
@java %javaOption% -cp %CLASSPATH% com.stk123.task.sub.XueqiuComment

@rem set /p choi=Press [Enter] to close ...