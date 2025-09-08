package com.link.atm_service.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(1)
public class CachingRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();

        if (requestURI.contains("/extraer") || requestURI.contains("/depositar")) {

            if (httpServletRequest.getMethod().equals("POST") || httpServletRequest.getMethod().equals("PUT")) {
                ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpServletRequest);
                chain.doFilter(wrappedRequest, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}