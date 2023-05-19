package com.orange.cliper.querymodel;


import com.orange.cliper.coreapi.events.PartyCreatedEvent;
import com.orange.cliper.coreapi.events.ContactMethodUpdatedEvent;
import com.orange.cliper.coreapi.queries.FindAllPartiesQuery;
import com.orange.cliper.coreapi.queries.Party;
import com.orange.cliper.coreapi.queries.PartyStatus;
import com.orange.cliper.coreapi.queries.PartyUpdatesQuery;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;

//import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.checkerframework.checker.nullness.qual.NonNull;


import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.*;

@Service
@ProcessingGroup("parties")
@Profile("mongo")
public class MongoPartiesEventHandler implements PartiesEventHandler {

    static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup()
      .lookupClass());

    private final MongoCollection<Document> parties;
    private final QueryUpdateEmitter emitter;
    private static final String PARTY_COLLECTION_NAME = "parties";
    private static final String AXON_FRAMEWORK_DATABASE_NAME = "axonframework";

    private static final String PARTY_ID_PROPERTY_NAME = "partyId";
    private static final String PRODUCTS_PROPERTY_NAME = "contactMethods";
    private static final String PARTY_STATUS_PROPERTY_NAME = "partyStatus";

    public MongoPartiesEventHandler(MongoClient client, QueryUpdateEmitter emitter) {
        parties = client.getDatabase(AXON_FRAMEWORK_DATABASE_NAME)
          .getCollection(PARTY_COLLECTION_NAME);
        parties.createIndex(Indexes.ascending(PARTY_ID_PROPERTY_NAME), new IndexOptions().unique(true));
        this.emitter = emitter;
    }

    @EventHandler
    public void on(PartyCreatedEvent event) {
        parties.insertOne(partyToDocument(new Party(event.getPartyId())));
    }

    @EventHandler
    public void on(ContactMethodUpdatedEvent event) {
        update(event.getPartyId(), o -> o.addContactMethod(event.getContactMethodId()));
    }

    @QueryHandler
    public List<Party> handle(FindAllPartiesQuery query) {
        List<Party> partyList = new ArrayList<>();
        parties.find()
          .forEach(d -> partyList.add(documentToParty(d)));
        return partyList;
    }

    @Override
    public Publisher<Party> handleStreaming(FindAllPartiesQuery query) {
        return Flux.fromIterable(parties.find())
          .map(this::documentToParty);
    }

    @QueryHandler
    public Party handle(PartyUpdatesQuery query) {
        return getParty(query.getPartyId()).orElse(null);
    }

    @Override
    public void reset(List<Party> partyList) {
        parties.deleteMany(new Document());
        partyList.forEach(o -> parties.insertOne(partyToDocument(o)));
    }

    private Optional<Party> getParty(String partyId) {
        return Optional.ofNullable(parties.find(eq(PARTY_ID_PROPERTY_NAME, partyId))
            .first())
          .map(this::documentToParty);
    }

    private Party emitUpdate(Party party) {
        emitter.emit(PartyUpdatesQuery.class, q -> party.getPartyId()
          .equals(q.getPartyId()), party);
        return party;
    }

    private Party updateParty(Party party, Consumer<Party> updateFunction) {
        updateFunction.accept(party);
        return party;
    }

    private UpdateResult persistUpdate(Party party) {
        return parties.replaceOne(eq(PARTY_ID_PROPERTY_NAME, party.getPartyId()), partyToDocument(party));
    }

    private void update(String partyId, Consumer<Party> updateFunction) {
        UpdateResult result = getParty(partyId).map(o -> updateParty(o, updateFunction))
          .map(this::emitUpdate)
          .map(this::persistUpdate)
          .orElse(null);
        logger.info("Result of updating party with partyId '{}': {}", partyId, result);
    }

    private Document partyToDocument(Party party) {
        return new Document(PARTY_ID_PROPERTY_NAME, party.getPartyId()).append(PRODUCTS_PROPERTY_NAME, party.getContactMethods())
          .append(PARTY_STATUS_PROPERTY_NAME, party.getPartyStatus()
            .toString());
    }

    private Party documentToParty(@NonNull Document document) {
        Party party = new Party(document.getString(PARTY_ID_PROPERTY_NAME));
        Document contactMethods = document.get(PRODUCTS_PROPERTY_NAME, Document.class);
        contactMethods.forEach((k, v) -> party.getContactMethods()
          .put(k, (Integer) v));
        String status = document.getString(PARTY_STATUS_PROPERTY_NAME);
        return party;
    }

 
}