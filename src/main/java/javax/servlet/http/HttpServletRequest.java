package javax.servlet.http;

public interface HttpServletRequest {
    // HTTP Method
    public String getMethod();
    // 요청 URL 중 URI
    public String getRequestURI();
    // 요청 URL 중 GET Query String
    public String getQueryString();
    // 메시지 길이
    public int getContentLength();
    // 요청 메시지 타입
    public String getContentType();
    // 요청 Header 특정 값 가져오기
    public String getHeaders(String name);
    // 요청 Parameter 특정 값 가져오기
    public String getParameter(String name);
    // 클라이언트 아이피 가져오기
    public String getRemoteAddr();
}
