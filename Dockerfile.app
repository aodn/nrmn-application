FROM tomcat:jdk17

ENV SPRING_PROFILES_ACTIVE=dev

ENV SPRING_DATASOURCE_USERNAME=nrmn_dev
ENV SPRING_DATASOURCE_PASSWORD=nrmn_dev

ENV CATALINA_OPTS="-Dapp.jwt.secretBase64=3zb9O314fW9abnfghxGK7Llab4ZfMemvPsMO8mUjvSKKmTsQya5wZqw6RC9KQPa"

COPY app/target/nrmn-app-0.0.0.war /usr/local/tomcat/webapps/ROOT.war
