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
            steps {
                script {
                    // Build the JAR using a temporary Maven container
                    // This matches "Step 5: Maven Package" from the guide, but without installing Maven manually
                    sh 'docker run --rm -v "${WORKSPACE}/weblog/weblog-springboot":/usr/src/mymaven -w /usr/src/mymaven maven:3.8-openjdk-8 mvn clean package -DskipTests'
                }
            }
        }

        stage('Deploy to Host') {
            steps {
                // This matches "Step 8: Upload Jar & Restart" from the guide
                // We use the 'Publish Over SSH' plugin logic via pipeline code
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'AWS-Server', // Must match the name you set in Jenkins System Config
                        transfers: [
                            sshTransfer(
                                // Source: The JAR we just built in the workspace
                                sourceFiles: 'weblog/weblog-springboot/weblog-web/target/weblog-web-0.0.1-SNAPSHOT.jar',
                                // Remove the long path prefix so it lands in the right place
                                removePrefix: 'weblog/weblog-springboot/weblog-web/target',
                                // Destination: The mapped volume folder on the host
                                remoteDirectory: 'weblog/weblog-springboot/weblog-web/target', 
                                // Command: Restart the container to pick up the new JAR
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