pipeline {
    agent any

    environment {
        // Define Docker image tag and credentials ID
        DOCKER_IMAGE = 'neupinion/neupinion:1.0'
        DOCKER_CREDS = 'docker-hub'
    }

    stages {
        stage('Prepare Environment') {
            steps {
                dir('neupinion-build') {
                    // Make the Gradle wrapper script executable
                    sh 'chmod +x gradlew'
                }
            }
        }

        stage('Build with Gradle') {
            steps {
                dir('neupinion-build') {
                    // Clean and build the project using Gradle wrapper
                    sh './gradlew clean bootJar'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('neupinion-build') {
                    // Build the Docker image
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                 dir('neupinion-build') {
                    // Log in to Docker Hub
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDS, usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
                    }
                    // Push the image to Docker Hub
                    sh "docker push ${DOCKER_IMAGE}"
                }
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
