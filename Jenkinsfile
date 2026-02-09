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
    }
}
