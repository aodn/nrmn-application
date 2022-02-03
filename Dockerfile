FROM ubuntu:20.04

ARG BUILDER_UID=9999
ARG DEBIAN_FRONTEND=noninteractive

ENV TZ="Australia"
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV HOME /home/builder
ENV JAVA_TOOL_OPTIONS -Duser.home=/home/builder

RUN apt-get update && apt-get install -y --no-install-recommends \
    software-properties-common \
    git-core \
    libxml2-utils \
    libnetcdf15 \
    libgsl23 \
    libudunits2-0 \
    python3-dev \
    maven \
	wget \
    && rm -rf /var/lib/apt/lists/*

RUN add-apt-repository ppa:openjdk-r/ppa

RUN apt-get update && apt-get install -y --no-install-recommends openjdk-11-jdk

RUN update-alternatives --install /usr/bin/python python /usr/bin/python3 10

RUN wget -q https://bootstrap.pypa.io/pip/3.5/get-pip.py \
    && python get-pip.py pip==18.1 setuptools==49.6.0 wheel==0.35.1 \
    && rm -rf get-pip.py
	
RUN pip install \
    bump2version==1.0.1

RUN useradd --create-home --no-log-init --shell /bin/bash --uid $BUILDER_UID builder
USER builder
WORKDIR /home/builder
