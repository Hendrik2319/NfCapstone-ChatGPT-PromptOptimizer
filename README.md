# ChatGPT PromptOptimizer
[Description]

## State of Code
[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-black.svg)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)
### Backend
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)  
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)  
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)  
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Backend&metric=bugs)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Backend)
### Frontend
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)  
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)  
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)  
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ChatGPT-PromptTester-Frontend&metric=bugs)](https://sonarcloud.io/summary/new_code?id=ChatGPT-PromptTester-Frontend)


## Configuration
### Environment Variables for Run
#### ChatGPT API
* `OPENAI_API_KEY`
  * used to get access to OpenAI API
  * you can disable API access if set to `disabled`
* `OPENAI_API_ORGANIZATION`
  * used to define a target for billing
  * you can disable API access if set to `disabled`
#### Mongo DB
* `MONGO_DB_URI`
  * URL of used MongoDB
* `MONGO_DB_NAME`
  * name of used database on DB server above
  * to separate databases of different run configurations / scenarios
<!--
#### OAuth2 (for later use)
* `OAUTH_GITHUB_CLIENT_ID`
  * client id from OAuth2 app in GitHub
* `OAUTH_GITHUB_CLIENT_SECRET`
  * client secret from OAuth2 app in GitHub
-->
### GitHub Secrets 
* `DOCKERHUB_PASSWORD`
  * used for pushing build docker image to docker in workflow "Deploy to render.com" (`.github/workflows/CD_DockerRender.yml`)
* `RENDER_DEPLOY_HOOK`
  * used for redeployment of docker image in workflow "Deploy to render.com" (`.github/workflows/CD_DockerRender.yml`)
* `SONAR_TOKEN`
  * used for code linting of Sonar Cloud (`.github/workflows/SonarCloud_backend.yml`, `.github/workflows/SonarCloud_frontend.yml`)
### GitHub Variables 
* `DOCKERHUB_USERNAME`
  * used for pushing build docker image to docker in workflow "Deploy to render.com" (`.github/workflows/CD_DockerRender.yml`)
* `DOCKER_IMAGE_NAME`
  * used to build name of docker image: `{ DOCKERHUB_USERNAME }/{ DOCKER_IMAGE_NAME }:latest`