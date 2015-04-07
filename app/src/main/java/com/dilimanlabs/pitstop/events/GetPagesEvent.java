package com.dilimanlabs.pitstop.events;

import com.dilimanlabs.pitstop.persistence.Page;

import java.util.List;

public class GetPagesEvent {
    public final String businessUrl;
    public final List<Page> pages;

    public GetPagesEvent(String businessUrl, List<Page> pages) {
        this.businessUrl = businessUrl;
        this.pages = pages;
    }
}
