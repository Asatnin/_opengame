package com.dkondratov.opengame.network.gson_deserializers;

import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class EventDeserializer implements JsonDeserializer<Event> {

    private Gson gson = new GsonBuilder()
            .create();

    @Override
    public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Event event = gson.fromJson(jsonObject, Event.class);

        return event;
    }
}
