import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class SimpleHTTPServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket server = new ServerSocket(port);
        while (true) {
            Socket client = server.accept();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream())) {

                Map<String, String> headers = new HashMap<>();

                String line;
                while (!(line = in.readLine()).equals("")) {
                    System.out.println(line);
                    setHeaders(headers, line);
                }
                if ("application/x-www-form-urlencoded".equals(headers.get("Content-Type"))) {
                    String messageBody = getMessageBody(in, headers);
                    System.out.println("Request Message Body ====================>");
                    System.out.println(messageBody);
                    System.out.println("====================");
                }

                String response = "HTTP/1.1 200 OK\n\nHello, World!";
                out.print(response);
                out.flush();
                out.flush();
            }
            client.close();
        }
    }

    private static String getMessageBody(BufferedReader in, Map<String, String> headers) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] body = new char[contentLength];
        in.read(body, 0, contentLength);
        String messageBody = new String(body);

        return messageBody;
    }

    private static void setHeaders(Map<String, String> headers, String line) {
        String[] parts = line.split(": ");
        if (parts.length == 2) {
            headers.put(parts[0], parts[1]);
        }
    }
}