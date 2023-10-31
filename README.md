# Employee API - Coding Challenge

Simple Spring Boot 3 Java 17 service/API to manage employees.

Project depends on Kafka and Postgres which are used in docker containers, and they are configured in `docker-compose.yml`
file which is in the root of the project. pgAdmin is also included in `docker-compose.yml`, and we can use it as 
DB admin panel (user interface)

Main service is not dockerized (it wasn't part of requirements), but probably before we prepare it for deployment, we will do it :) 


## How to run the application

### Requirements
- [Docker Compose](https://docs.docker.com/compose/install/) need to be installed, and it requires the [Docker](https://docs.docker.com/engine/install/) engine already installed. 
- Run docker on your machine
- For building and running the application you need:

  * [JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
  * [Maven 3](https://maven.apache.org)


### Running the application locally
- First clone the repo 
- Import the project using preferred IDE
- If a case we want to run app from IDE, open TakeawayApplication class and run it. Because of the dependency on 
`spring-boot-docker-compose` it will automatically run `docker compose up` and start all dependencies. 
- Alternatively we can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

First we need to run dependencies with
```shell
docker compose up
```
 
Build the project using 
```shell
mvn clean install
```
  Run the app
```shell
mvn spring-boot:run -Pdocker-compose
```

- It will start the service, which will communicate with Kafka and DB that are in docker

### API Documentation
Documentation is not fully completed. It's just there to show how we can use it. 
We can use Swagger UI to execute HTTP request to our app, but keep in mind that authentication is in the place.
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI specification: http://localhost:8080/v3/api-docs

## Authentication
For the sake of simplicity and time for development, I added only Spring Basic Authentication. I am aware that 
this is not the best way to secure a REST API. Also, Users and roles are hardcoded and limited on one user only. 
In a real life project DB should be added to store those data.

### API  Authentication
* username: `admin`
* password: `admin`



## Default credentials

### DB credentials (also with pgAdmin)
* username: `jogyco`
* password: `password`


## Features

* Employee Rest API
* Kafka Messaging (in docker container)
* PostgreSQL (in docker container)
* Spring Security
* Basic Authentication on API and in Swagger UI
* OpenAPI 3 specification

### Testing
Using JUnit 5, plus some spring boot test features, and in memory h2 DB
