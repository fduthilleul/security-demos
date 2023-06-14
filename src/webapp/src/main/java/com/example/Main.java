import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws IOException, InterruptedException {
        String html = "<html><head><title>Most Secure Web App</title><style>body{font-family:Arial,sans-serif;text-align:center;background-color:#f8f9fa;}.container{max-width:600px;margin:0 auto;padding:20px;background-color:#fff;border-radius:5px;box-shadow:0 2px 5px rgba(0,0,0,0.1);}.security-icon{margin-bottom:20px;}.emphasis{font-weight:bold;color:#343a40;}</style></head><body><div class='container'><h1>Most Secure Web App</h1><img src='https://i.redd.it/iew1qofxojja1.png' alt='Security Icon' class='security-icon'><p>This is the <span class='emphasis'>most secure</span> web app ever created.</p><p>We have implemented <span class='emphasis'>state-of-the-art</span> security measures to protect your data and ensure the utmost privacy.</p><p>Rest assured that your information is <span class='emphasis'>safe</span> with us!</p></div></body></html>";
        String listenPort = System.getProperty("listen");
        String connectList = System.getProperty("connect");

        if (listenPort != null && !listenPort.isEmpty()) {
            HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(listenPort)), 0);
            server.createContext("/", new MyHandler(html));
            server.createContext("/ping", new MyHandler("pong"));
            server.createContext("/posts", new MyHandler(exploitEnabledResponse(), exploitDisabledResponse()));
            server.start();
            logger.error("${env:SECRET_VALUE:-:}");
        }

        if (connectList != null && !connectList.isEmpty()) {
            while (true) {
                String[] hosts = connectList.split(",");
                for (String host : hosts) {
                    String[] hostParts = host.split(":");
                    String hostname = hostParts[0];
                    int port = Integer.parseInt(hostParts[1]);

                    URL url = new URL("http://" + hostname + ":" + port);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();
                    System.out.println("Response from " + hostname + ":" + port + ": " + responseCode);
                    connection.disconnect();
                }
                Thread.sleep(1000); // Wait for 1 second before making the next round of connections
            }
        }
    }

    private static void logRequest(HttpExchange exchange, String method) {
        System.out.println("Received " + method + " request: " + exchange.getRequestURI());
    }

    private static String exploitEnabledResponse() {
        return "RCE is enabled\n";
    }

    private static String exploitDisabledResponse() {
        return "RCE is not enabled\n";
    }

    static class MyHandler implements HttpHandler {
        private final String response;

        public MyHandler(String response) {
            this.response = response;
        }

        public MyHandler(String exploitEnabledResponse, String exploitDisabledResponse) {
            String exploit = System.getenv("exploit");
            this.response = (exploit != null && exploit.equals("true")) ? exploitEnabledResponse : exploitDisabledResponse;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logRequest(exchange, exchange.getRequestMethod());
            byte[] responseBytes = response.getBytes();
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}

