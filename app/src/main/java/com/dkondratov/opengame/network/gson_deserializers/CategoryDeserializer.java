package com.dkondratov.opengame.network.gson_deserializers;

import com.dkondratov.opengame.model.Category;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by andrew on 09.04.2015.
 */
public class CategoryDeserializer implements JsonDeserializer<Category> {

    private final static String ICON_URL_FIELD = "icon_url";
    private final static String NAME_FIELD = "name";
    private final static String SPORT_ID_FIELD = "sport_id";
    private final static String ORDER_FIELD = "order";

    @Override
    public Category deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Category category = new Category();
        category.setIconUrl(jsonObject.get(ICON_URL_FIELD).getAsString());
        category.setName(jsonObject.get(NAME_FIELD).getAsString());
        category.setSportId(jsonObject.get(SPORT_ID_FIELD).getAsString());
        category.setOrder(jsonObject.get(ORDER_FIELD).getAsInt());

        return category;
    }
}
