package com.dilimanlabs.pitstop.events;

public class AuthenticationErrorEvent extends BaseEvent {
    public final Event event;

    public AuthenticationErrorEvent(Event event) {
        this.event = event;
    }
}
