def call(String scanType, String target, String severity = 'HIGH,CRITICAL', int exitCode = 0, String reportOutput = '') {
    sh """
        if ! command -v trivy > /dev/null; then
            echo "Installing Trivy security scanner..."
            curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sudo sh -s -- -b /usr/local/bin
        fi

        case "${scanType}" in
            fs)
                echo "Running Trivy Filesystem Scan on ${target}..."
                trivy fs --severity ${severity} --exit-code ${exitCode} ${target}
                ;;
            config)
                echo "Running Trivy Config Scan on ${target}..."
                trivy config --severity ${severity} --exit-code ${exitCode} ${target}
                ;;
            image)
                echo "Running Trivy Container Image Scan on ${target}..."
                if [ -n "${reportOutput}" ]; then
                    trivy image --severity ${severity} --exit-code ${exitCode} -f json -o ${reportOutput} ${target}
                else
                    trivy image --severity ${severity} --exit-code ${exitCode} ${target}
                fi
                ;;
            *)
                echo "Unknown Trivy scan type: ${scanType}"
                exit 1
                ;;
        esac
    """
}
