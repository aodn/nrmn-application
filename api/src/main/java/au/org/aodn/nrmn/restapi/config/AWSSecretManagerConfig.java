package au.org.aodn.nrmn.restapi.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * This is used to load the secret manager for jdbc connection, we will walk away from the username password
 * code into the cloud-deploy config and use aws secret manage instead
 */
@Configuration
public class AWSSecretManagerConfig {
    /**
     * Create this datasource connection if the aws.secure exist, else fall back to use postgres jdbc in dev machine
     * @param properties - Custom datasource properties
     * @return Custom datasource
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = {"spring.datasource.aws.secure.url", "spring.datasource.aws.secure.secretId"})
    public DataSource createAwsSecretConnection(AWSSecretManagerProperties properties) {

        // Populate the user property with the secret ARN to retrieve user and password from the secret
        Properties info = new Properties( );
        info.put( "user", properties.getSecretId());

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setUrl(properties.getUrl());
        dataSource.setDriverClassName("com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver");
        dataSource.setConnectionProperties(info);

        return dataSource;
    }
}
