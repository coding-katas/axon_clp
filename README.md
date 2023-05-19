## Presentations

This module contains the spring party app with axon

## Profiles

Optionally the code can be run with the 'mongo' profile to use Mongo to store the projection. Otherwise, an in-memory
projection is used.

## Scripts

Two scripts are included to easily start middleware using Docker matching the properties files:

- `start_axon_server.sh` to start an Axon Server instance
- `start_mongo.sh` to start a MongoDB instance



## Scripts


- mvn clean install

Manual test:
-  mvn spring-boot:run
- mvn test -Dtest=OrderRestEndpointManualTest -Dspring.config.location=src/test/resources/application-mongo-srv.properties

