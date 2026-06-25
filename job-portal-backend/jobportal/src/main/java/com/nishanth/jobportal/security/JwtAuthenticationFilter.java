package com.nishanth.jobportal.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // POLISHED: Replaced System.out with production-grade SLF4J Logger
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // POLISHED: Only process token if Header exists AND user is not already authenticated in this thread context
        if (authHeader != null && authHeader.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authHeader.substring(7);
            
            try {
                if (jwtUtils.validateToken(token)) {
                    String email = jwtUtils.getEmailFromToken(token);
                    String role = jwtUtils.getRoleFromToken(token);

                    // Add ROLE_ prefix for Spring Security hasRole() compatibility
                    String formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    
                    // POLISHED: Non-blocking debug statement
                    log.debug("Authentication successful for principal: {} with authority: {}", email, formattedRole);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            email, 
                            null, 
                            AuthorityUtils.createAuthorityList(formattedRole)
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception ex) {
                // POLISHED: Prevent silent validation drops by capturing explicit security context anomalies
                log.error("Failed to set user authentication inside security context handler", ex);
            }
        }
        
        // Hand off tracking execution down the Filter Chain pipeline safely
        filterChain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // MENTOR FIX: Bypass token validation for public auth endpoints AND browser OPTIONS preflight handshakes
      return path.startsWith("/api/auth/") || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}