package io.openmaru.server;

import io.openmaru.test.util.Log;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequestImpl;
import javax.servlet.http.HttpServletResponseImpl;
import java.io.*;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestProcessor implements Runnable {
    private Socket client;

    private Map<String, String> REQUEST_HEADERS = null;
    private Map<String, String> REQUEST_PARAMATERS = null;

    public RequestProcessor(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream())) {
                REQUEST_HEADERS = new HashMap<>();
                REQUEST_PARAMATERS = new HashMap<>();
                File tmpResponseBody = new File("." + UUID.randomUUID().toString());;

                HttpServletRequestImpl httpServletRequest = new HttpServletRequestImpl();
                HttpServletResponseImpl httpServletResponse = new HttpServletResponseImpl(tmpResponseBody);
                httpServletRequest.setRemoteAddr(client.getRemoteSocketAddress());

                int lineCnt = 0;
                String line;
                while (!(line = in.readLine()).equals("")) {
                    if (lineCnt++ == 0) {
                        httpServletRequest.setGeneral(line);
                    } else {
                        setHeaders(line);
                    }
                }
                httpServletRequest.setHeaders(REQUEST_HEADERS);
                httpServletRequest.setParamaters(REQUEST_PARAMATERS);
                httpServletRequest.setBody(getBody(in));

                if ("/".equals(httpServletRequest.getRequestURI())) {
                    HttpServlet servlet = null;
                    try {
                        Class clazz = Class.forName("io.openmaru.test.web.IndexServlet");
                        Constructor<?> constructor = clazz.getConstructor();
                        servlet = (HttpServlet) constructor.newInstance();
                        servlet.init();

                        servlet.service(httpServletRequest, httpServletResponse);

                        httpServletResponse.setStatus(200);
                    } catch (Exception e) {
                        httpServletResponse.setStatus(500);
                    } finally {
                        servlet.destroy();
                    }
                } else {
                    httpServletResponse.getWriter().print("404 Not Found");
                    httpServletResponse.setContentType("text/html;charset=UTF-8");
                    httpServletResponse.setStatus(404);
                }
                httpServletResponse.getWriter().flush();
                httpServletResponse.getWriter().close();


                long size = tmpResponseBody.length();
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


                if (tmpResponseBody.exists()) {
                    try (BufferedReader fileIn = new BufferedReader(new FileReader(tmpResponseBody))) {
                        String content;
                        while ((content = fileIn.readLine()) != null) {
                            out.println(content);
                        }
                    }
                }
                out.flush();

                if (tmpResponseBody != null && tmpResponseBody.exists()) {
                    tmpResponseBody.delete();
                }
            } finally {
                if (client != null) {
                    client.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setHeaders(String line) {
        String[] parts = line.split(": ");
        if (parts.length == 2) {
            REQUEST_HEADERS.put(parts[0], parts[1]);
        }
    }

    private String getBody(BufferedReader in) throws IOException {
        if ("application/x-www-form-urlencoded".equals(REQUEST_HEADERS.get("Content-Type"))) {
            int contentLength = Integer.parseInt(REQUEST_HEADERS.get("Content-Length"));
            char[] body = new char[contentLength];
            in.read(body, 0, contentLength);
            return new String(body);
        }
        return null;
    }
}
