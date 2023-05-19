package com.orange.cliper.querymodel;

import com.orange.cliper.coreapi.queries.PartyStatus;

public enum PartyStatusResponse {
    CREATED, UPDATED, UNKNOWN;

    static PartyStatusResponse toResponse(PartyStatus status) {
        for (PartyStatusResponse response : values()) {
            if (response.toString()
              .equals(status.toString())) {
                return response;
            }
        }
        return UNKNOWN;
    }
}
