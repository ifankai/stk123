@echo off
title build

cd D:\share\workspace\stk123
d:

set root_dir=%~dp0

rem git pull

D:\apps\apache-maven-3.5.2\bin\mvn clean install

set /p choi=Press [Enter] to close ...