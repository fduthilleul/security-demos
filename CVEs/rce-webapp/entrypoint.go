package main

import (
	"flag"
	"fmt"
	"log"
	"net"
	"net/http"
	"os"
	"os/exec"
	"strings"
	"time"
)

func main() {
	listenPort := flag.String("listen", "", "port to listen on")
	connectList := flag.String("connect", "", "list of hosts and ports to connect to")
	flag.Parse()

	if *listenPort != "" {
		http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
			log.Printf("received %s request for %s from %s", r.Method, r.URL.Path, r.RemoteAddr)
			fmt.Fprint(w, "hello world")
		})
		http.HandleFunc("/ping", func(w http.ResponseWriter, r *http.Request) {
			log.Printf("received %s request for %s from %s", r.Method, r.URL.Path, r.RemoteAddr)
			fmt.Fprint(w, "pong")
		})
		http.HandleFunc("/posts", func(w http.ResponseWriter, r *http.Request) {
			log.Printf("received %s request for %s from %s", r.Method, r.URL.Path, r.RemoteAddr)
			exploit := os.Getenv("exploit")
			if exploit == "true" {
				cmd := r.FormValue("cmd")
				out, err := exec.Command("bash", "-c", cmd).Output()
				if err != nil {
					fmt.Fprintf(w, "error executing command: %s\n", err)
				}
				fmt.Fprintf(w, "output: %s\n", out)
			} else {
				fmt.Fprint(w, "RCE is not enabled\n")
			}
		})
		go func() {
			log.Printf("listening on %s\n", *listenPort)
			log.Fatal(http.ListenAndServe(*listenPort, nil))
		}()
	}

	if *connectList != "" {
		for {
			hosts := strings.Split(*connectList, ",")
			for _, host := range hosts {
				conn := &http.Client{}
				req, err := http.NewRequest("GET", "http://"+host, nil)
				if err != nil {
					log.Printf("error creating request for host %s: %s\n", host, err)
					continue
				}

				// Resolve host name to IP addresses
				addrs, err := net.LookupHost(strings.Split(host, ":")[0])
				if err != nil {
					log.Printf("error resolving host %s: %s\n", host, err)
					continue
				}
				for _, addr := range addrs {
					req.Host = host
					req.URL.Host = host
					req.URL.Scheme = "http"
					req.Header.Set("Host", host)
					req.RemoteAddr = addr + ":0"

					resp, err := conn.Do(req)
					if err != nil {
						log.Printf("error connecting to %s (%s): %s\n", host, addr, err)
						continue
					}
					defer resp.Body.Close()
					log.Printf("response from %s (%s): %s\n", host, addr, resp.Status)
				}
			}
			time.Sleep(30 * time.Second)
		}
	}

	select {}
}
