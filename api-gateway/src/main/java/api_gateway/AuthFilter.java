package api_gateway;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_PATH = "/api/auth";

    private final SecretKey key;

    public AuthFilter(@Value("${jwt.secret}") String secret) {
        super(Config.class);
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (isPublicEndpoint(request)) {
                return chain.filter(exchange);
            }

            String token = extractToken(request);
            validateToken(token);

            return chain.filter(exchange);
        };
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        return request.getURI().getPath().contains(AUTH_PATH);
    }

    private String extractToken(ServerHttpRequest request) {
        if (!request.getHeaders().containsKey(AUTHORIZATION_HEADER)) {
            throw new AuthenticationException("Missing Authorization Header");
        }

        String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException("Invalid Authorization Header Format");
        }

        return authHeader.substring(BEARER_PREFIX.length());
    }

    private void validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (JwtException e) {
            throw new AuthenticationException("Invalid JWT Token: " + e.getMessage());
        }
    }

    public static class Config {
        // Configuration properties if needed
    }

    private static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }
}