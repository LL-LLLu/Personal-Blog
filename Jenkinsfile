pipeline {
    agent any

    environment {
        // Define registry if needed, or keep local
        DOCKER_REGISTRY = "" 
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            agent {
                docker { 
                    image 'maven:3.8-openjdk-8' 
                    args '-v /root/.m2:/root/.m2' 
                }
            }
            steps {
                sh 'mvn -f weblog/weblog-springboot/pom.xml clean package -DskipTests'
            }
        }

        stage('Build Frontend') {
            agent {
                docker {
                    image 'node:16-alpine'
                }
            }
            steps {
                script {
                    dir('blog-vue3') {
                        sh 'npm install'
                        sh 'npm run build'
                    }
                }
            }
        }

        stage('Deploy to Host') {
            agent any
            steps {
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'AWS-Server',
                        transfers: [
                            // Backend Deployment
                            sshTransfer(
                                sourceFiles: 'weblog/weblog-springboot/weblog-web/target/weblog-web-0.0.1-SNAPSHOT.jar',
                                removePrefix: 'weblog/weblog-springboot/weblog-web/target',
                                remoteDirectory: 'weblog/weblog-springboot/weblog-web/target', 
                                execCommand: '''
                                    cd /home/ubuntu/Personal-Blog/docker
                                    docker-compose restart backend
                                '''
                            ),
                            // Frontend Deployment
                            sshTransfer(
                                sourceFiles: 'blog-vue3/dist/**',
                                removePrefix: 'blog-vue3/dist',
                                remoteDirectory: 'blog-vue3/dist',
                                execCommand: '''
                                    # Ensure permissions are correct for Nginx
                                    chmod -R 755 /home/ubuntu/Personal-Blog/blog-vue3/dist
                                    cd /home/ubuntu/Personal-Blog/docker
                                    docker-compose restart frontend
                                '''
                            )
                        ],
                        usePromotionTimestamp: false,
                        useWorkspaceInPromotion: false,
                        verbose: true
                    )
                ])
            }
        }
    }
}