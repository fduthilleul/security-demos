Logs https://docs.openshift.com/container-platform/4.13/security/audit-log-view.html

## Get the list of masters
masters=$(oc get nodes -l node-role.kubernetes.io/master -o custom-columns=POD:.metadata.name --no-headers)

## get all actions on payments namespace
echo '"Timestamp","Username","Verb","Namespace","Resource","Name","UserAgent","Authorization Decision","Authorization Reason"' > report.csv
for master in $(echo $masters)
do
  oc adm node-logs  ${master} --path=kube-apiserver/audit.log | jq -r 'select(.requestURI | contains("/api/v1/namespaces/payments")) | select(.user.username != "system:apiserver") | [.requestReceivedTimestamp, .user.username, .verb, .objectRef.namespace, .objectRef.resource, .objectRef.name, .userAgent, .responseStatus.code, .annotations."authorization.k8s.io/decision", .annotations."authorization.k8s.io/reason"] | @csv' >> report.csv
done

## Get everything that the system:serviceaccount:payments:visa-processor has beeing done to the cluster
echo '"Timestamp","Username","Verb","Namespace","Resource","Name","UserAgent","Authorization Decision","Authorization Reason"' > report.csv
for master in $(echo $masters)
do
  oc adm node-logs  ${master} --path=kube-apiserver/audit.log | jq -r 'select(.user.username =="system:serviceaccount:payments:visa-processor") | [.requestReceivedTimestamp, .user.username, .verb, .objectRef.namespace, .objectRef.resource, .objectRef.name, .userAgent, .responseStatus.code, .annotations."authorization.k8s.io/decision", .annotations."authorization.k8s.io/reason"] | @csv' >> report.csv
done

## Get all the info for the pod r00t
echo '"Timestamp","Username","Verb","Namespace","Resource","Name","UserAgent","Authorization Decision","Authorization Reason"' > report.csv
for master in $(echo $masters)
do
  oc adm node-logs  ${master} --path=kube-apiserver/audit.log | jq -r 'select(.objectRef.name == "r00t") | [.requestReceivedTimestamp, .user.username, .verb, .objectRef.namespace, .objectRef.resource, .objectRef.name, .userAgent, .responseStatus.code, .annotations."authorization.k8s.io/decision", .annotations."authorization.k8s.io/reason"] | @csv' >> report.csv
done

## when the pod was created - csv
echo '"Timestamp","Username","Verb","Namespace","Resource","Name","UserAgent","Authorization Decision","Authorization Reason"' > report.csv
for master in $(echo $masters)
for master in $(echo $masters)
  oc adm node-logs  ${master} --path=kube-apiserver/audit.log | jq -r 'select(.objectRef.name == "r00t") | [.requestReceivedTimestamp, .user.username, .verb, .objectRef.namespace, .objectRef.resource, .objectRef.name, .userAgent, .responseStatus.code, .annotations."authorization.k8s.io/decision", .annotations."authorization.k8s.io/reason"] | @csv' >> report.csv
done



## namespaces created 
for master in $(echo $masters)
do
oc adm node-logs  ${master} --path=kube-apiserver/audit.log  | jq -r 'select(.objectRef.resource == "namespaces" and .verb == "create")'
done

## pods created in the payment namespaces
for master in $(echo $masters)
do
oc adm node-logs  ${master} --path=kube-apiserver/audit.log  | jq -r 'select(.objectRef.resource == "pods" and .verb == "create" and .objectRef.namespace == "payments" and (.user.groups | index("system:serviceaccounts:kube-system") | not) and .user.username != "system:kube-scheduler") | [.requestReceivedTimestamp, .user.username, .verb, .objectRef.namespace, .objectRef.name] | @csv'
done

## deployments created in the payment namespaces
for master in $(echo $masters)
do
oc adm node-logs  ${master} --path=kube-apiserver/audit.log  | jq -r 'select(.objectRef.resource == "deployments" and .verb == "create" and .objectRef.namespace == "payments") | [.requestReceivedTimestamp, .user.username, .verb, .objectRef.namespace, .objectRef.name] | @csv'
done

# Get the secureContext of all pods running or runned on payment namespaces
for master in $(echo $masters)
do
oc adm node-logs  ${master} --path=kube-apiserver/audit.log  | jq -r 'select(.objectRef.resource == "pods" and .objectRef.namespace == "payments" and (.objectRef.name | length > 0) and (.annotations."securitycontextconstraints.admission.openshift.io/chosen" | length > 0)) | [.requestReceivedTimestamp, .user.username, .objectRef.namespace, .objectRef.name, .annotations."securitycontextconstraints.admission.openshift.io/chosen"] | @csv'
done


## Identify exec into a pod
for master in $(echo $masters)
do
oc adm node-logs  ${master} --path=kube-apiserver/audit.log | jq -r 'select(.objectRef.subresource == "exec") | [.requestReceivedTimestamp, .user.username, .objectRef.namespace, .objectRef.name ] | @csv'
done
