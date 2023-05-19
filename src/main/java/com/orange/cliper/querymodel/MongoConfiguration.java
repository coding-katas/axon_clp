package com.orange.cliper.querymodel;

import com.mongodb.client.MongoClient;

import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
// import org.axonframework.extensions.mongo.queryhandling.MongoQueryModelStorage;

@Configuration
@Profile("mongo")
public class MongoConfiguration {


    @Bean
    public TokenStore getTokenStore(MongoClient client, Serializer serializer) {
        return MongoTokenStore.builder()
          .mongoTemplate(DefaultMongoTemplate.builder()
            .mongoDatabase(client)
            .build())
          .serializer(serializer)
          .build();
    }

    @Bean
    public EventStorageEngine storageEngine(MongoClient mongoClient) {
        return MongoEventStorageEngine
                .builder()
                .mongoTemplate(DefaultMongoTemplate
                        .builder()
                        .mongoDatabase(mongoClient)
                        .build())
                .build();
    }
/*
    @Bean
    public TokenStore getTokenStore(EventStorageEngine eventStorageEngine) {
        return new MongoTokenStore(eventStorageEngine);
    }*/
/*
    @Bean
    public MongoQueryModelStorage queryModelStorage(MongoClient client, Serializer serializer) {
        return MongoQueryModelStorage.builder()
                .mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
                .serializer(serializer)
                .build();
    }    */
}



/*
    @Bean
    public TokenStore getTokenStore(MongoClient client, Serializer serializer) {
        return MongoTokenStore.builder()
          .mongoTemplate(DefaultMongoTemplate.builder()
            .mongoDatabase(client)
            .build())
          .serializer(serializer)
          .build();
    }


    @Bean
    public EventStorageEngine eventStorageEngine(MongoClient client, Serializer serializer) {
        return MongoEventStorageEngine.builder()
                .mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
                .serializer(serializer)
                .build();
    }

    @Bean
    public TokenStore getTokenStore(EventStorageEngine eventStorageEngine) {
        return new MongoTokenStore(eventStorageEngine);
    }    */