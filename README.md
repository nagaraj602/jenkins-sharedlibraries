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
  - `Pipeline: Shared Groovy Libraries` (`workflow-cps-global-lib`)
  - `SonarQube Scanner for Jenkins` (`sonar`)
  - `Docker Pipeline` (`docker-workflow`)
  - `AWS Credentials Plugin` (`aws-credentials`)
  - `Credentials Binding Plugin` (`credentials-binding`)
- **Agent Node Prerequisites**:
  - Docker Engine 24+ & Docker CLI (socket mounted `/var/run/docker.sock`)
  - AWS CLI v2 (`aws`)
  - Helm v3+ (`helm`)
  - Trivy security scanner CLI (`trivy`)
  - JDK 25 LTS & Maven 3.9+
  - Python 3.15.2+ & Node.js 24 LTS
- **Configured Credentials in Jenkins**:
  - `githubCred`: Secret text / GitHub Personal Access Token
  - `awsCredentials`: AWS Access Key ID + Secret Key credentials binding
  - `sonarServer`: SonarQube Server endpoint configured under Jenkins System settings

---

## ⚙️ Configuration in Jenkins

1. Go to **Manage Jenkins** > **System**.
2. Scroll to **Global Pipeline Libraries**.
3. Add a new library named `sharedlibrary`:
   - Default version: `main` (or `master`)
   - Retrieval method: **Modern SCM** (Git)
   - Project Repository: `https://github.com/nagaraj602/jenkins-sharedlibraries.git`
   - Credentials: `githubCred`
