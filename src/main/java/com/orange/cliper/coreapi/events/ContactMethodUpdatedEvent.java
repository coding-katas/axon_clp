package com.orange.cliper.coreapi.events;

import java.util.Objects;

public class ContactMethodUpdatedEvent {

    private final String partyId;
    private final String contactMethodId;

    public ContactMethodUpdatedEvent(String partyId, String contactMethodId) {
        this.partyId = partyId;
        this.contactMethodId = contactMethodId;
    }

    public String getPartyId() {
        return partyId;
    }

    public String getContactMethodId() {
        return contactMethodId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContactMethodUpdatedEvent that = (ContactMethodUpdatedEvent) o;
        return Objects.equals(partyId, that.partyId) && Objects.equals(contactMethodId, that.contactMethodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId, contactMethodId);
    }

    @Override
    public String toString() {
        return "ContactMethodUpdatedEvent{" + "partyId='" + partyId + '\'' + ", contactMethodId='" + contactMethodId + '\'' + '}';
    }
}
