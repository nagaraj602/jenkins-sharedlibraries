def call(String dockerRegistry, String dockerImageTag, String dockerfilePath = 'Dockerfile') {
    sh """
        echo "Building Docker image: ${dockerRegistry}:${dockerImageTag} from ${dockerfilePath}"
        docker image build -f ${dockerfilePath} -t ${dockerRegistry} .
        docker image tag ${dockerRegistry} ${dockerRegistry}:${dockerImageTag}
        docker image tag ${dockerRegistry} ${dockerRegistry}:latest
    """
}
