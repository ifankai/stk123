$host.ui.RawUI.WindowTitle="Task"

copy-item -Force D:\share\workspace\stk123\taskserver\target\taskserver-1.0.0-exec.jar D:\share\workspace\stk123\taskserver\taskserver.jar

java -Xms256m -Xmx2048m -jar D:\share\workspace\stk123\taskserver\taskserver.jar
