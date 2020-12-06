$host.ui.RawUI.WindowTitle="Service"

Set-Location "D:\share\workspace\stk123\service"
cd D:\share\workspace\stk123\service

Get-Content -Path D:\share\workspace\stk123\service\pid.txt -Raw
$processId = Get-Content -Path .\pid.txt -Raw

get-process java | findstr $processId
$foundProcesses = get-process java | findstr $processId

IF ($foundProcesses) {
	Stop-Process -Id $processId
}

copy-item -Force D:\share\workspace\stk123\service\target\service-1.0.0-exec.jar D:\share\workspace\stk123\service\service.jar

java -Xms256m -Xmx2048m -jar D:\share\workspace\stk123\service\service.jar
