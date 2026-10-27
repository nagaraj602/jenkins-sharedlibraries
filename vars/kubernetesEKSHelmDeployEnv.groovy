def call(String dockerRegistry, String dockerImageTag, String helmChartName, String awsCredID, String awsRegion, String eksClusterName, String kubernetesNamespace = 'default') {
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: awsCredID]]) {
        sh """
            if ! command -v aws > /dev/null; then
                echo "AWS CLI not found. Installing..."
                curl -s "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                dpkg-query -l unzip > /dev/null 2>&1 || (sudo apt-get update && sudo apt-get -y install unzip)
                unzip -q awscliv2.zip
                sudo ./aws/install --update > /dev/null 2>&1 || true
                rm -rf awscliv2.zip aws
            fi

            aws configure set aws_access_key_id \$AWS_ACCESS_KEY_ID
            aws configure set aws_secret_access_key \$AWS_SECRET_ACCESS_KEY
            aws configure set region ${awsRegion}
            aws eks --region ${awsRegion} update-kubeconfig --name ${eksClusterName}

            if ! command -v helm > /dev/null; then
                echo "Installing Helm 3..."
                curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3
                chmod 700 get_helm.sh
                ./get_helm.sh
                rm -f get_helm.sh
            fi
        """
    }

    def valuesFile = "PROD.yaml"
    if (kubernetesNamespace == "dev") {
        valuesFile = "DEV.yaml"
    } else if (kubernetesNamespace == "uat" || kubernetesNamespace == "staging") {
        valuesFile = "UAT.yaml"
    }

    sh """
        echo "Deploying ${helmChartName} to namespace: ${kubernetesNamespace} using helm/values/${valuesFile}"
        helm upgrade --install ${helmChartName} helm/ \
            --namespace ${kubernetesNamespace} \
            --create-namespace \
            --set image.repository=${dockerRegistry} \
            --set image.tag=${dockerImageTag} \
            -f helm/values/${valuesFile}
    """
}
