import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class SimpleHTTPServer1 {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8080);
        while (true) {
            Socket client = server.accept();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream())) {

                int oneInt = -1;
                while(-1 != (oneInt = in.read())){
                    System.out.print((char)oneInt);
                }

                String response = "HTTP/1.1 200 OK\n\nHello, World!";
                out.print(response);
                out.flush();
            }
            client.close();
        }
    }
}