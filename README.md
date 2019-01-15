# deployonfriday-resilience
Resilience for the Deploy On Friday series

#How to use
* Check out this project, then build both apps using 'mvnw package' (Win) or './mvnw package' (Mac/Linux)
* Start the resilience-provider using 'mvnw spring-boot:run' (Win) or './mvnw spring-boot:run' (Mac/Linux)
* Start the resilience-consumer using 'mvnw spring-boot:run' (Win) or './mvnw spring-boot:run' (Mac/Linux)
* Use your favorite tool to test the consumer on http://localhost:8080/bulkhead and https://localhost:8080/circuitbreaker?shouldFail=true|false 
* My favorite tool is https://httpd.apache.org/docs/2.4/programs/ab.html
