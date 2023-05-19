package com.orange.cliper.commandmodel.party;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.EntityId;

import java.util.Objects;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

public class ContactMethod {

    @EntityId
    private final String contactMethodId;

    public ContactMethod(String contactMethodId) {
        this.contactMethodId = contactMethodId;
    }

}
