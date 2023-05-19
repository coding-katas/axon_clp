package com.orange.cliper.coreapi.queries;

import java.util.Objects;

public class PartyUpdatesQuery {

    private final String partyId;

    public PartyUpdatesQuery(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyId() {
        return partyId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PartyUpdatesQuery that = (PartyUpdatesQuery) o;
        return Objects.equals(partyId, that.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId);
    }

    @Override
    public String toString() {
        return "PartyUpdatesQuery{" + "partyId='" + partyId + '\'' + '}';
    }
}
