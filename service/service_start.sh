echo "starting service..."

nohup java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/root/stk -jar ./service.jar --spring.datasource.url=jdbc:oracle:thin:@localhost:1539/xepdb1 >/dev/null 2>&1 &

echo "service is started"
