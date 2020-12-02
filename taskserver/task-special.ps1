$host.ui.RawUI.WindowTitle="Task-Special"

copy-item -Force D:\share\workspace\stk123\taskserver\target\taskserver-1.0.0-exec.jar D:\share\workspace\stk123\taskserver\taskserver.jar

cd D:\share\workspace\stk123\taskserver

java -Xms1024m -Xmx4096m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:\dump -D"server.port"=8082 -D"stk.task"=true -jar D:\share\workspace\stk123\taskserver\taskserver.jar
