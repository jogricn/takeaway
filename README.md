# Employee API - Coding Challenge

Simple Spring Boot 3 service/API to manage employees.  
Project depends on Kafka and Postgres which I used in docker containers, and they are configured in `docker-compose.yml`
file which is in the root of the project. In `docker-compose.yml` I also included PGAdmin which is admin panel for postgres DB

Main service is not dockerized, but probably before we prepare it for deployment, we should do it

## How to run the application

### Requirements
- Docker should be installed
- Run docker on your machine


### Steps to run the app
- First clone the repo 
- Import the project using your preferred IDE
- Build the project with `mvn clean install`
- From terminal, in the root of your project execute `docker compose up`. It will run all dependencies in docker containers
- In your IDE open TakeawayApplication class and run it. It will start the service, which will communicate with Kafka and DB that are in docker

### API Documentation
Documentation is not fully completed. It's just there to show how we can use it
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI specification: http://localhost:8080/v3/api-docs

### Authentication
For the sake of simplicity and time for development, I added only Spring Basic Authentication. I am aware that 
this is not the best way to secure a REST API

## Default credentials

### DB credentials (also with pgAdmin)
* username: `jogyco`
* password: `password`
### API  Authentication
* username: `admin`
* password: `admin`

## Features

* Employee Rest API
* Kafka Messaging 
* Spring Security
* Basic Authentication on API and in Swagger UI
* OpenAPI 3 specification

## Built With

* [Spring Boot v3.1.5](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/)
* [springdoc-openapi](https://springdoc.org/)
* 