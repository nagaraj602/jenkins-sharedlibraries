# Jenkins Shared Libraries for Marketplace Risk Detection Platform

This repository provides reusable, standardized Jenkins pipeline steps (vars) for the **Marketplace Seller Risk & Abuse Detection Platform**.

---

## 🔗 Platform Repositories

| Repository | GitHub Link | Purpose & Tech Stack |
| :--- | :--- | :--- |
| **Platform & Infrastructure** | [nagaraj602/marketplace-risk-platform](https://github.com/nagaraj602/marketplace-risk-platform.git) | Terraform AWS IaC (VPC, EKS 1.37, RDS PostgreSQL 17, Redis 7.4, Jenkins), DB init scripts, Docker Compose |
| **Core Risk Backend** | [nagaraj602/marketplace-risk-backend](https://github.com/nagaraj602/marketplace-risk-backend.git) | Java 25 LTS, Spring Boot 4.1.2, JPA, Velocity Rule Engine, Risk Aggregation, Enforcement Orchestration |
| **ML Anomaly Model** | [nagaraj602/marketplace-risk-anomaly-model](https://github.com/nagaraj602/marketplace-risk-anomaly-model.git) | Python 3.15.2, FastAPI 0.162, scikit-learn 1.9.1 Isolation Forest, behavioral anomaly inference |
| **Trust & Safety Portal** | [nagaraj602/marketplace-risk-frontend](https://github.com/nagaraj602/marketplace-risk-frontend.git) | React 19, Vite 8, Node 24, Express 5, Lucide-React, live review queue & signal injection simulator |
| **Jenkins Shared Libraries** | [nagaraj602/jenkins-sharedlibraries](https://github.com/nagaraj602/jenkins-sharedlibraries.git) | Reusable Groovy pipeline vars (`trivyScan`, `sonarAnalysis`, `dockerECRImagePush`, `kubernetesEKSHelmDeployEnv`) |

---

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

---

## ⚙️ Prerequisites & Jenkins Controller Requirements

To consume these shared libraries in your Jenkins CI/CD controllers:
- **Jenkins Controller**: Jenkins LTS 2.440+ running on Java 21 or 25 LTS
- **Required Jenkins Plugins**:
  - `Multibranch Scan Webhook Trigger` (`multibranch-scan-webhook-trigger`): Instant build triggers via webhooks
  - `Pipeline: Shared Groovy Libraries` (`workflow-cps-global-lib`): Core shared library support
  - `GitHub Branch Source` (`github-branch-source`): Branch & PR discovery
  - `SonarQube Scanner for Jenkins` (`sonar`): Static analysis and quality gate checks
  - `Docker Pipeline` (`docker-workflow`): Image building and registry integration
  - `AWS Credentials Plugin` (`aws-credentials`): ECR and EKS authentication
  - `Credentials Binding Plugin` (`credentials-binding`): Secure credentials injection
- **Agent Node Prerequisites**:
  - Docker Engine 24+ & Docker CLI (socket mounted `/var/run/docker.sock`)
  - AWS CLI v2 (`aws`)
  - Helm v3+ / v4 (`helm`)
  - Trivy security scanner CLI (`trivy`)
  - JDK 25 LTS & Maven 3.9+
  - Python 3.15.2+ & Node.js 24 LTS
- **Configured Credentials in Jenkins**:
  - `githubCred`: Secret text / GitHub Personal Access Token (PAT) with `repo`, `admin:repo_hook` scopes
  - `awsCred`: AWS Access Key ID + Secret Key credentials binding (for ECR and EKS)
  - `sonarToken`: SonarQube User token configured under SonarQube Server settings

---

## 🛠️ Step-by-Step Jenkins Shared Library Setup

### 1. Register Global Shared Library in Jenkins
1. Go to **Manage Jenkins** > **System** (or **Configure System**).
2. Scroll down to the **Global Pipeline Libraries** section.
3. Click **Add**:
   - **Name**: `sharedlibrary` *(Must match `@Library('sharedlibrary')_` in all Jenkinsfiles)*
   - **Default version**: `main` (or `master`)
   - **Load implicitly**: Unchecked
   - **Allow default version to be overridden**: Checked
   - **Include @Library changes in job recent changes**: Checked
   - **Retrieval method**: Modern SCM
   - **Source Code Management**: Git
   - **Project Repository**: `https://github.com/nagaraj602/jenkins-sharedlibraries.git`
   - **Credentials**: `githubCred`
4. Click **Save**.

### 2. Configure Multibranch Pipeline Jobs
For each microservice repository (`marketplace-risk-backend`, `marketplace-risk-frontend`, `marketplace-risk-anomaly-model`):
1. In the Jenkins dashboard, click **New Item**.
2. Enter the item name and choose **Multibranch Pipeline**.
3. Under **Branch Sources**:
   - Add source: **GitHub** (or **Git**)
   - Repository URL: `https://github.com/nagaraj602/<repo-name>.git`
   - Credentials: `githubCred`
   - Discover branches: Filter `main master staging dev feature/*`
   - Discover pull requests: Strategy `The current pull request revision`
4. Under **Build Configuration**:
   - Mode: `by Jenkinsfile`
   - Script Path: `Jenkinsfile`

### 3. Configure Multibranch Scan Webhook Trigger
1. In the job configuration, scroll to **Scan Multibranch Pipeline Triggers**.
2. Check **Scan by webhook**.
3. Set **Trigger token**: e.g., `marketplace-risk-backend-token`
4. Webhook URL:
   `http://<JENKINS_HOST>:8080/multibranch-webhook-trigger/invoke?token=marketplace-risk-backend-token`

### 4. Setup Webhook in GitHub Repository
1. In GitHub, go to your repository **Settings** > **Webhooks** > **Add webhook**.
2. **Payload URL**: `http://<JENKINS_HOST>:8080/multibranch-webhook-trigger/invoke?token=<TRIGGER_TOKEN>`
3. **Content type**: `application/json`
4. **Events**: Select **Let me select individual events**:
   - ✅ **Pushes**
   - ✅ **Pull requests**
5. **Active**: Checked. Click **Add webhook**.
