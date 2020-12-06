$host.ui.RawUI.WindowTitle="Task"

Set-Location "D:\share\workspace\stk123\taskserver"
cd D:\share\workspace\stk123\taskserver

Get-Content -Path D:\share\workspace\stk123\taskserver\pid.txt -Raw
$processId = Get-Content -Path .\pid.txt -Raw

get-process java | findstr $processId
$foundProcesses = get-process java | findstr $processId

IF ($foundProcesses) {
	Stop-Process -Id $processId
}

copy-item -Force D:\share\workspace\stk123\taskserver\target\taskserver-1.0.0-exec.jar D:\share\workspace\stk123\taskserver\taskserver.jar

java -Xms1024m -Xmx4096m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:\dump -jar D:\share\workspace\stk123\taskserver\taskserver.jar
