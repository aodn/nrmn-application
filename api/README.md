# nrmn-api
REST API component for the NRMN system

## Setup and run instructions
1. Clone the repo
1. Update the credential in _/src/main/resources/application.properties_ as appropriate
1. Run the command below to start the REST api
    ```
    mvn spring-boot:run
    ```

## Testing the setup
1. Navigate to localhost:8080 in a browser - you should be redirected to the login function
1. Login as per the [Spring Security](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-security) documentation
