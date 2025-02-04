package com.school.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.common.exception.ErrorResponse;
import com.school.security.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String key = resolveKey(request);
        Bucket bucket = rateLimitConfig.resolveBucket(key);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            handleRateLimitExceeded(response);
        }
    }

    private String resolveKey(@NonNull HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        return ip + ":" + path;
    }

    private void handleRateLimitExceeded(@NonNull HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.TOO_MANY_REQUESTS.value(),
            "Rate limit exceeded. Please try again later."
        );

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip rate limiting for static resources and documentation
        return path.contains("/swagger-ui") || 
               path.contains("/v3/api-docs") || 
               path.contains("/static/");
    }
}
