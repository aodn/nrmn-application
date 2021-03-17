package au.org.aodn.nrmn.restapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WormsClientConfig {

    @Bean
    public WebClient wormsClient(@Value("${app.worms.restapi.url}") String wormsRestApiUrl) {
        return WebClient.create(wormsRestApiUrl);
    }
}
