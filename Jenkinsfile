pipeline {
    agent any
    triggers {
        githubPush()
    }
    tools {
        maven 'maven-3.9'
    }
    environment {
        DOCKER_IMAGE = 'tool-management-app'
        DOCKER_TAG = "${BUILD_NUMBER}"
        CONTAINER_NAME = 'tool-management-container'
    }
    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'master', url: 'https://github.com/JonFaull/ToolManagementMicroservice.git'
            }
        }
        stage('Static Code Analysis') {
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    sh 'mvn clean verify sonar:sonar'
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
        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests=false'
            }
        }
        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                }
            }
        }
        stage('Stop Old Container') {
            steps {
                script {
                    sh "docker stop ${CONTAINER_NAME} || true"
                    sh "docker rm ${CONTAINER_NAME} || true"
                }
            }
        }
        stage('Deploy Container') {
            steps {
                script {
                    sh """
                        docker run -d \
                        --name ${CONTAINER_NAME} \
                        -p 8081:8080 \
                        ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }
    }
    post {
        success {
            echo "Deployment successful! Application is running at http://localhost:8081"
        }
        failure {
            echo "Pipeline failed. Check logs for details."
        }
    }
}