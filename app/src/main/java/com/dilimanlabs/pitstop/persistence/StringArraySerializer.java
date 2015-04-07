package com.dilimanlabs.pitstop.persistence;

import com.activeandroid.serializer.TypeSerializer;
import com.google.gson.Gson;

public class StringArraySerializer extends TypeSerializer {
    private static final Gson gson = new Gson();

    @Override
    public Class<?> getDeserializedType() {
        return String[].class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public String serialize(Object object) {
        if (object == null) {
            return null;
        }

        return gson.toJson(object);
    }

    @Override
    public String[] deserialize(Object object) {
        if (object == null) {
            return null;
        }

        return gson.fromJson(object.toString(), String[].class);
    }
}
