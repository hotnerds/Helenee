package com.hotnerds.common.security.filter;

import antlr.StringUtils;
import com.hotnerds.common.security.oauth2.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.StringBuilders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        if(token != null && !"".equals(token) && jwtTokenProvider.validateToken(token)) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);
        }
        filterChain.doFilter(request, response);
    }
}