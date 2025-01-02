package com.diagnosis_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;

    @Value("${user-service.base-url}")
    private String userServiceBaseUrl;

    public AuthenticationFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || authHeader.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing Authorization header");
            return;
        }

        try {
            String validationUrl = userServiceBaseUrl + "/validate";

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> validationResponse = restTemplate.exchange(validationUrl, HttpMethod.GET, requestEntity, Map.class);

            // Validate the response
            if (!validationResponse.getStatusCode().is2xxSuccessful()) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid token");
                return;
            }

            Map<Object, Object> responseBody = validationResponse.getBody();

            if (responseBody == null || responseBody.get("token") == null
                    || "Invalid access token".equals(responseBody.get("token"))) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid token");
                return;
            }

            Object userIdObj = responseBody.get("user_id");
            if (userIdObj == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Missing user_id in validation response");
                return;
            }

            Long userId = Long.parseLong(userIdObj.toString());
            request.setAttribute("user_id", userId);

        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Authorization service is unavailable");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
