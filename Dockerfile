FROM amazoncorretto:11-al2-jdk

ARG BUILDER_UID=9999

ENV TZ="Australia" \
    MAVEN_HOME="/opt/maven" \
    MAVEN_VERSION=3.8.5 \
    NODE_MAJOR=18 \
    HOME="/home/builder" \
    JAVA_TOOL_OPTIONS="-Duser.home=/home/builder"

RUN yum install --quiet --assumeyes git python3 python3-pip apache-maven shadow-utils tar

# Install Node
RUN apt-get update \
    && apt-get install -y ca-certificates curl gnupg \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list

RUN apt-get update \
    && apt-get install nodejs -y \
    && npm install -g yarn \
    && yarn set version canary

# Install Maven
RUN set -ex \
    && mkdir -p $MAVEN_HOME \
    && curl --location --silent --output /var/tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz https://archive.apache.org/dist/maven/maven-3//$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
    && tar xzf /var/tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz -C $MAVEN_HOME --strip-components=1 \
    && rm /var/tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz \
    && update-alternatives --install /usr/bin/mvn mvn /opt/maven/bin/mvn 10000

RUN pip3 install bump2version==1.0.1
RUN /usr/sbin/useradd --no-create-home --no-log-init --shell /bin/bash --uid $BUILDER_UID builder
RUN chown -R builder:builder /home/builder
USER builder
WORKDIR /home/builder
