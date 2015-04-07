package com.dilimanlabs.pitstop.events;

import com.dilimanlabs.pitstop.persistence.Establishment;

public class GetEstablishmentEvent {
    public final Establishment establishment;

    public GetEstablishmentEvent(Establishment establishment) {
        this.establishment = establishment;
    }
}
