# Jenkins Shared Libraries for Marketplace Risk Detection Platform

This repository provides reusable, standardized Jenkins pipeline steps (vars) for the **Marketplace Seller Risk & Abuse Detection Platform**.

---

## 🔗 Platform Repositories

| Repository | GitHub Link | Purpose & Tech Stack |
| :--- | :--- | :--- |
| **Platform & Infrastructure** | [nagaraj602/marketplace-risk-platform](https://github.com/nagaraj602/marketplace-risk-platform.git) | Terraform AWS IaC (VPC, EKS 1.37, RDS PostgreSQL 17, Redis 7.4, Jenkins), DB init scripts, Docker Compose |
| **Core Risk Backend** | [nagaraj602/marketplace-risk-backend](https://github.com/nagaraj602/marketplace-risk-backend.git) | Java 21, Spring Boot 4.1.2, JPA, Velocity Rule Engine, Risk Aggregation, Enforcement Orchestration |
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

## ⚙️ Configuration in Jenkins

1. Go to **Manage Jenkins** > **System**.
2. Scroll to **Global Pipeline Libraries**.
3. Add a new library named `sharedlibrary`:
   - Default version: `main` (or `master`)
   - Retrieval method: **Modern SCM** (Git)
   - Project Repository: `https://github.com/nagaraj602/jenkins-sharedlibraries.git`
   - Credentials: `githubCred`
