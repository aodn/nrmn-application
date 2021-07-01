package au.org.aodn.nrmn.restapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RLSClientConfig {

    @Bean
    public WebClient rlsClient(@Value("${app.rls.api.url}") String rlsApiUrl) {
        return WebClient.builder().baseUrl(rlsApiUrl).exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(20 * 1024 * 1024))
                .build())
                .build();
    }
}
