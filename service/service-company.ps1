$host.ui.RawUI.WindowTitle="Service"

Set-Location "D:\IdeaProjects\stk123\service"

Get-Content -Path .\pid.txt -Raw
$processId = Get-Content -Path .\pid.txt -Raw

get-process java | findstr $processId
$foundProcesses = get-process java | findstr $processId

IF ($foundProcesses) {
	Stop-Process -Id $processId
}

copy-item -Force .\target\service-1.0.0-exec.jar .\service.jar

java -Xms256m -Xmx2048m  -jar -D"spring.profiles.active"=company .\service.jar
