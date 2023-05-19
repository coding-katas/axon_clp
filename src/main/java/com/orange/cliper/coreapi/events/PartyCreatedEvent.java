package com.orange.cliper.coreapi.events;

import java.util.Objects;

public class PartyCreatedEvent {

    private final String partyId;

    public PartyCreatedEvent(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyId() {
        return partyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PartyCreatedEvent that = (PartyCreatedEvent) o;
        return Objects.equals(partyId, that.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId);
    }

    @Override
    public String toString() {
        return "PartyCreatedEvent{" + "partyId='" + partyId + '\'' + '}';
    }
}