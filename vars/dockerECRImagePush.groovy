def call(String dockerRegistry, String dockerImageTag, String awsRegion, String ecrRepoName = '', String awsCredID = 'awsCred') {
    def targetRepo = ecrRepoName ?: dockerRegistry.tokenize('/').last()

    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: awsCredID]]) {
        sh """
            if ! command -v aws > /dev/null; then
                echo "AWS CLI not found. Installing AWS CLI v2..."
                curl -s "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                dpkg-query -l unzip > /dev/null 2>&1 || (sudo apt-get update && sudo apt-get -y install unzip)
                unzip -q awscliv2.zip
                sudo ./aws/install --update > /dev/null 2>&1 || true
                rm -rf awscliv2.zip aws
            fi

            aws configure set aws_access_key_id \$AWS_ACCESS_KEY_ID
            aws configure set aws_secret_access_key \$AWS_SECRET_ACCESS_KEY
            aws configure set region ${awsRegion}

            # Check if ECR repo exists; create if not
            if ! aws ecr describe-repositories --repository-names ${targetRepo} --region ${awsRegion} > /dev/null 2>&1; then
                echo "Creating ECR repository: ${targetRepo}"
                aws ecr create-repository --repository-name ${targetRepo} --region ${awsRegion}
            else
                echo "ECR repository ${targetRepo} exists."
            fi

            # Login to Amazon ECR
            aws ecr get-login-password --region ${awsRegion} | docker login --username AWS --password-stdin ${dockerRegistry}

            # Push images
            docker image push ${dockerRegistry}:${dockerImageTag}
            docker image push ${dockerRegistry}:latest
        """
    }
}
