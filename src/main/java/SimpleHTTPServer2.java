import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SimpleHTTPServer2 {
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

                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html;charset=UTF-8");
                out.println();
                out.println("Hello, World!");
                out.flush();
            }
            client.close();
        }
    }

    private static void setHeaders(Map<String, String> headers, String line) {
        String[] parts = line.split(": ");
        if (parts.length == 2) {
            headers.put(parts[0], parts[1]);
        }
    }

    private static String getMessageBody(BufferedReader in, Map<String, String> headers) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] body = new char[contentLength];
        in.read(body, 0, contentLength);
        String messageBody = new String(body);

        return messageBody;
    }
}