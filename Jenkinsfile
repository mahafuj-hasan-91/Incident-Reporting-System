pipeline {
    agent any

    environment {
        // Change these variables to match your actual details
        DOCKER_IMAGE = 'byteBender0/incident-reporting-system'
        REGISTRY_CREDS_ID = 'dockerhub-pwd'
    }

    stages {
        stage('1. Checkout Code') {
            steps {
                // Fetches code from your GitHub repository
                checkout([$class: 'GitSCM',
                    branches: [[name: '*/main']],
                    extensions: [],
                    userRemoteConfigs: [[url: 'https://github.com/mahafuj-hasan-91/Incident-Reporting-System.git']]
                ])
            }
        }

        stage('2. Maven Build') {
            steps {
                // Compiles code and packages it, skipping tests to save time here
                // We use chmod to ensure the wrapper script is executable
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('3. Unit Testing') {
            steps {
                // Runs the unit tests specifically
                // If tests fail, the pipeline stops here preventing bad code from being dockerized
                sh './mvnw test'
            }
            post {
                always {
                    // Optional: Captures test results for Jenkins UI visualization
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('4. Docker Build') {
            steps {
                script {
                    // Builds the image using the current build number as a tag
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                }
            }
        }

        stage('5. Push to Registry') {
            steps {
                script {
                    withCredentials([string(credentialsId: "${REGISTRY_CREDS_ID}", variable: 'DOCKER_PASSWORD')]) {
                        // Securely logs in and pushes the image
                        sh "echo $DOCKER_PASSWORD | docker login -u byteBender0 --password-stdin"
                        sh "docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                    }
                }
            }
        }
    }
}