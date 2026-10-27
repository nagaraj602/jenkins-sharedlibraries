def call(String projectName, String projectKey, String sonarServerName = 'sonar-server', String sonarCredentialsId = 'sonarToken') {
    def scannerHome = tool 'sonar-scanner'
    withSonarQubeEnv(sonarServerName) {
        sh """
            ${scannerHome}/bin/sonar-scanner \
                -Dsonar.projectName="${projectName}" \
                -Dsonar.projectKey="${projectKey}" \
                -Dsonar.sources=.
        """
    }
    // Hard fail the pipeline if Quality Gate thresholds are breached
    waitForQualityGate abortPipeline: true, credentialsId: sonarCredentialsId
}
