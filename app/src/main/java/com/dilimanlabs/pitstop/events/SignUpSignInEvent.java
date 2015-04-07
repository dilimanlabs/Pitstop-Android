package com.dilimanlabs.pitstop.events;

public class SignUpSignInEvent extends BaseEvent {
    public static enum Type {
        SIGNUP, SIGNIN
    }

    public final Type type;
    public final Event event;

    public SignUpSignInEvent(Type type, Event event) {
        this.type = type;
        this.event = event;
    }
}
