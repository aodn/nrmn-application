# nrmn-api
REST API component for the NRMN system

## Setup and run instructions
1. Clone the repo
1. Update the credential in _/src/main/resources/application.properties_ as appropriate `temporary step until environment config is implemented`
1. Run the command below to start the application `also temporary until a WAR file has been built`
    ```
    mvn spring-boot:run
    ```

## Testing the setup
1. Navigate to localhost:8080 in a browser - you should be redirected to the login function
1. Login as per the [Spring Security](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-security) documentation - this should redirect you to the HAL Browser Explorer page
1. Drill down using the GET Links to ensure database connections are working
