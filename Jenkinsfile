pipeline {
    agent any

    triggers {
        githubPush()
    }

    tools {
        maven 'maven-3.9'
    }

    stages {

        stage('Clone Repository') {
            steps {
                git branch: 'master', url: 'https://github.com/JonFaull/ToolManagementMicroservice.git'
            }
        }

        stage('Build + Test + Sonar') {
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    sh 'mvn clean verify sonar:sonar'
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Build Docker Image') {
            agent {
                docker {
                    image 'docker:24.0.5-dind'
                    args '--privileged'
                }
            }
            steps {
                sh 'docker build -t jonfaull/tool-app:${BUILD_NUMBER} .'
            }
        }

        stage('Push Docker Image') {
            agent {
                docker {
                    image 'docker:24.0.5-dind'
                    args '--privileged'
                }
            }
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-creds') {
                        sh 'docker push jonfaull/tool-app:${BUILD_NUMBER}'
                        sh 'docker tag jonfaull/tool-app:${BUILD_NUMBER} jonfaull/tool-app:latest'
                        sh 'docker push jonfaull/tool-app:latest'
                    }
                }
            }
        }
    }
}
