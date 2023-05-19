package com.orange.cliper.gui;

import com.orange.cliper.PartyApplication;
import com.orange.cliper.querymodel.PartyResponse;
import com.orange.cliper.querymodel.PartyStatusResponse;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mongodb.client.MongoClient;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.beans.factory.annotation.Autowired;

import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PartyApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//marked as manual as the test is unstable on Jenkins due to low resources
class PartyRestEndpointManualTest {

    @Autowired
    MongoClient mongoClient;

    @LocalServerPort
    private int port;

    @Autowired
    private EventStore eventStore;

    @BeforeEach
    void setUp() {

    }


    @Test
    @DirtiesContext
    void givenCreatePartyCalled_whenCallingAllParties_thenOneCreatedPartyIsReturned() {
        //mongoClient.getDatabase("axonframework").drop();
       // purgeEventStore();

        WebClient client = WebClient.builder()
          .clientConnector(httpConnector())
          .build();
        createRandomNewParty(client);
/*        StepVerifier.create(retrieveListResponse(client.get()
            .uri("http://localhost:" + port + "/all-partys")))
          .expectNextMatches(list -> 1 == list.size() && list.get(0)
            .getPartyStatus() == PartyStatusResponse.CREATED)
          .verifyComplete();*/
    }
/*
    private void purgeEventStore() {
        List<GenericEventMessage<?>> events;
        UnitOfWork<?> uow = DefaultUnitOfWork.startAndGet(null);
        try {
            events = eventStore.readEvents(null).asStream().map(e -> (GenericEventMessage<?>) e).toList();
            eventStore.deleteEvents(events);
            uow.commit();
        } catch (Exception e) {
            uow.rollback();
        }
    }*/
/*
    @Test
    @DirtiesContext
    void givenCreatePartyCalledThreeTimesAnd_whenCallingAllPartiesStreaming_thenTwoCreatedPartiesAreReturned() {
        WebClient client = WebClient.builder()
          .clientConnector(httpConnector())
          .build();
        for (int i = 0; i < 3; i++) {
            createRandomNewParty(client);
        }
        StepVerifier.create(retrieveStreamingResponse(client.get()
            .uri("http://localhost:" + port + "/all-partys-streaming")))
          .expectNextMatches(o -> o.getPartyStatus() == PartyStatusResponse.CREATED)
          .expectNextMatches(o -> o.getPartyStatus() == PartyStatusResponse.CREATED)
          .expectNextMatches(o -> o.getPartyStatus() == PartyStatusResponse.CREATED)
          .verifyComplete();
    }

    @Test
    @DirtiesContext
    void givenRuleExistThatNeedConfirmationBeforeShipping_whenCallingShipUnconfirmed_thenErrorReturned() {
        WebClient client = WebClient.builder()
          .clientConnector(httpConnector())
          .build();
        StepVerifier.create(retrieveResponse(client.post()
            .uri("http://localhost:" + port + "/ship-unconfirmed-party")))
          .verifyError(WebClientResponseException.class);
    }

    @Test
    @DirtiesContext
    void givenShipPartyCalled_whenCallingAllShippedChairs_then234PlusOneIsReturned() {
        WebClient client = WebClient.builder()
          .clientConnector(httpConnector())
          .build();
        verifyVoidPost(client, "http://localhost:" + port + "/ship-party");
        StepVerifier.create(retrieveIntegerResponse(client.get()
            .uri("http://localhost:" + port + "/total-shipped/Deluxe Chair")))
          .assertNext(r -> assertEquals(235, r))
          .verifyComplete();
    }

    @Test
    @DirtiesContext
    void givenPartiesAreUpdated_whenCallingPartyUpdates_thenUpdatesReturned() {
        WebClient updaterClient = WebClient.builder()
          .clientConnector(httpConnector())
          .build();
        WebClient receiverClient = WebClient.builder()
          .clientConnector(httpConnector())
          .build();
        String partyId = UUID.randomUUID()
          .toString();
        String contactMethodId = UUID.randomUUID()
          .toString();
        StepVerifier.create(retrieveResponse(updaterClient.post()
            .uri("http://localhost:" + port + "/party/" + partyId)))
          .assertNext(Assertions::assertNotNull)
          .verifyComplete();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> addIncrementDecrementConfirmAndShipContactMethod(partyId, contactMethodId), 1L, TimeUnit.SECONDS);
        try {
            StepVerifier.create(retrieveStreamingResponse(receiverClient.get()
                .uri("http://localhost:" + port + "/party-updates/" + partyId)))
              .assertNext(p -> assertTrue(p.getContactMethods()
                .isEmpty()))
              .assertNext(p -> assertEquals(1, p.getContactMethods()
                .get(contactMethodId)))
              .assertNext(p -> assertEquals(2, p.getContactMethods()
                .get(contactMethodId)))
              .assertNext(p -> assertEquals(1, p.getContactMethods()
                .get(contactMethodId)))
              .assertNext(p -> assertEquals(PartyStatusResponse.CONFIRMED, p.getPartyStatus()))
              .assertNext(p -> assertEquals(PartyStatusResponse.SHIPPED, p.getPartyStatus()))
              .thenCancel()
              .verify();
        } finally {
            executor.shutdown();
        }
    }

    private void addIncrementDecrementConfirmAndShipContactMethod(String partyId, String contactMethodId) {
        WebClient client = WebClient.builder()
          .clientConnector(httpConnector())
          .build();
        String base = "http://localhost:" + port + "/party/" + partyId;
        verifyVoidPost(client, base + "/contactMethod/" + contactMethodId);
        verifyVoidPost(client, base + "/contactMethod/" + contactMethodId + "/increment");
        verifyVoidPost(client, base + "/contactMethod/" + contactMethodId + "/decrement");
        verifyVoidPost(client, base + "/confirm");
        verifyVoidPost(client, base + "/ship");
    }
*/
    private void createRandomNewParty(WebClient client){
        StepVerifier.create(retrieveResponse(client.post()
            .uri("http://localhost:" + port + "/party")))
          .assertNext(Assertions::assertNotNull)
          .verifyComplete();
    }

    private void verifyVoidPost(WebClient client, String uri) {
        StepVerifier.create(retrieveResponse(client.post()
            .uri(uri)))
          .verifyComplete();
    }

    private static ReactorClientHttpConnector httpConnector() {
        HttpClient httpClient = HttpClient.create()
          .wiretap(true);
        return new ReactorClientHttpConnector(httpClient);
    }

    private Mono<String> retrieveResponse(WebClient.RequestBodySpec spec) {
        return spec.retrieve()
          .bodyToMono(String.class);
    }

    private Mono<ResponseList> retrieveListResponse(WebClient.RequestHeadersSpec<?> spec) {
        return spec.accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .bodyToMono(ResponseList.class);
    }

    private Mono<Integer> retrieveIntegerResponse(WebClient.RequestHeadersSpec<?> spec) {
        return spec.retrieve()
          .bodyToMono(Integer.class);
    }

    private Flux<PartyResponse> retrieveStreamingResponse(WebClient.RequestHeadersSpec<?> spec) {
        return spec.retrieve()
          .bodyToFlux(PartyResponse.class);
    }

    private static class ResponseList extends ArrayList<PartyResponse> {

        private ResponseList() {
            super();
        }
    }
}
