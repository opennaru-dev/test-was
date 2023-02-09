import io.openmaru.server.RequestProcessor;
import io.openmaru.test.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHTTPServer4 {
    private static ExecutorService POOL = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket server = new ServerSocket(port);
        while (true) {
            Socket client = server.accept();
            Log.info("client: " + client);
            try {
                Runnable r = new RequestProcessor(client);
                POOL.execute(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}