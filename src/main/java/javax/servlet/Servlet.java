package javax.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Servlet {
    public void init();

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException;

    public void destroy();
}
