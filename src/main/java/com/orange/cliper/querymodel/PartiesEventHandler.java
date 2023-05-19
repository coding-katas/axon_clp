package com.orange.cliper.querymodel;


import com.orange.cliper.coreapi.queries.FindAllPartiesContactMethodsQuery;
import com.orange.cliper.coreapi.events.PartyCreatedEvent;
import com.orange.cliper.coreapi.events.ContactMethodUpdatedEvent;
import com.orange.cliper.coreapi.queries.FindAllPartiesQuery;
import com.orange.cliper.coreapi.queries.Party;
import com.orange.cliper.coreapi.queries.PartyUpdatesQuery;

import org.reactivestreams.Publisher;

import java.util.List;

public interface PartiesEventHandler {

    void on(PartyCreatedEvent event);

    void on(ContactMethodUpdatedEvent event);

    List<Party> handle(FindAllPartiesContactMethodsQuery query);

    Publisher<Party> handleStreaming(FindAllPartiesContactMethodsQuery query);

    Party handle(PartyUpdatesQuery query);

    void reset(List<Party> partyList);
}
