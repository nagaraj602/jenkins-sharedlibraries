def call(String dockerImageName, String dockerImageTag, String dockerHubCredID) {
    withCredentials([usernamePassword(credentialsId: dockerHubCredID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
        sh """
            echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin
            docker image push ${dockerImageName}:${dockerImageTag}
            docker image push ${dockerImageName}:latest
        """
    }
}
