package com.orange.cliper.gui;

import com.orange.cliper.coreapi.commands.UpdateContactMethodCommand;
import com.orange.cliper.coreapi.commands.CreatePartyCommand;
import com.orange.cliper.querymodel.PartyQueryService;
import com.orange.cliper.querymodel.PartyResponse;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
public class PartyRestEndpoint {

    private final CommandGateway commandGateway;
    private final PartyQueryService partyQueryService;

    public PartyRestEndpoint(CommandGateway commandGateway, PartyQueryService partyQueryService) {
        this.commandGateway = commandGateway;
        this.partyQueryService = partyQueryService;
    }

    @PostMapping("/party")
    public CompletableFuture<String> createParty() {
        return createParty(UUID.randomUUID()
          .toString());
    }

    @PostMapping("/party/{party-id}")
    public CompletableFuture<String> createParty(@PathVariable("party-id") String partyId) {
        return commandGateway.send(new CreatePartyCommand(partyId));
    }

    @PostMapping("/party/{party-id}/contactMethod/{contact-method-id}")
    public CompletableFuture<Void> updateContactMethod(@PathVariable("party-id") String partyId, @PathVariable("contact-method-id") String contactMethodId) {
        return commandGateway.send(new UpdateContactMethodCommand(partyId, contactMethodId));
    }


    @GetMapping("/all-parties")
    public CompletableFuture<List<PartyResponse>> findAllParties() {
        return partyQueryService.findAllParties();
    }

    @GetMapping(path = "/all-parties-streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PartyResponse> allPartiesStreaming() {
        return partyQueryService.allPartiesStreaming();
    }

    @GetMapping(path = "/party-updates/{party-id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PartyResponse> partyUpdates(@PathVariable("party-id") String partyId) {
        return partyQueryService.partyUpdates(partyId);
    }
}
