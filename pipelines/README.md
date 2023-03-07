# Install the openshift-pipeline operator 

I'll not cover the instalation here, just go to the operator hub and install the openshift pipeline operator

# Create a roxsecrets secret
```
export ROX_ENDPOINT=<CENTRAL_URL>:443 
export ROX_API_TOKEN=<TOKEN>

oc new-project pipeline-demo
oc create secret generic roxsecrets --from-literal=rox_central_endpoint="${ROX_ENDPOINT}" --from-literal=rox_api_token=${ROX_API_TOKEN} -n pipeline-demo
```

# SSH authentication (Git) 

[Authenticating pipelines](https://docs.openshift.com/container-platform/4.12/cicd/pipelines/authenticating-pipelines-using-git-secret.html#op-configuring-ssh-authentication-for-git_authenticating-pipelines-using-git-secret)

For a pipeline to retrieve resources from repositories configured with SSH keys, you must configure the SSH-based authentication for that pipeline.

## Easy Steps

```
ssh-keygen -t rsa -b 4096 -C "tekton@tekton.dev" -f id_rsa
oc create secret generic --type=kubernetes.io/ssh-auth git-ssh-key --from-file=ssh-privatekey=id_rsa -n pipeline-demo
oc annotate secret git-ssh-key tekton.dev/git-0=github.com -n pipeline-demo
oc patch serviceaccount pipeline -p '{"secrets": [{"name": "git-ssh-key"}]}' -n pipeline-demo
```

**Dont forget to add the public key to yout git provider and give write access :)**

# Create the demo pipeline
```
oc apply -k .
```