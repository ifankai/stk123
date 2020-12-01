$host.ui.RawUI.WindowTitle="Service"

copy-item -Force D:\share\workspace\stk123\service\target\service-1.0.0-exec.jar D:\share\workspace\stk123\service\service.jar

cd D:\share\workspace\stk123\service

java -Xms256m -Xmx2048m -jar D:\share\workspace\stk123\service\service.jar
