package com.orange.cliper.querymodel;

import com.orange.cliper.coreapi.queries.FindAllPartiesContactMethodsQuery;
import com.orange.cliper.coreapi.queries.Party;
import com.orange.cliper.coreapi.queries.PartyUpdatesQuery;

import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PartyQueryService {

    private final QueryGateway queryGateway;

    public PartyQueryService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    public CompletableFuture<List<PartyResponse>> findAllParties() {
        return queryGateway.query(new FindAllPartiesContactMethodsQuery(), ResponseTypes.multipleInstancesOf(Party.class))
          .thenApply(r -> r.stream()
            .map(PartyResponse::new)
            .collect(Collectors.toList()));
    }

    public Flux<PartyResponse> allPartiesStreaming() {
        Publisher<Party> publisher = queryGateway.streamingQuery(new FindAllPartiesContactMethodsQuery(), Party.class);
        return Flux.from(publisher)
          .map(PartyResponse::new);
    }

    public Flux<PartyResponse> partyUpdates(String partyId) {
        return subscriptionQuery(new PartyUpdatesQuery(partyId), ResponseTypes.instanceOf(Party.class)).map(PartyResponse::new);
    }

    private <Q, R> Flux<R> subscriptionQuery(Q query, ResponseType<R> resultType) {
        SubscriptionQueryResult<R, R> result = queryGateway.subscriptionQuery(query, resultType, resultType);
        return result.initialResult()
          .concatWith(result.updates())
          .doFinally(signal -> result.close());
    }
}
