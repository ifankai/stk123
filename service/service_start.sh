echo "starting service..."

java -jar ./service.jar --spring.datasource.url=jdbc:oracle:thin:@localhost:1539/xepdb1 &

echo "service is started"
