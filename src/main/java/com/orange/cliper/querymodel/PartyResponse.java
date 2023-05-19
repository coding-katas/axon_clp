package com.orange.cliper.querymodel;

import com.orange.cliper.coreapi.queries.Party;

import java.util.Map;

import static com.orange.cliper.querymodel.PartyStatusResponse.toResponse;

public class PartyResponse {

    private String partyId;
    private Map<String, Integer> contactMethods;
    private PartyStatusResponse partyStatus;

    PartyResponse(Party party) {
        this.partyId = party.getPartyId();
        this.contactMethods = party.getContactMethods();
        this.partyStatus = toResponse(party.getPartyStatus());
    }

    /**
     * Added for the integration test, since it's using Jackson for the response
     */
    PartyResponse() {
    }

    public String getPartyId() {
        return partyId;
    }

    public Map<String, Integer> getContactMethods() {
        return contactMethods;
    }

    public PartyStatusResponse getPartyStatus() {
        return partyStatus;
    }
}
