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
                    // Reuse the maven repo to speed up builds
                    args '-v /root/.m2:/root/.m2' 
                }
            }
            steps {
                // We are now INSIDE the maven container, with the workspace mounted automatically
                sh 'mvn -f weblog/weblog-springboot/pom.xml clean package -DskipTests'
            }
        }

        stage('Deploy to Host') {
            steps {
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'AWS-Server',
                        transfers: [
                            sshTransfer(
                                sourceFiles: 'weblog/weblog-springboot/weblog-web/target/weblog-web-0.0.1-SNAPSHOT.jar',
                                removePrefix: 'weblog/weblog-springboot/weblog-web/target',
                                remoteDirectory: 'weblog/weblog-springboot/weblog-web/target', 
                                execCommand: '''
                                    cd /home/ubuntu/Personal-Blog/docker
                                    docker-compose restart backend
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