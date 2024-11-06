# Kuna Backend

Kuna is an e-commerce web application developed using Java and Spring Boot. The backend architecture provides a foundation for features such as SQL database storage, REST API endpoints, database containerization with Docker, and integration with payment gateways such as Stripe.

## Setup

To set up and run the application, follow the instructions below:

### Prerequisites

- Java Development Kit (JDK) 17 
- Docker (if running MySQL in a Docker container)

### Steps

1. Clone the repository to your local machine:

   ```shell
   git clone https:https://github.com/fiorellamansilla/kuna_backend

2. Navigate to the project directory:

    ```shell
   cd kuna_backend

3. Build the project using Gradle:

    ```shell
    ./gradlew build

4. If you're running MySQL in a Docker container, create a `docker-compose.yml` file in the project directory with the following contents: 

   ```yaml
   version: '3.1'
   
   services:
    db:
       image: mysql
       ports:
         - "3306:3306"
       expose:
         - "3306"
       environment:
         - MYSQL_ROOT_HOST=%
         - MYSQL_DATABASE=kuna_db
         - MYSQL_ROOT_PASSWORD=your_password
         - MYSQL_ALLOW_EMPTY_PASSWORD=yes
       volumes:
         - ./docker/mysql_volume:/var/lib/mysql 
   ```
   
   This configuration sets up a MySQL container with the necessary environment variables and exposes port 3306.

5. Start the MySQL container using Docker Compose:

   ```shell
   docker-compose up -d

6. Use the test data from the `data.sql` file for the application.<br />


7. Configure the database connection by modifying the Flyway block in the `build.gradle` file:

   ```groovy
   flyway {
    url = 'jdbc:mysql://localhost:3306/kuna_db'
    user = 'root'
    password = 'your_password'
    schemas = ['kuna_db']
   }
   ```

8. Migrate the database schema using Flyway:

   ```shell
   ./gradlew flywayMigrate

9. Start the application:

   ```shell
   ./gradlew bootRun

10. The application will start running on `http://localhost:8080`

## Application Properties

The application requires the configuration specified in the `application.properties` file. Make sure to update the values of the properties to match your specific configuration.

## Dependencies

The application uses the following libraries and frameworks:

- Spring Boot: 3.2.0
- Spring Boot Starter Data JPA: 3.2.0
- Spring Boot Starter Web: 3.2.0
- Flyway: 9.11.0
- Flyway MySQL: 9.11.0
- Stripe Java: 22.13.0
- Jetbrains Annotations: 24.0.1
- Spring Security Crypto: 6.0.2
- JUnit: 4.13.2
- Mockito Core: 5.2.0
- MySQL Connector/J: 8.0.32

## Testing

The tests are implemented using JUnit and Mockito. To run the unit tests, use the following command :

   ```shell
   ./gradlew test
```

## License

This project is licensed under the MIT License.


