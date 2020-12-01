$host.ui.RawUI.WindowTitle="Task"

copy-item -Force D:\share\workspace\stk123\taskserver\target\taskserver-1.0.0-exec.jar D:\share\workspace\stk123\taskserver\taskserver.jar

cd D:\share\workspace\stk123\taskserver

java -Xms1024m -Xmx4096m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:\dump -jar D:\share\workspace\stk123\taskserver\taskserver.jar
