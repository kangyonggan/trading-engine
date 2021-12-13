package com.kangyonggan.tradingEngine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author kyg
 */
@Configuration
public class RequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("websocket".equals(request.getHeader("upgrade"))) {
            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(new RequestWrapper(request), response);
        }
    }

    /**
     * @author kyg
     */
    public static class RequestWrapper extends HttpServletRequestWrapper {

        private final byte[] body;

        public RequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            body = getBodyString(request).getBytes(StandardCharsets.UTF_8);
        }

        /**
         * 获取请求体
         *
         * @return
         */
        public String getRequestBody() {
            return new String(body, StandardCharsets.UTF_8);
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public int read() {
                    return inputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }

        public String getBodyString(ServletRequest request) throws IOException {
            StringBuilder sb = new StringBuilder();
            try (InputStreamReader is = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(is)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            return sb.toString();
        }
    }

}
