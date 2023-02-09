package javax.servlet.http;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HttpServletResponseImpl implements HttpServletResponse {
    private Map<String, String> headers = new HashMap<>();
    private PrintWriter printWriter = null;
    private int status;

    public HttpServletResponseImpl(File tmpFile) {
        try {
            printWriter = new PrintWriter(tmpFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setContentLength(long len) {
        headers.put("Content-Length", len + "");
    }

    @Override
    public String getContentType() {
        return headers.get("Content-Type");
    }

    @Override
    public void setContentType(String type) {
        headers.put("Content-Type", type);
    }

    @Override
    public String getHeaders(String name) {
        return headers.get(name);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return new ArrayList<>(headers.keySet());
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }
}
