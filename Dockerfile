FROM ubuntu:22.04

ARG BUILDER_UID=9999

ENV TZ="Australia" \
    NVM_DIR="/opt/nvm" \
    MAVEN_HOME="/opt/maven" \
    MAVEN_VERSION=3.8.5 \
    NODE_MAJOR=18 \
    HOME="/home/builder" \
    JAVA_TOOL_OPTIONS="-Duser.home=/home/builder"

# Install required packages and download the Amazon Corretto 11 JDK
RUN apt-get update && \
    apt-get install -y wget software-properties-common && \
    wget -O- https://apt.corretto.aws/corretto.key | apt-key add - && \
    add-apt-repository 'deb https://apt.corretto.aws stable main' && \
    apt-get update && \
    apt-get install -y java-17-amazon-corretto-jdk
# Set the JAVA_HOME environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto

RUN apt-get install -y git python3 python3-pip curl wget unzip zip



# Install Node
RUN apt-get update \
    && apt-get install -y ca-certificates curl gnupg \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list

RUN apt-get update \
    && apt-get install nodejs -y

RUN corepack enable
RUN npm install -g --force yarn && yarn set version 4.3.0
RUN yarn --version



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
