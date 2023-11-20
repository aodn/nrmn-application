FROM amazoncorretto:11-al2-jdk

ARG BUILDER_UID=9999

ENV TZ="Australia" \
    NVM_DIR="/opt/nvm" \
    MAVEN_HOME="/opt/maven" \
    MAVEN_VERSION=3.8.5 \
    NODE_VERSION=18.18.2 \
    HOME="/home/builder" \
    JAVA_TOOL_OPTIONS="-Duser.home=/home/builder"

RUN yum install --quiet --assumeyes git python3 python3-pip apache-maven shadow-utils tar

# Install Node
RUN mkdir -p $NVM_DIR
RUN curl --silent https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash
RUN . "$NVM_DIR/nvm.sh" && nvm install ${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm use v${NODE_VERSION}
ENV PATH="/opt/nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"
RUN npm install -g yarn@1.22.4

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
