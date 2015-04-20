package com.dilimanlabs.pitstop.events;

import com.dilimanlabs.pitstop.persistence.Establishment;

import java.util.List;

public class SearchEstablishmentsEvent {
    public final String query;
    public final List<Establishment> establishments;

    public SearchEstablishmentsEvent(String query, List<Establishment> establishments) {
        this.query = query;
        this.establishments = establishments;
    }

}
