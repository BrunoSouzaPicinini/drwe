# DRWE
Database Read Write Entities

This application read from datasource origin and save in datasource destination.

### Pre-requisites

- Java 8
- Gradle
- Docker compose

#### Command to build do artifact:

    gradle clean build

Will be generated a **.jar** in folder **build/libs**

#### To run the application is necessary be configured the following environment variables<br>
>URL_FIRST_DATABASE;
USERNAME_FIRST_DATABASE;
PASSWORD_FIRST_DATABASE;
DRIVER_FIRST_DATABASE;
URL_SECOND_DATABASE;
USERNAME_SECOND_DATABASE;
PASSWORD_SECOND_DATABASE;
DRIVER_SECOND_DATABASE;
CHUNK_SIZE;
RENDER_PAGE_SIZE;

#### To up the two mysql for example with docker compose 
    
    docker-compose up -d