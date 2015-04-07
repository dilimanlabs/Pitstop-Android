package com.dilimanlabs.pitstop.events;

import com.dilimanlabs.pitstop.persistence.Business;

public class GetBusinessEvent {
    public final Business business;

    public GetBusinessEvent(Business business) {
        this.business = business;
    }
}
