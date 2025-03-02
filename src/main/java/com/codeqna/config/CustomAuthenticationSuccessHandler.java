package com.codeqna.config;

import com.codeqna.entity.Users;
import com.codeqna.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        Users users = userRepository.findByEmail(email).orElseThrow();
        if (users.getUser_condition().equals("N")) {
            response.sendRedirect("/main");
        } else {
            SecurityContextHolder.clearContext();
            request.getSession().invalidate();
            response.sendRedirect("/users/login/expired");
        }
    }


}
