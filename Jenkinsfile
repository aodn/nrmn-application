pipeline {
    agent none

    options {
        disableConcurrentBuilds()
    }

    stages {
        stage('container') {
            agent {
                dockerfile {
                    args '-v ${HOME}/.m2:/home/builder/.m2 -v ${HOME}/.cache:/home/builder/.cache -v ${HOME}/bin:${HOME}/bin'
                    additionalBuildArgs '--build-arg BUILDER_UID=$(id -u)'
                }
            }
            stages {
                stage('clean') {
                    steps {
                        sh 'rm -rf ui/node api/target'
                        sh 'git reset --hard'
                        sh 'git clean -xffd -e ui/node_modules'
                    }
                }
                stage('set_version_build') {
                    when { not { branch "master" } }
                    steps {
                        sh './bumpversion.sh build'
                    }
                }
                stage('set_version_release') {
                    when { branch "master" }
                    steps {
                        withCredentials([usernamePassword(credentialsId: env.GIT_CREDENTIALS_ID, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                            sh './bumpversion.sh release'
                        }
                    }
                }
                stage('build') {
                    steps {
                        sh 'mvn -B clean package'
                    }
                }
            }
            post {
                success {
                    dir('app/target/') {
                        archiveArtifacts artifacts: '*.war', fingerprint: true, onlyIfSuccessful: true
                    }
                }
            }
        }
    }
}
