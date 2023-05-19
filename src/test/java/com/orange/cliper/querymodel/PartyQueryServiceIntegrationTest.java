package com.orange.cliper.querymodel;

import com.orange.cliper.PartyApplication;
import com.orange.cliper.coreapi.events.PartyConfirmedEvent;
import com.orange.cliper.coreapi.events.PartyShippedEvent;
import com.orange.cliper.coreapi.events.ContactMethodUpdatedEvent;
import com.orange.cliper.coreapi.events.ContactMethodCountDecrementedEvent;
import com.orange.cliper.coreapi.events.ContactMethodCountIncrementedEvent;
import com.orange.cliper.coreapi.queries.Party;

import org.axonframework.eventhandling.gateway.EventGateway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PartyApplication.class)
class PartyQueryServiceIntegrationTest {

    @Autowired
    PartyQueryService queryService;

    @Autowired
    EventGateway eventGateway;

    @Autowired
    PartiesEventHandler handler;

    private String partyId;
    private final String contactMethodId = "Deluxe Chair";

    @BeforeEach
    void setUp() {
        partyId = UUID.randomUUID()
          .toString();
        Party party = new Party(partyId);
        handler.reset(Collections.singletonList(party));
    }

    @Test
    void givenPartyCreatedEventSend_whenCallingAllParties_thenOneCreatedPartyIsReturned() throws ExecutionException, InterruptedException {
        List<PartyResponse> result = queryService.findAllParties()
          .get();
        assertEquals(1, result.size());
        PartyResponse response = result.get(0);
        assertEquals(partyId, response.getPartyId());
        assertEquals(PartyStatusResponse.CREATED, response.getPartyStatus());
        assertTrue(response.getContactMethods()
          .isEmpty());
    }

    @Test
    void givenPartyCreatedEventSend_whenCallingAllPartiesStreaming_thenOnePartyIsReturned() {
        Flux<PartyResponse> result = queryService.allPartiesStreaming();
        StepVerifier.create(result)
          .assertNext(party -> assertEquals(partyId, party.getPartyId()))
          .expectComplete()
          .verify();
    }

    @Test
    void givenThreeDeluxeChairsShipped_whenCallingAllShippedChairs_then234PlusTreeIsReturned() {
        Party party = new Party(partyId);
        party.getContactMethods()
          .put(contactMethodId, 3);
        party.setPartyShipped();
        handler.reset(Collections.singletonList(party));

        assertEquals(237, queryService.totalShipped(contactMethodId));
    }

    @Test
    void givenPartiesAreUpdated_whenCallingPartyUpdates_thenUpdatesReturned() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(this::addIncrementDecrementConfirmAndShip, 100L, TimeUnit.MILLISECONDS);
        try {
            StepVerifier.create(queryService.partyUpdates(partyId))
              .assertNext(party -> assertTrue(party.getContactMethods()
                .isEmpty()))
              .assertNext(party -> assertEquals(1, party.getContactMethods()
                .get(contactMethodId)))
              .assertNext(party -> assertEquals(2, party.getContactMethods()
                .get(contactMethodId)))
              .assertNext(party -> assertEquals(1, party.getContactMethods()
                .get(contactMethodId)))
              .assertNext(party -> assertEquals(PartyStatusResponse.CONFIRMED, party.getPartyStatus()))
              .assertNext(party -> assertEquals(PartyStatusResponse.SHIPPED, party.getPartyStatus()))
              .thenCancel()
              .verify();
        } finally {
            executor.shutdown();
        }
    }

    private void addIncrementDecrementConfirmAndShip() {
        sendContactMethodUpdatedEvent();
        sendContactMethodCountIncrementEvent();
        sendContactMethodCountDecrementEvent();
        sendPartyConfirmedEvent();
        sendPartyShippedEvent();
    }

    private void sendContactMethodUpdatedEvent() {
        ContactMethodUpdatedEvent event = new ContactMethodUpdatedEvent(partyId, contactMethodId);
        eventGateway.publish(event);
    }

    private void sendContactMethodCountIncrementEvent() {
        ContactMethodCountIncrementedEvent event = new ContactMethodCountIncrementedEvent(partyId, contactMethodId);
        eventGateway.publish(event);
    }

    private void sendContactMethodCountDecrementEvent() {
        ContactMethodCountDecrementedEvent event = new ContactMethodCountDecrementedEvent(partyId, contactMethodId);
        eventGateway.publish(event);
    }

    private void sendPartyConfirmedEvent() {
        PartyConfirmedEvent event = new PartyConfirmedEvent(partyId);
        eventGateway.publish(event);
    }

    private void sendPartyShippedEvent() {
        PartyShippedEvent event = new PartyShippedEvent(partyId);
        eventGateway.publish(event);
    }
}
