def call(String dockerRegistry, String dockerImageTag, String helmChartName, String awsCredID, String awsRegion, String eksClusterName, String kubernetesNamespace = 'default') {
    kubernetesEKSHelmDeployEnv(dockerRegistry, dockerImageTag, helmChartName, awsCredID, awsRegion, eksClusterName, kubernetesNamespace)
}
