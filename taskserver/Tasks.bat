@echo off
title Tasks

copy /y D:\share\workspace\stk123\taskserver\target\taskserver-1.0.0-exec.jar D:\share\workspace\stk123\taskserver\taskserver.jar

set root_dir=%~dp0

set javaOption=-Xms256m -Xmx2048m

@java %javaOption% -jar -Dfile.encoding=UTF-8 %root_dir%taskserver.jar

@rem set /p choi=Press [Enter] to close ...