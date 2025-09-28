package Oops.backend.config.s3;

import Oops.backend.config.ClientProperties;
import Oops.backend.domain.auth.AuthenticatedUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ClientProperties clientProperties;
    private final AuthenticatedUserArgumentResolver authenticatedUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedUserArgumentResolver);
    }
    public WebConfig(ClientProperties clientProperties,
                     AuthenticatedUserArgumentResolver authenticatedUserArgumentResolver) {
        this.clientProperties = clientProperties;
        this.authenticatedUserArgumentResolver = authenticatedUserArgumentResolver;
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(clientProperties.getHost().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*");
    }
}