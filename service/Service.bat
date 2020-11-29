@echo off
title Service

copy /y D:\share\workspace\stk123\service\target\service-1.0.0-exec.jar D:\share\workspace\stk123\service\service.jar

set root_dir=%~dp0

set javaOption=-Xms256m -Xmx2048m

@java %javaOption% -jar -Dfile.encoding=UTF-8 -Dspring.output.ansi.enabled=NEVER %root_dir%service.jar

@rem set /p choi=Press [Enter] to close ...

