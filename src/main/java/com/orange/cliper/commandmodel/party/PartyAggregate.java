package com.orange.cliper.commandmodel.party;

import com.orange.cliper.coreapi.commands.UpdateContactMethodCommand;
import com.orange.cliper.coreapi.commands.CreatePartyCommand;

import com.orange.cliper.coreapi.events.PartyCreatedEvent;
import com.orange.cliper.coreapi.events.ContactMethodUpdatedEvent;

import com.orange.cliper.coreapi.exceptions.DuplicateContactMethodException;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.HashMap;
import java.util.Map;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate(snapshotTriggerDefinition = "partyAggregateSnapshotTriggerDefinition")
public class PartyAggregate {

    @AggregateIdentifier
    private String partyId;

    @AggregateMember
    private Map<String, ContactMethod> contactMethods;

    @CommandHandler
    public PartyAggregate(CreatePartyCommand command) {
        apply(new PartyCreatedEvent(command.getPartyId()));
    }

    @CommandHandler
    public void handle(UpdateContactMethodCommand command) {

        String contactMethodId = command.getContactMethodId();
        if (contactMethods.containsKey(contactMethodId)) {
            throw new DuplicateContactMethodException(contactMethodId);
        }
        apply(new ContactMethodUpdatedEvent(partyId, contactMethodId));
    }

    @EventSourcingHandler
    public void on(PartyCreatedEvent event) {
        this.partyId = event.getPartyId();
        this.contactMethods = new HashMap<>();
    }

    @EventSourcingHandler
    public void on(ContactMethodUpdatedEvent event) {
        String contactMethodId = event.getContactMethodId();
        this.contactMethods.put(contactMethodId, new ContactMethod(contactMethodId));
    }

    protected PartyAggregate() {
        // Required by Axon to build a default Aggregate prior to Event Sourcing
    }
}