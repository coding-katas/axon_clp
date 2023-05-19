package com.orange.cliper.querymodel;

import com.orange.cliper.coreapi.queries.FindAllPartiesContactMethodsQuery;
import com.orange.cliper.coreapi.events.PartyCreatedEvent;
import com.orange.cliper.coreapi.events.ContactMethodUpdatedEvent;
import com.orange.cliper.coreapi.queries.FindAllPartiesQuery;
import com.orange.cliper.coreapi.queries.Party;
import com.orange.cliper.coreapi.queries.PartyStatus;
import com.orange.cliper.coreapi.queries.PartyUpdatesQuery;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@ProcessingGroup("parties")
@Profile("!mongo")
public class InMemoryPartiesEventHandler implements PartiesEventHandler {

    private final Map<String, Party> parties = new HashMap<>();
    private final QueryUpdateEmitter emitter;

    public InMemoryPartiesEventHandler(QueryUpdateEmitter emitter) {
        this.emitter = emitter;
    }

    @EventHandler
    public void on(PartyCreatedEvent event) {
        String partyId = event.getPartyId();
        parties.put(partyId, new Party(partyId));
    }

    @EventHandler
    public void on(ContactMethodUpdatedEvent event) {
        parties.computeIfPresent(event.getPartyId(), (partyId, party) -> {
            party.addContactMethod(event.getContactMethodId());
            emitUpdate(party);
            return party;
        });
    }


    @QueryHandler
    public List<Party> handle(FindAllPartiesQuery query) {
        return new ArrayList<>(parties.values());
    }

    @QueryHandler
    public Publisher<Party> handleStreaming(FindAllPartiesContactMethodsQuery query) {
        return Mono.fromCallable(parties::values)
          .flatMapMany(Flux::fromIterable);
    }

    @QueryHandler
    public Party handle(PartyUpdatesQuery query) {
        return parties.get(query.getPartyId());
    }

    private void emitUpdate(Party party) {
        emitter.emit(PartyUpdatesQuery.class, q -> party.getPartyId()
          .equals(q.getPartyId()), party);
    }

    @Override
    public void reset(List<Party> partyList) {
        parties.clear();
        partyList.forEach(o -> parties.put(o.getPartyId(), o));
    }
}
