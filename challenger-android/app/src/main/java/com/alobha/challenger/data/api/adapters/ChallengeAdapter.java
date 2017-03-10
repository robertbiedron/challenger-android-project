package com.alobha.challenger.data.api.adapters;


import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.data.entities.Competitor;
import com.alobha.challenger.data.entities.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChallengeAdapter implements JsonDeserializer<Challenge>, JsonSerializer<Challenge> {

    @Override
    public Challenge deserialize(JsonElement jsonElement, Type typeOF,
                                 JsonDeserializationContext context) throws JsonParseException {
        Challenge item = new Challenge();
        JsonObject object = jsonElement.getAsJsonObject();
        item.id = object.getAsJsonPrimitive("id").getAsLong();
        item.owner = context.deserialize(object.getAsJsonObject("owner"), User.class);
        item.distance = 1000 * (float) context.deserialize(object.getAsJsonPrimitive("distance"), Float.class);
        item.time = (long) (3600000 * (float) context.deserialize(object.getAsJsonPrimitive("time"), Float.class));
        item.start_date = context.deserialize(object.getAsJsonPrimitive("start_date"), Date.class);
        item.end_date = context.deserialize(object.getAsJsonPrimitive("end_date"), Date.class);
        JsonArray competitorsJson = object.getAsJsonArray("competitors");
        item.competitors = new ArrayList<>();
        for (JsonElement competitorJson : competitorsJson) {
            JsonObject competitorObject = competitorJson.getAsJsonObject();
            Competitor competitor = new Competitor();
            competitor.user = context.deserialize(competitorObject.getAsJsonObject("user"), User.class);
            competitor.position = context.deserialize(competitorObject.getAsJsonPrimitive("position"), Integer.class);
            competitor.avg_speed = context.deserialize(competitorObject.getAsJsonPrimitive("avg_speed"), Float.class);
            competitor.distance = 1000 * (float) context.deserialize(competitorObject.getAsJsonPrimitive("distance"), Float.class);
            competitor.time = context.deserialize(competitorObject.getAsJsonPrimitive("time"), Long.class);
            item.competitors.add(competitor);
        }
        Collections.sort(item.competitors);
        for (int i = 0; i < item.competitors.size(); i++) {
            item.competitors.get(i).position = i + 1;
        }
        return item;
    }

    @Override
    public JsonElement serialize(Challenge src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("id", new JsonPrimitive(src.id));
        object.add("host", new JsonPrimitive(src.host));
        object.add("owner", context.serialize(src.owner));
        object.add("distance", context.serialize(src.distance));
        object.add("time", context.serialize(src.time));
        object.add("start_date", context.serialize(src.start_date));
        object.add("end_date", context.serialize(src.end_date));
        JsonArray competitorsJson = new JsonArray();
        List<Competitor> competitors = src.competitors;
        for (Competitor c : competitors) {
            JsonObject competitorJson = new JsonObject();
            competitorJson.add("id", context.serialize(c.user.id));
            competitorJson.add("time", context.serialize(c.time));
            competitorJson.add("distance", context.serialize(c.distance));

            competitorsJson.add(competitorJson);
        }
        object.add("competitors", competitorsJson);
        return object;
    }
}