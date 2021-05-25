package net.vanderkast.fs4r.service.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebMvc
@ConfigurationProperties(prefix = "fs4r.web")
public class WebConfiguration implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebConfiguration.class);

    private final String[] allowedOrigins;

    public WebConfiguration(@Nullable @Value("${fs4r.web.cors-allowed-origins:}") String[] allowedOrigins) {
        if (allowedOrigins == null || allowedOrigins.length == 0 || allowedOrigins[0] == null)
            this.allowedOrigins = new String[]{"http://localhost:3000"}; // ?standard? web-app port
        else
            this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setPatternParser(new PathPatternParser());
    }

    @SuppressWarnings("ConstantConditions")
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        logger.info("CORS allowed origins: {}", Arrays.toString(allowedOrigins));
        return source;
    }
}
