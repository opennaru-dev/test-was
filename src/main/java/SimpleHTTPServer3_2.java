import io.openmaru.test.web.IndexServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequestImpl;
import javax.servlet.http.HttpServletResponseImpl;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleHTTPServer3_2 {
    private static Map<String, String> REQUEST_HEADERS = null;
    private static Map<String, String> REQUEST_PARAMATERS = null;

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket server = new ServerSocket(port);
        while (true) {
            Socket client = server.accept();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream())) {

                /*if (!in.ready()) {
                    client.close();
                    continue;
                }*/

                REQUEST_HEADERS = new HashMap<>();
                REQUEST_PARAMATERS = new HashMap<>();
                File tmpFile = new File("." + UUID.randomUUID().toString());;

                HttpServletRequestImpl httpServletRequest = new HttpServletRequestImpl();
                HttpServletResponseImpl httpServletResponse = new HttpServletResponseImpl(tmpFile);
                httpServletRequest.setRemoteAddr(client.getRemoteSocketAddress());
                System.out.println("tmpFile 1-1: " + httpServletRequest);
                System.out.println("in.ready(): " + in.ready());

                System.out.println("000");

                int oneInt = -1;
                byte oldByte = (byte) -1;
                StringBuilder sb = new StringBuilder();
                int lineNumber = 0;
                while (-1 != (oneInt = in.read())) {
                    byte thisByte = (byte) oneInt;
                    if (thisByte == '\n' && oldByte == '\r') {
                        String oneLine = sb.substring(0, sb.length() - 1);
//                        System.out.println("oneLine: " + oneLine);
                        lineNumber++;

                        if (lineNumber == 1) {
                            httpServletRequest.setGeneral(oneLine);
                        } else {
                            setHeaders(oneLine);
                        }
                        if (oneLine.length() <= 0) {
                            break;
                        }
                        sb.setLength(0);
                    } else {
                        sb.append((char) thisByte);
                    }
                    oldByte = (byte) oneInt;
                }
                httpServletRequest.setHeaders(REQUEST_HEADERS);
                httpServletRequest.setParamaters(REQUEST_PARAMATERS);
                httpServletRequest.setBody(getBody(in));



                System.out.println("httpServletRequest.getRequestURI(): " + httpServletRequest.getRequestURI());
                if ("/".equals(httpServletRequest.getRequestURI())) {
                    HttpServlet indexServlet = new IndexServlet();
                    indexServlet.init();
                    try {
                        indexServlet.service(httpServletRequest, httpServletResponse);

                        httpServletResponse.setStatus(200);
                    } catch (IOException e) {
                        httpServletResponse.setStatus(500);
                    } finally {
                        indexServlet.destroy();
                    }
                } else {
                    httpServletResponse.getWriter().print("404 Not Found");
                    httpServletResponse.setContentType("text/html;charset=UTF-8");
                    httpServletResponse.setStatus(404);
                }
                httpServletResponse.getWriter().flush();
                httpServletResponse.getWriter().close();


                long size = tmpFile.length();
                httpServletResponse.setContentLength(size);


                String responseFirstLine = httpServletRequest.getHttpVersion() + " " + httpServletResponse.getStatus() + " ";
                if (httpServletResponse.getStatus() == 200) {
                    responseFirstLine += "OK";
                } else if (httpServletResponse.getStatus() == 404) {
                    responseFirstLine += "Not Found";
                } else {
                    responseFirstLine += "INTERNAL_SERVER_ERROR";
                }
                out.println(responseFirstLine);


                Collection<String> headerNames = httpServletResponse.getHeaderNames();
                for (String headerName : headerNames) {
                    out.println(headerName + ": " + httpServletResponse.getHeaders(headerName));
                }

                out.println();


                if (tmpFile.exists()) {
                    try (BufferedReader fileIn = new BufferedReader(new FileReader(tmpFile))) {
                        String content;
                        while ((content = fileIn.readLine()) != null) {
                            out.println(content);
                        }
                    }
                }
                out.flush();

                System.out.println("httpServletRequest.getRequestURI()2: " + httpServletRequest.getRequestURI());

                System.out.println("tmpFile2: " + tmpFile);
                System.out.println("tmpFile2: " + tmpFile.exists());
                if (tmpFile != null && tmpFile.exists()) {
                    tmpFile.delete();
                }
            } finally {
                System.out.println("close");
                if (client != null) {
                    client.close();
                }
            }
        }
    }

    private static void setHeaders(String line) {
        String[] parts = line.split(": ");
        if (parts.length == 2) {
            REQUEST_HEADERS.put(parts[0], parts[1]);
        }
    }

    private static String getBody(BufferedReader in) throws IOException {
        if ("application/x-www-form-urlencoded".equals(REQUEST_HEADERS.get("Content-Type"))) {
            int contentLength = Integer.parseInt(REQUEST_HEADERS.get("Content-Length"));
            char[] body = new char[contentLength];
            in.read(body, 0, contentLength);
            return new String(body);
        }
        return null;
    }
}