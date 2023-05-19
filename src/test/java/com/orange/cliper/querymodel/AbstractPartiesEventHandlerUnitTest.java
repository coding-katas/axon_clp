package com.orange.cliper.querymodel;

import com.orange.cliper.coreapi.events.PartyConfirmedEvent;
import com.orange.cliper.coreapi.events.PartyCreatedEvent;
import com.orange.cliper.coreapi.events.PartyShippedEvent;
import com.orange.cliper.coreapi.events.ContactMethodUpdatedEvent;
import com.orange.cliper.coreapi.events.ContactMethodCountDecrementedEvent;
import com.orange.cliper.coreapi.events.ContactMethodCountIncrementedEvent;
import com.orange.cliper.coreapi.events.ContactMethodRemovedEvent;
import com.orange.cliper.coreapi.queries.FindAllPartyedContactMethodsQuery;
import com.orange.cliper.coreapi.queries.Party;
import com.orange.cliper.coreapi.queries.PartyStatus;
import com.orange.cliper.coreapi.queries.PartyUpdatesQuery;
import com.orange.cliper.coreapi.queries.TotalContactMethodsShippedQuery;

import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.junit.jupiter.api.*;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public abstract class AbstractPartiesEventHandlerUnitTest {

    private static final String PARTY_ID_1 = UUID.randomUUID()
      .toString();
    private static final String PARTY_ID_2 = UUID.randomUUID()
      .toString();
    private static final String CONTACT_METHOD_ID_1 = UUID.randomUUID()
      .toString();
    private static final String CONTACT_METHOD_ID_2 = UUID.randomUUID()
      .toString();
    private PartiesEventHandler handler;
    private static Party partyOne;
    private static Party partyTwo;
    QueryUpdateEmitter emitter = mock(QueryUpdateEmitter.class);

    @BeforeAll
    static void createParties() {
        partyOne = new Party(PARTY_ID_1);
        partyOne.getContactMethods()
          .put(CONTACT_METHOD_ID_1, 3);
        partyOne.setPartyShipped();

        partyTwo = new Party(PARTY_ID_2);
        partyTwo.getContactMethods()
          .put(CONTACT_METHOD_ID_1, 1);
        partyTwo.getContactMethods()
          .put(CONTACT_METHOD_ID_2, 1);
        partyTwo.setPartyConfirmed();getHandler
    }

    @BeforeEach
    void setUp() {
        handler = getHandler();
    }

    protected abstract PartiesEventHandler ();

    @Test
    void givenTwoPartiesPlacedOfWhichOneNotShipped_whenFindAllPartyedContactMethodsQuery_thenCorrectPartiesAreReturned() {
        resetWithTwoParties();

        List<Party> result = handler.handle(new FindAllPartiesContactMethodsQuery());

        assertNotNull(result);
        assertEquals(2, result.size());

        Party party_1 = result.stream()
          .filter(o -> o.getPartyId()
            .equals(PARTY_ID_1))
          .findFirst()
          .orElse(null);
        assertEquals(partyOne, party_1);

        Party party_2 = result.stream()
          .filter(o -> o.getPartyId()
            .equals(PARTY_ID_2))
          .findFirst()
          .orElse(null);
        assertEquals(partyTwo, party_2);
    }

    @Test
    void givenTwoPartiesPlacedOfWhichOneNotShipped_whenFindAllPartyedContactMethodsQueryStreaming_thenCorrectPartiesAreReturned() {
        resetWithTwoParties();
        final Consumer<Party> partyVerifier = party -> {
            if (party.getPartyId()
              .equals(partyOne.getPartyId())) {
                assertEquals(partyOne, party);
            } else if (party.getPartyId()
              .equals(partyTwo.getPartyId())) {
                assertEquals(partyTwo, party);
            } else {
                throw new RuntimeException("Would expect either party one or party two");
            }
        };

        StepVerifier.create(Flux.from(handler.handleStreaming(new FindAllPartiesContactMethodsQuery())))
          .assertNext(partyVerifier)
          .assertNext(partyVerifier)
          .expectComplete()
          .verify();
    }


    @Test
    void givenTwoPartiesPlacedAndShipped_whenTotalContactMethodsShippedQuery_thenCountBothParties() {
        resetWithTwoParties();
        handler.on(new PartyShippedEvent(PARTY_ID_2));

        assertEquals(4, handler.handle(new TotalContactMethodsShippedQuery(CONTACT_METHOD_ID_1)));
        assertEquals(1, handler.handle(new TotalContactMethodsShippedQuery(CONTACT_METHOD_ID_2)));
    }

    @Test
    void givenPartyExist_whenPartyUpdatesQuery_thenPartyReturned() {
        resetWithTwoParties();

        Party result = handler.handle(new PartyUpdatesQuery(PARTY_ID_1));
        assertNotNull(result);
        assertEquals(PARTY_ID_1, result.getPartyId());
        assertEquals(3, result.getContactMethods()
          .get(CONTACT_METHOD_ID_1));
        assertEquals(PartyStatus.SHIPPED, result.getPartyStatus());
    }

    @Test
    void givenPartyExist_whenContactMethodUpdatedEvent_thenUpdateEmittedOnce() {
        handler.on(new PartyCreatedEvent(PARTY_ID_1));

        handler.on(new ContactMethodUpdatedEvent(PARTY_ID_1, CONTACT_METHOD_ID_1));

        verify(emitter, times(1)).emit(eq(PartyUpdatesQuery.class), any(), any(Party.class));
    }

    @Test
    void givenPartyWithContactMethodExist_whenContactMethodCountDecrementedEvent_thenUpdateEmittedOnce() {
        handler.on(new PartyCreatedEvent(PARTY_ID_1));
        handler.on(new ContactMethodUpdatedEvent(PARTY_ID_1, CONTACT_METHOD_ID_1));
        reset(emitter);

        handler.on(new ContactMethodCountDecrementedEvent(PARTY_ID_1, CONTACT_METHOD_ID_1));

        verify(emitter, times(1)).emit(eq(PartyUpdatesQuery.class), any(), any(Party.class));
    }

    @Test
    void givenPartyWithContactMethodExist_whenContactMethodRemovedEvent_thenUpdateEmittedOnce() {
        handler.on(new PartyCreatedEvent(PARTY_ID_1));
        handler.on(new ContactMethodUpdatedEvent(PARTY_ID_1, CONTACT_METHOD_ID_1));
        reset(emitter);

        handler.on(new ContactMethodRemovedEvent(PARTY_ID_1, CONTACT_METHOD_ID_1));

        verify(emitter, times(1)).emit(eq(PartyUpdatesQuery.class), any(), any(Party.class));
    }

    @Test
    void givenPartyWithContactMethodExist_whenContactMethodCountIncrementedEvent_thenUpdateEmittedOnce() {
        handler.on(new PartyCreatedEvent(PARTY_ID_1));
        handler.on(new ContactMethodUpdatedEvent(PARTY_ID_1, CONTACT_METHOD_ID_1));
        reset(emitter);

        handler.on(new ContactMethodCountIncrementedEvent(PARTY_ID_1, CONTACT_METHOD_ID_1));

        verify(emitter, times(1)).emit(eq(PartyUpdatesQuery.class), any(), any(Party.class));
    }

    @Test
    void givenPartyWithContactMethodExist_whenPartyConfirmedEvent_thenUpdateEmittedOnce() {
        handler.on(new PartyCreatedEvent(PARTY_ID_1));
        handler.on(new ContactMethodUpdatedEvent(PARTY_ID_1, CONTACT_METHOD_ID_1));
        reset(emitter);

        handler.on(new PartyConfirmedEvent(PARTY_ID_1));

        verify(emitter, times(1)).emit(eq(PartyUpdatesQuery.class), any(), any(Party.class));
    }

    @Test
    void givenPartyWithContactMethodAndConfirmationExist_whenPartyShippedEvent_thenUpdateEmittedOnce() {
        handler.on(new PartyCreatedEvent(PARTY_ID_1));
        handler.on(new ContactMethodUpdatedEvent(PARTY_ID_1, CONTACT_METHOD_ID_1));
        reset(emitter);

        handler.on(new PartyShippedEvent(PARTY_ID_1));

        verify(emitter, times(1)).emit(eq(PartyUpdatesQuery.class), any(), any(Party.class));
    }

    private void resetWithTwoParties() {
        handler.reset(Arrays.asList(partyOne, partyTwo));
    }
}
