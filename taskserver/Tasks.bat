@echo off
title Tasks

set root_dir=%~dp0

set javaOption=-Xms256m -Xmx2048m

@java %javaOption% -jar %root_dir%target\taskserver-1.0.0.jar

@rem set /p choi=Press [Enter] to close ...