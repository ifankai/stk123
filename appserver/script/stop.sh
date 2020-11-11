oldpid=$(ps -ef | grep "appserver" | grep -v grep | awk '{print $2}')
for i in ${oldpid}
do
  kill -9 ${i}
done
echo "appserver is stopped."

