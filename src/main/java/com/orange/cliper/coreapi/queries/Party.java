package com.orange.cliper.coreapi.queries;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Party {

    private final String partyId;
    private final Map<String, Integer> contactMethods;
    private PartyStatus partyStatus;

    public Party(String partyId) {
        this.partyId = partyId;
        this.contactMethods = new HashMap<>();
        partyStatus = PartyStatus.CREATED;
    }

    public String getPartyId() {
        return partyId;
    }

    public Map<String, Integer> getContactMethods() {
        return contactMethods;
    }

    public PartyStatus getPartyStatus() {
        return partyStatus;
    }

    public void addContactMethod(String contactMethodId) {
        contactMethods.putIfAbsent(contactMethodId, 1);
    }

    public void incrementContactMethodInstance(String contactMethodId) {
        contactMethods.computeIfPresent(contactMethodId, (id, count) -> ++count);
    }

    public void decrementContactMethodInstance(String contactMethodId) {
        contactMethods.computeIfPresent(contactMethodId, (id, count) -> --count);
    }

    public void removeContactMethod(String contactMethodId) {
        contactMethods.remove(contactMethodId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Party that = (Party) o;
        return Objects.equals(partyId, that.partyId) && Objects.equals(contactMethods, that.contactMethods) && partyStatus == that.partyStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId, contactMethods, partyStatus);
    }

    @Override
    public String toString() {
        return "Party{" + "partyId='" + partyId + '\'' + ", contactMethods=" + contactMethods + ", partyStatus=" + partyStatus + '}';
    }
}
