pipeline {
    // Define a default agent for the entire pipeline.
    // This can be 'any' to run on the Jenkins controller,
    // or 'none' if every stage defines its own agent.
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
            // Define the Docker agent specifically for this stage
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
            // This stage runs on the default agent (e.g., Jenkins controller)
            // as it needs to access the host's Docker daemon via SSH publisher.
            agent any 
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