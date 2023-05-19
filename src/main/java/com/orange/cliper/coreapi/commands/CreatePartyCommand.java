package com.orange.cliper.coreapi.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Objects;

public class CreatePartyCommand {

    @TargetAggregateIdentifier
    private final String partyId;

    public CreatePartyCommand(String partyId) {
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
        CreatePartyCommand that = (CreatePartyCommand) o;
        return Objects.equals(partyId, that.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId);
    }

    @Override
    public String toString() {
        return "CreatePartyCommand{" + "partyId='" + partyId + '\'' + '}';
    }
}