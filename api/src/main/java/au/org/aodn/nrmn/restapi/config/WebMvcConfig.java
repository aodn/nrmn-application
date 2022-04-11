package au.org.aodn.nrmn.restapi.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${frontend.pages.whitelist}")
    private List<String> frontendPagesWhitelist;

    @Value("${app.cors.max_age_secs}")
    private long MAX_AGE_SECS;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://*.aodn.org.au","http://*.dev.aodn.org.au")
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "DELETE")
                .maxAge(MAX_AGE_SECS);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        frontendPagesWhitelist.stream()
                .forEach(frontEndPage -> registry.addViewController(frontEndPage).setViewName("forward:/"));
    }
}
