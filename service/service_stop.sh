oldpid=$(ps -ef | grep "service" | grep -v grep | awk '{print $2}')
for i in ${oldpid}
do
  kill -9 ${i}
done
echo "service is stopped."

