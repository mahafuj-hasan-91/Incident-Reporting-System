pipeline {
    agent any

    environment {
        IMAGE_NAME = "YOUR_DOCKERHUB_USERNAME/incident-reporting-app"
        IMAGE_TAG  = "1.0.${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout Source') {
            steps {
                echo "Source code already checked out by Jenkins"
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t $IMAGE_NAME:$IMAGE_TAG ."
            }
        }

        stage('Push to DockerHub') {
            steps {
                withDockerRegistry([credentialsId: 'dockerhub', url: '']) {
                    sh "docker push $IMAGE_NAME:$IMAGE_TAG"
                    sh "docker tag $IMAGE_NAME:$IMAGE_TAG $IMAGE_NAME:latest"
                    sh "docker push $IMAGE_NAME:latest"
                }
            }
        }
    }

    post {
        success {
            echo "incident-reporting-app build and Docker push completed successfully"
        }
        failure {
            echo "Pipeline failed — fix errors before retrying"
        }
    }
}
