# GitHub-Scanner

This Spring Boot application fetches all repositories of a given GitHub user and filters out the forked repositories. 

## Prerequisites

- Java 17 or higher
- Maven
- GitHub API token (optional, works without it for github accounts with small number of repositories and branches)

## Getting Started

### Clone the repository

```shell
git clone https://github.com/mateuszhub/github-scanner.git
cd github-scanner
```

### Run application locally with maven

```shell
.\mvnw spring-boot:run "-Dspring-boot.run.arguments=--GH_TOKEN=xyz123"
```

or without token

```shell
.\mvnw spring-boot:run 
```

### Test application

example request

```shell
curl -X GET -H "Accept: application/json" "localhost:8080/users/mateuszhub/repositories"
```

example response body:

```text
{"repositories":[{"repositoryName":"androidApk","userLogin":"MateuszHub","branches":[{"branchName":"master",
"lastCommitSha":"2fe6d9ee87e4fafab61d3c5758b0ece3efc14db4"}]}]}
```