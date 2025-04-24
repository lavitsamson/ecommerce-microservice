package api_gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, AuthFilter authFilter) {
        return builder.routes()
            .route("secured_route", r -> r.path("/secured/**")
                .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                .uri("lb://your-service"))
            .build();
    }
}