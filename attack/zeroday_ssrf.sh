wget -q -O - http://$(oc -n frontend get route/blog --output jsonpath={.spec.host})/fetch?url=http://checkip.dyndns.com
wget -q -O - http://$(oc -n frontend get route/blog --output jsonpath={.spec.host})/fetch?url=http://visa-processor-service.payments:8080
