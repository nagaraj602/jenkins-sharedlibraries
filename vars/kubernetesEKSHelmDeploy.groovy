def call(String helmChartName, String kubernetesNamespace = 'default') {
    def valuesFile = "PROD.yaml"
    if (kubernetesNamespace == "dev") {
        valuesFile = "DEV.yaml"
    } else if (kubernetesNamespace == "staging" || kubernetesNamespace == "uat") {
        valuesFile = "STAGING.yaml"
    } else if (kubernetesNamespace == "qa") {
        valuesFile = "QA.yaml"
    }

    sh """
        if ! command -v helm > /dev/null 2>&1; then
            echo "Installing Helm 3..."
            curl -fsSL https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
        fi
        echo "Deploying \${helmChartName} to namespace: \${kubernetesNamespace} using helm/values/\${valuesFile}"
        helm upgrade --install \${helmChartName} helm/ \
            --namespace \${kubernetesNamespace} \
            --create-namespace \
            -f helm/values/\${valuesFile}
    """
}

def call(String dockerRegistry, String dockerImageTag, String helmChartName, String awsCredID, String awsRegion, String eksClusterName, String kubernetesNamespace = 'default') {
    kubernetesEKSHelmDeployEnv(dockerRegistry, dockerImageTag, helmChartName, awsCredID, awsRegion, eksClusterName, kubernetesNamespace)
}
