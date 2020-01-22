#!groovy​

pipeline {
    agent none

    stages {
        stage('container-build') {
            agent {
                dockerfile {
                    args '-v ${HOME}/.m2:/home/builder/.m2 -v ${HOME}/bin:${HOME}/bin'
                    additionalBuildArgs '--build-arg BUILDER_UID=$(id -u)'
                }
            }
            stages {
                stage('clean') {
                    steps {
                        sh 'git reset --hard'
                    }
                }
                stage('package') {
                    steps {
                        sh './build.sh'
                    }
                }
            }
            post {
                success {
                    dir('api/target/') {
                        archiveArtifacts artifacts: 'rest-api-*.jar', fingerprint: true, onlyIfSuccessful: true
                    }
                }
            }
        }
    }
}
