package com.dkondratov.opengame.network.gson_deserializers;

import com.dkondratov.opengame.model.Field;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by andrew on 12.04.2015.
 */
public class FieldDeserializer implements JsonDeserializer<Field> {

    private Gson gson = new GsonBuilder()
            .create();

    @Override
    public Field deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Field field = gson.fromJson(json, Field.class);

        return field;
    }
}
