pipeline {
    agent any

    environment {
        // Define database/minio hosts as 'host.docker.internal' or use the network alias if running inside the same docker network
        DOCKER_REGISTRY = "my-docker-registry" // Replace with your actual registry if pushing to one
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                script {
                    // We use a temporary docker container to build the maven project
                    // This avoids needing to install Maven on the Jenkins server itself
                    sh 'docker run --rm -v "${WORKSPACE}/weblog/weblog-springboot":/usr/src/mymaven -w /usr/src/mymaven maven:3.8-openjdk-8 mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                script {
                    // Similarly, use a Node container to build the frontend
                    sh 'docker run --rm -v "${WORKSPACE}/blog-vue3":/app -w /app node:16-alpine sh -c "npm install && npm run build"'
                }
            }
        }

        stage('Build & Deploy Docker Containers') {
            steps {
                dir('docker') {
                    script {
                        // Tear down old containers and bring up new ones
                        // --build ensures we use the artifacts we just created
                        sh 'docker-compose down'
                        sh 'docker-compose up -d --build'
                    }
                }
            }
        }
    }
    
    post {
        always {
            cleanWs() // Clean up workspace to save disk space
        }
    }
}