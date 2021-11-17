@echo off
title WebApplication

copy /y D:\IdeaProjects\stk123\appserver\target\appserver-1.0.0.war D:\IdeaProjects\stk123\appserver\appserver.jar

set root_dir=%~dp0

set javaOption=-Xms2048m -Xmx12288m

@D:\apps\jdk-17.0.1\bin\java %javaOption% -jar -Dfile.encoding=UTF-8 %root_dir%\appserver\appserver.jar --spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE

@rem set /p choi=Press [Enter] to close ...


