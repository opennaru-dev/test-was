package javax.servlet.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public interface HttpServletResponse {
    // 응답 상태 코드
    public int getStatus();
    public void setStatus(int status);
    // 메시지 길이
    public void setContentLength(long len);
    // 요청 메시지 타입
    public String getContentType();
    public void setContentType(String type);
    // 요청 Header 특정 값 가져오기
    public String getHeaders(String name);
    public void setHeader(String name, String value);
    public Collection<String> getHeaderNames();
    public PrintWriter getWriter() throws IOException;
}
