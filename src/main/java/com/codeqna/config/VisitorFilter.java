package com.codeqna.config;

import com.codeqna.service.UserService;
import com.codeqna.service.VisitorService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class VisitorFilter implements Filter {

    private final VisitorService visitorService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        //아이피가져오고
        String ipAddr = req.getRemoteAddr();

        //저장해주고
        visitorService.saveIp(ipAddr);

        filterChain.doFilter(servletRequest, servletResponse);

    }
}
