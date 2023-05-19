package com.orange.cliper.commandmodel;

import com.orange.cliper.commandmodel.party.PartyAggregate;
import com.orange.cliper.coreapi.commands.UpdateContactMethodCommand;
import com.orange.cliper.coreapi.commands.ConfirmPartyCommand;
import com.orange.cliper.coreapi.commands.CreatePartyCommand;
import com.orange.cliper.coreapi.commands.DecrementContactMethodCountCommand;
import com.orange.cliper.coreapi.commands.IncrementContactMethodCountCommand;
import com.orange.cliper.coreapi.commands.ShipPartyCommand;
import com.orange.cliper.coreapi.events.PartyConfirmedEvent;
import com.orange.cliper.coreapi.events.PartyCreatedEvent;
import com.orange.cliper.coreapi.events.PartyShippedEvent;
import com.orange.cliper.coreapi.events.ContactMethodUpdatedEvent;
import com.orange.cliper.coreapi.events.ContactMethodCountDecrementedEvent;
import com.orange.cliper.coreapi.events.ContactMethodCountIncrementedEvent;
import com.orange.cliper.coreapi.events.ContactMethodRemovedEvent;
import com.orange.cliper.coreapi.exceptions.DuplicateContactMethodException;
import com.orange.cliper.coreapi.exceptions.PartyAlreadyConfirmedException;
import com.orange.cliper.coreapi.exceptions.UnconfirmedPartyException;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.axonframework.test.matchers.Matchers;
import org.junit.jupiter.api.*;

import java.util.UUID;

class PartyAggregateUnitTest {

    private static final String PARTY_ID = UUID.randomUUID()
      .toString();
    private static final String CONTACT_METHOD_ID = UUID.randomUUID()
      .toString();

    private FixtureConfiguration<PartyAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(PartyAggregate.class);
    }

    @Test
    void giveNoPriorActivity_whenCreatePartyCommand_thenShouldPublishPartyCreatedEvent() {
        fixture.givenNoPriorActivity()
          .when(new CreatePartyCommand(PARTY_ID))
          .expectEvents(new PartyCreatedEvent(PARTY_ID));
    }

    @Test
    void givenPartyCreatedEvent_whenUpdateContactMethodCommand_thenShouldPublishContactMethodUpdatedEvent() {
        fixture.given(new PartyCreatedEvent(PARTY_ID))
          .when(new UpdateContactMethodCommand(PARTY_ID, CONTACT_METHOD_ID))
          .expectEvents(new ContactMethodUpdatedEvent(PARTY_ID, CONTACT_METHOD_ID));
    }

    @Test
    void givenPartyCreatedEventAndContactMethodUpdatedEvent_whenUpdateContactMethodCommandForSameContactMethodId_thenShouldThrowDuplicateContactMethodException() {
        fixture.given(new PartyCreatedEvent(PARTY_ID), new ContactMethodUpdatedEvent(PARTY_ID, CONTACT_METHOD_ID))
          .when(new UpdateContactMethodCommand(PARTY_ID, CONTACT_METHOD_ID))
          .expectException(DuplicateContactMethodException.class)
          .expectExceptionMessage(Matchers.predicate(message -> ((String) message).contains(CONTACT_METHOD_ID)));
    }

    @Test
    void givenPartyCreatedEventAndContactMethodUpdatedEvent_whenIncrementContactMethodCountCommand_thenShouldPublishContactMethodCountIncrementedEvent() {
        fixture.given(new PartyCreatedEvent(PARTY_ID), new ContactMethodUpdatedEvent(PARTY_ID, CONTACT_METHOD_ID))
          .when(new IncrementContactMethodCountCommand(PARTY_ID, CONTACT_METHOD_ID))
          .expectEvents(new ContactMethodCountIncrementedEvent(PARTY_ID, CONTACT_METHOD_ID));
    }

    @Test
    void givenPartyCreatedEventContactMethodUpdatedEventAndContactMethodCountIncrementedEvent_whenDecrementContactMethodCountCommand_thenShouldPublishContactMethodCountDecrementedEvent() {
        fixture.given(new PartyCreatedEvent(PARTY_ID), new ContactMethodUpdatedEvent(PARTY_ID, CONTACT_METHOD_ID), new ContactMethodCountIncrementedEvent(PARTY_ID, CONTACT_METHOD_ID))
          .when(new DecrementContactMethodCountCommand(PARTY_ID, CONTACT_METHOD_ID))
          .expectEvents(new ContactMethodCountDecrementedEvent(PARTY_ID, CONTACT_METHOD_ID));
    }

    @Test
    void givenPartyCreatedEventAndContactMethodUpdatedEvent_whenDecrementContactMethodCountCommand_thenShouldPublishContactMethodRemovedEvent() {
        fixture.given(new PartyCreatedEvent(PARTY_ID), new ContactMethodUpdatedEvent(PARTY_ID, CONTACT_METHOD_ID))
          .when(new DecrementContactMethodCountCommand(PARTY_ID, CONTACT_METHOD_ID))
          .expectEvents(new ContactMethodRemovedEvent(PARTY_ID, CONTACT_METHOD_ID));
    }

    @Test
    void givenPartyCreatedEvent_whenConfirmPartyCommand_thenShouldPublishPartyConfirmedEvent() {
        fixture.given(new PartyCreatedEvent(PARTY_ID))
          .when(new ConfirmPartyCommand(PARTY_ID))
          .expectEvents(new PartyConfirmedEvent(PARTY_ID));
    }

    @Test
    void givenPartyCreatedEventAndPartyConfirmedEvent_whenConfirmPartyCommand_thenExpectNoEvents() {
        fixture.given(new PartyCreatedEvent(PARTY_ID), new PartyConfirmedEvent(PARTY_ID))
          .when(new ConfirmPartyCommand(PARTY_ID))
          .expectNoEvents();
    }

    @Test
    void givenPartyCreatedEvent_whenShipPartyCommand_thenShouldThrowUnconfirmedPartyException() {
        fixture.given(new PartyCreatedEvent(PARTY_ID))
          .when(new ShipPartyCommand(PARTY_ID))
          .expectException(UnconfirmedPartyException.class);
    }

    @Test
    void givenPartyCreatedEventAndPartyConfirmedEvent_whenShipPartyCommand_thenShouldPublishPartyShippedEvent() {
        fixture.given(new PartyCreatedEvent(PARTY_ID), new PartyConfirmedEvent(PARTY_ID))
          .when(new ShipPartyCommand(PARTY_ID))
          .expectEvents(new PartyShippedEvent(PARTY_ID));
    }

    @Test
    void givenPartyCreatedEventContactMethodAndPartyConfirmedEvent_whenUpdateContactMethodCommand_thenShouldThrowPartyAlreadyConfirmedException() {
        fixture.given(new PartyCreatedEvent(PARTY_ID), new PartyConfirmedEvent(PARTY_ID))
          .when(new UpdateContactMethodCommand(PARTY_ID, CONTACT_METHOD_ID))
          .expectException(PartyAlreadyConfirmedException.class)
          .expectExceptionMessage(Matchers.predicate(message -> ((String) message).contains(PARTY_ID)));
    }

    @Test
    void givenPartyCreatedEventContactMethodUpdatedEventAndPartyConfirmedEvent_whenIncrementContactMethodCountCommand_thenShouldThrowPartyAlreadyConfirmedException() {
        fixture.given(new PartyCreatedEvent(PARTY_ID), new ContactMethodUpdatedEvent(PARTY_ID, CONTACT_METHOD_ID), new PartyConfirmedEvent(PARTY_ID))
          .when(new IncrementContactMethodCountCommand(PARTY_ID, CONTACT_METHOD_ID))
          .expectException(PartyAlreadyConfirmedException.class)
          .expectExceptionMessage(Matchers.predicate(message -> ((String) message).contains(PARTY_ID)));
    }

    @Test
    void givenPartyCreatedEventContactMethodUpdatedEventAndPartyConfirmedEvent_whenDecrementContactMethodCountCommand_thenShouldThrowPartyAlreadyConfirmedException() {
        fixture.given(new PartyCreatedEvent(PARTY_ID), new ContactMethodUpdatedEvent(PARTY_ID, CONTACT_METHOD_ID), new PartyConfirmedEvent(PARTY_ID))
          .when(new DecrementContactMethodCountCommand(PARTY_ID, CONTACT_METHOD_ID))
          .expectException(PartyAlreadyConfirmedException.class)
          .expectExceptionMessage(Matchers.predicate(message -> ((String) message).contains(PARTY_ID)));
    }
}