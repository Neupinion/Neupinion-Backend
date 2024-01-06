pipeline {
    agent any

    environment {
        // Define Docker image tag and credentials ID
        DOCKER_IMAGE = 'neupinion/neupinion:1.0'
        DOCKER_CREDS = 'docker_hub'
    }

    stages {
        stage('Prepare Environment') {
            steps {
                // Make the Gradle wrapper script executable
                sh 'chmod +x gradlew'
            }
        }

        stage('Build with Gradle') {
            steps {
                // Clean and build the project using Gradle wrapper
                sh './gradlew clean bootJar'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Build the Docker image
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                // Log in to Docker Hub with --password-stdin
                withCredentials([usernamePassword(credentialsId: DOCKER_CREDS, usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "echo ${DOCKERHUB_PASSWORD} | docker login -u ${DOCKERHUB_USERNAME} --password-stdin"
                }
                // Push the image to Docker Hub
                sh "docker push ${DOCKER_IMAGE}"
            }
        }
    }

    post {
        always {
            // Post actions like cleaning up, notifications, etc.
            echo 'Build process completed.'
        }
    }
}
