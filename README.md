# Jenkins Shared Libraries for Movie Analyzer Platform

[![Jenkins](https://img.shields.io/badge/Jenkins-LTS-red?logo=jenkins)](https://www.jenkins.io/)
[![Java](https://img.shields.io/badge/Java-25%20LTS-orange.svg)](https://adoptium.net/)
[![Groovy](https://img.shields.io/badge/Language-Groovy-blue.svg)](https://groovy-lang.org/)
[![GitHub Repository](https://img.shields.io/badge/GitHub-nagaraj602%2Fjenkins--sharedlibraries-blue?logo=github)](https://github.com/nagaraj602/jenkins-sharedlibraries.git)

This repository provides reusable, standardized Jenkins CI/CD pipeline steps (`vars`) for the **Movie Analyzer Platform**, maintained by **nagaraj602**. It modularizes build, containerization, security scanning (Trivy, SonarQube), AWS ECR image distribution, and automated Amazon EKS 1.37 Helm deployments across development, staging, and production environments.

---

## 🔗 Platform Ecosystem & Repositories

| Component | Repository Link | Tech Stack & Role |
| :--- | :--- | :--- |
| **Main Orchestration & IaC** | [movie-analyzer-project](https://github.com/nagaraj602/movie-analyzer-project.git) | Terraform (AWS EKS 1.37, RDS PostgreSQL 17, VPC, Jenkins), Docker Compose |
| **Backend API Service** | [project-movie-analyzer-backend](https://github.com/nagaraj602/project-movie-analyzer-backend.git) | Java 21/25 LTS, Spring Boot 3.5, Hibernate/JPA, Actuator (Port `7280`) |
| **Frontend Portal** | [project-movie-analyzer-frontend](https://github.com/nagaraj602/project-movie-analyzer-frontend.git) | React 19, Express 4, Node 24 LTS (Port `7180`) |
| **AI/ML Model Service** | [project-movie-analyzer-model](https://github.com/nagaraj602/project-movie-analyzer-model.git) | Python 3.13/3.15, Flask 3.1, TextBlob, NLTK (Port `7380`) |
| **Jenkins Shared Libraries** | [jenkins-sharedlibraries](https://github.com/nagaraj602/jenkins-sharedlibraries.git) | Reusable Jenkins Groovy pipelines (Trivy, SonarQube, Helm, ECR) |

---

## 📁 Shared Pipeline Steps (`vars/`)

| Step | Parameters | Description |
| :--- | :--- | :--- |
| `gitCheckout` | `(repoUrl, branch, credId)` | Authenticated Git checkout for PRs and multibranch builds |
| `dockerImageBuild` | `(imageName, dockerTag, dockerfile = 'Dockerfile')` | Builds container images using Docker CLI |
| `dockerECRImagePush` | `(registry, tag, region, repoName, credId)` | Authenticates to Amazon ECR via AWS CLI v2 and pushes images |
| `dockerHubImagePush` | `(registry, tag, credId)` | Authenticates and pushes images to Docker Hub registry |
| `trivyScan` | `(scanType, target, severity, exitCode, reportName)` | Security scanner for filesystem, Dockerfiles, and image vulnerabilities |
| `sonarAnalysis` | `(projectName, projectKey, sonarServer, credId)` | Executes SonarQube static code quality analysis and checks quality gate |
| `kubernetesEKSHelmDeploy` | `(chartName, namespace)` | Deploys Helm chart to EKS using environment values (`DEV.yaml`, `STAGING.yaml`, `PROD.yaml`) |
| `kubernetesEKSHelmDeployEnv` | `(registry, tag, chart, cred, region, cluster, ns)` | Full-parameter EKS Helm deployment with automated AWS/Helm CLI provisioning |
| `mavenBuild` | `(goals = 'clean package -DskipTests')` | Executes Maven builds with clean target packaging |

---

## ⚙️ Prerequisites & Jenkins Controller Requirements

### 1. Jenkins Controller Configuration
* **Jenkins Controller**: Jenkins LTS running on Java 25 LTS (recommended image: `jenkins/jenkins:lts-jdk25`).
* **Required Jenkins Plugins**:
  * **Multibranch Scan Webhook Trigger** (`multibranch-scan-webhook-trigger`): Real-time build triggering on GitHub push and pull requests.
  * **Pipeline: Shared Groovy Libraries** (`workflow-cps-global-lib`): Global shared library integration.
  * **GitHub Branch Source** (`github-branch-source`): Automatic branch & PR discovery.
  * **SonarQube Scanner for Jenkins** (`sonar`): Code quality evaluation and quality gate enforcement.
  * **Docker Pipeline** (`docker-workflow`): Docker build and agent execution.
  * **AWS Credentials Plugin** (`aws-credentials`): Seamless AWS IAM credentials binding.
  * **Credentials Binding Plugin** (`credentials-binding`): Secure injection of passwords, secrets, and SSH keys.

### 2. Jenkins Agent Node Tools
* **Docker Engine 24+** (with user added to `docker` group)
* **AWS CLI v2** (`aws`)
* **Helm v3+** (`helm`)
* **kubectl** compatible with Kubernetes 1.37
* **JDK 25 LTS** & **Maven 3.9+**
* **Python 3.13 / 3.15** & **Node.js 24 LTS**
* **Trivy CLI** (`trivy`)

### 3. Configured Credentials in Jenkins
* `githubCred`: Username with Password (or GitHub PAT with `repo`, `admin:repo_hook` scopes).
* `awsCred`: AWS Access Key ID + Secret Key credentials binding.
* `sonarToken`: Secret text containing SonarQube user analysis token.

---

## 🛠️ Step-by-Step Setup Guide

### 1. Register Global Shared Library in Jenkins
1. Open Jenkins Dashboard -> **Manage Jenkins** -> **System** (or **Configure System**).
2. Scroll to **Global Pipeline Libraries**.
3. Click **Add**:
   * **Name**: `sharedlibrary` *(Must match `@Library('sharedlibrary')_` in microservice Jenkinsfiles)*
   * **Default version**: `main`
   * **Load implicitly**: Unchecked
   * **Allow default version to be overridden**: Checked
   * **Retrieval method**: Modern SCM -> **Git**
   * **Project Repository**: `https://github.com/nagaraj602/jenkins-sharedlibraries.git`
   * **Credentials**: `githubCred`
4. Click **Save**.

### 2. Configure SonarQube Server
1. Go to **Manage Jenkins** -> **System** -> **SonarQube servers**.
2. Add SonarQube installation:
   * **Name**: `sonar-server`
   * **Server URL**: `http://<jenkins-agent-ip>:9000`
   * **Server authentication token**: `sonarToken`
3. Go to **Manage Jenkins** -> **Tools** -> **SonarQube Scanner**:
   * **Name**: `sonar-scanner`
   * Check **Install automatically**.

### 3. Setup Multibranch Pipeline Jobs
For each microservice repository (`project-movie-analyzer-backend`, `project-movie-analyzer-frontend`, `project-movie-analyzer-model`):
1. Click **New Item** -> Name it (e.g., `movie-analyzer-backend`) -> Select **Multibranch Pipeline**.
2. Under **Branch Sources**:
   * Add source: **GitHub**
   * Repository HTTPS URL: `https://github.com/nagaraj602/<repo-name>.git`
   * Credentials: `githubCred`
   * Discover branches: `main master staging dev`
   * Discover pull requests: `The current pull request revision`
3. Under **Scan Multibranch Pipeline Triggers**:
   * Check **Scan by webhook**
   * Trigger token: `<repo-name>-trigger`
4. Under **Build Configuration**:
   * Script Path: `Jenkinsfile`
5. Click **Save**.

### 4. Configure GitHub Webhook
In GitHub repository **Settings** -> **Webhooks** -> **Add webhook**:
* **Payload URL**: `http://<JENKINS_PUBLIC_URL>/multibranch-webhook-trigger/invoke?token=<TRIGGER_TOKEN>`
* **Content type**: `application/json`
* **Events**: Pushes & Pull Requests
* **Active**: Checked
