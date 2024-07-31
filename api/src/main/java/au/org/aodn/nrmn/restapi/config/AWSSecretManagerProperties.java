package au.org.aodn.nrmn.restapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix="spring.datasource.aws.secure")
public class AWSSecretManagerProperties {

    private String url;
    private String secretId;
}
