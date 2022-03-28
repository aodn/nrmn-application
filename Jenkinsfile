pipeline {
    agent none

    options {
        disableConcurrentBuilds()
    }

    stages {
        stage('container') {
            agent {
                dockerfile {
                    args '--volume ${HOME}/.m2:/home/builder/.m2 --volume ${HOME}/.cache:/home/builder/.cachejournal/ --volume ${HOME}/bin:${HOME}/bin'
                    additionalBuildArgs '--build-arg BUILDER_UID=$(id -u)'
                }
            }
            stages {
                stage('clean') {
                    steps {
                        sh 'git reset --hard'
                        sh 'git clean --force --force -xd --exclude=web/node_modules'
                    }
                }
                stage('set_version_release') {
                    when { branch "master" }
                    steps {
                        withCredentials([usernamePassword(credentialsId: env.GIT_CREDENTIALS_ID, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                            sh './bumpversion.sh'
                        }
                    }
                }
                stage('build') {
                    steps {
                        sh 'yarn --cwd web --frozen-lockfile'
                        sh 'mvn --batch-mode --threads 2 clean package'
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
