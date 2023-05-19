package com.orange.cliper.querymodel;

import com.mongodb.client.MongoClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
public class MongoPartiesEventHandlerUnitTest extends AbstractPartiesEventHandlerUnitTest {

    @Autowired
    MongoClient mongoClient;

    @Override
    protected PartiesEventHandler getHandler() {
        mongoClient.getDatabase("axonframework")
          .drop();
        return new MongoPartiesEventHandler(mongoClient, emitter);
    }
}
