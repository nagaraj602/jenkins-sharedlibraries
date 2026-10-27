# Jenkins Shared Libraries for Marketplace Risk Detection Platform

This repository provides reusable, standardized Jenkins pipeline steps (vars) for the **Marketplace Seller Risk & Abuse Detection Platform**.

## 📁 Pipeline Steps (`vars/`)

| Step | Description |
| :--- | :--- |
| `gitCheckout(repo, branch, credId)` | Authenticated Git checkout supporting feature, dev, staging, and master branches |
| `dockerImageBuild(imageName, tag, dockerfile)` | Container image build with multi-stage build optimization and tagging |
| `dockerECRImagePush(image, tag, region, repo, credId)` | Automates AWS CLI v2 setup, ECR repository validation, login, and image push |
| `dockerHubImagePush(image, tag, credId)` | Docker Hub push with credential bindings |
| `trivyScan(scanType, target, severity, exitCode, report)` | Trivy security scanner for `fs`, `config` (Dockerfile misconfigurations), and `image` vulnerabilities |
| `sonarAnalysis(projectName, projectKey, server, credId)` | SonarQube static code analysis with quality gate hard failure gating |
| `kubernetesEKSHelmDeployEnv(reg, tag, chart, cred, region, cluster, ns)` | Deploys microservices via Helm using environment values (`DEV.yaml`, `UAT.yaml`, `PROD.yaml`) |
| `mavenBuild(goals)` | Apache Maven build runner for Java microservices |

## ⚙️ Configuration in Jenkins

1. Go to **Manage Jenkins** > **System**.
2. Scroll to **Global Pipeline Libraries**.
3. Add a new library named `sharedlibrary`:
   - Default version: `main` (or `master`)
   - Retrieval method: **Modern SCM** (Git)
   - Project Repository: `https://github.com/nagaraj602/jenkins-sharedlibraries.git`
   - Credentials: `githubCred`
