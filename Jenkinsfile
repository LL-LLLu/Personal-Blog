pipeline {
    agent any

    environment {
        // Define registry if needed, or keep local
        DOCKER_REGISTRY = "" 
        // Email recipient
        EMAIL_TO = "qilutx@gmail.com" 
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend Tests') {
            agent {
                                docker {
                                    image 'maven:3.8-openjdk-8' 
                                    args '-v /root/.m2:/root/.m2 --entrypoint=""'
                                }            }
            steps {
                // Run tests ONLY. If this fails, the pipeline STOPS.
                // We use -Dmaven.test.failure.ignore=false to ensure build fails on test failure
                sh 'mvn -f weblog/weblog-springboot/pom.xml test'
            }
        }

        stage('Build Backend') {
            agent {
                                docker {
                                    image 'maven:3.8-openjdk-8' 
                                    args '-v /root/.m2:/root/.m2 --entrypoint=""'
                                }            }
            steps {
                // Build the JAR (skip tests here since we just ran them)
                sh 'mvn -f weblog/weblog-springboot/pom.xml clean package -DskipTests'
            }
        }

        stage('Frontend Tests') {
            agent {
                docker { image 'node:16-alpine' }
            }
            steps {
                script {
                    dir('blog-vue3') {
                        sh 'npm install'
                        // Run linting to check for syntax errors
                        sh 'npm run lint'
                    }
                }
            }
        }

        stage('Build Frontend') {
            agent {
                docker { image 'node:16-alpine' }
            }
            steps {
                script {
                    dir('blog-vue3') {
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

    post {
        success {
            mail to: "${EMAIL_TO}",
                 subject: "Build Success: ${currentBuild.fullDisplayName}",
                 body: "Great news! The build and deployment for ${currentBuild.fullDisplayName} was successful.\n\nCheck console output at ${env.BUILD_URL}"
        }
        failure {
            mail to: "${EMAIL_TO}",
                 subject: "Build Failed: ${currentBuild.fullDisplayName}",
                 body: "Alert! The build failed for ${currentBuild.fullDisplayName}.\n\nCheck console output at ${env.BUILD_URL}"
        }
    }
}
