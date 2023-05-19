package com.orange.cliper.coreapi.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Objects;

public class UpdateContactMethodCommand {

    @TargetAggregateIdentifier
    private final String partyId;
    private final String contactMethodId;

    public UpdateContactMethodCommand(String partyId, String contactMethodId) {
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
        UpdateContactMethodCommand that = (UpdateContactMethodCommand) o;
        return Objects.equals(partyId, that.partyId) && Objects.equals(contactMethodId, that.contactMethodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId, contactMethodId);
    }

    @Override
    public String toString() {
        return "UpdateContactMethodCommand{" + "partyId='" + partyId + '\'' + ", contactMethodId='" + contactMethodId + '\'' + '}';
    }
}
