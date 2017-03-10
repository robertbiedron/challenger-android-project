package com.alobha.challenger.data.api.adapters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ArrayAdapter implements JsonSerializer<ArrayList<String>> {
    @Override
    public JsonElement serialize(ArrayList<String> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray jsonElements = new JsonArray();
        for (int i = 0; i < src.size(); i++)
            jsonElements.add(context.serialize(src.get(i)));
        return jsonElements;
    }
}