package javax.servlet.http;

import java.net.SocketAddress;
import java.util.Map;

public class HttpServletRequestImpl implements HttpServletRequest {
    private Map<String, String> headers = null;
    private Map<String, String> paramaters = null;

    private String remoteAddr;
    private String method;
    private String requestUrl;
    private String httpVersion;

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getRequestURI() {
        if (requestUrl != null) {
            int idx = requestUrl.indexOf("?");
            if (idx > -1) {
                return requestUrl.substring(0, idx == -1 ? requestUrl.length() - 1 : idx);
            } else {
                return requestUrl;
            }
        }
        return null;
    }

    @Override
    public String getQueryString() {
        String queryString = null;
        if (requestUrl != null) {
            int idx = requestUrl.indexOf("?");
            if (idx > -1) {
                queryString = requestUrl.substring(idx + 1);
            }

            if (queryString != null) {
                String[] pairs = queryString.split("&");
                for (String pair : pairs) {
                    idx = pair.indexOf("=");
                    this.paramaters.put(pair.substring(0, idx), pair.substring(idx + 1));
                }
            }
        }
        return queryString;
    }

    @Override
    public int getContentLength() {
        String length = this.getHeaders("Content-Length");
        if (length == null) {
            return 0;
        } else {
            return Integer.parseInt(length);
        }
    }

    @Override
    public String getContentType() {
        return getHeaders("Content-Type");
    }

    @Override
    public String getHeaders(String name) {
        return headers==null?null:headers.get(name);
    }

    @Override
    public String getParameter(String name) {
        return paramaters==null?null:paramaters.get(name);
    }

    @Override
    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    public void setGeneral(String general) {
        int firstBlank = general.indexOf(" ");
        int secondBlank = general.lastIndexOf(" ");
        this.method = general.substring(0, firstBlank);
        this.requestUrl = general.substring(firstBlank+1, secondBlank);
        this.httpVersion = general.substring(secondBlank+1);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setParamaters(Map<String, String> paramaters) {
        this.paramaters = paramaters;
    }

    public void setBody(String body) {
        if (body != null && body.contains("&")) {
            System.out.println("body: " + body);
            String[] pairs = body.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                this.paramaters.put(pair.substring(0, idx), pair.substring(idx + 1));
            }
        }
    }

    public void setRemoteAddr(SocketAddress remoteAddr) {
        this.remoteAddr = remoteAddr.toString().split(":")[0].substring(1);
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
