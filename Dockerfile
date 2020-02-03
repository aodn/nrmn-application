FROM ubuntu:16.04

ARG BUILDER_UID=9999

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV JAVA_TOOL_OPTIONS -Duser.home=/home/builder

# maven
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    libnetcdf11 \
    libgsl2 \
    libudunits2-0 \
    libxml2-utils \
    openjdk-8-jdk \
    maven \
    && rm -rf /var/lib/apt/lists/*


# dependencies for nodejs and yarn
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    wget \
    xz-utils \
    openssl \
    && rm -rf /var/lib/apt/lists/*

ENV NODE_VERSION 12.13.1
ENV NODE_ARCH x64
ENV TMP /tmp
ENV NODE_FILEPATH node-v$NODE_VERSION-linux-$NODE_ARCH

# nodejs
RUN wget -q https://nodejs.org/dist/v$NODE_VERSION/$NODE_FILEPATH.tar.xz -O $TMP/$NODE_FILEPATH.tar.xz \
    && cd $TMP/ && tar -xJf $NODE_FILEPATH.tar.xz && rm $NODE_FILEPATH.tar.xz \
    && mv $NODE_FILEPATH /opt/node \
    && ln -sf /opt/node/bin/node /usr/bin/node \
    && ln -sf /opt/node/bin/npm /usr/bin/npm


# yarn
RUN wget -q https://yarnpkg.com/latest.tar.gz -O $TMP/latest.tar.gz \
    && cd $TMP/ && tar -zxf latest.tar.gz && rm latest.tar.gz \
    && mv $TMP/yarn* /opt/yarn \
    && ln -sf /opt/yarn/bin/yarn /usr/bin/yarn

RUN useradd --create-home --no-log-init --shell /bin/bash --uid $BUILDER_UID builder
USER builder

WORKDIR /home/builder
