package org.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializationContext;

import java.lang.reflect.Type;

public class MovieStateAdapter implements JsonSerializer<MovieState>, JsonDeserializer<MovieState> {

    @Override
    public JsonElement serialize(MovieState src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("stateName", src.getStateName());
        return jsonObject;
    }

    @Override
    public MovieState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        String stateName = jsonObject.get("stateName").getAsString();

        switch (stateName) {
            case "Unwatched":
                return new UnwatchedState();
            case "Watched":
                return new WatchedState();
            default:
                throw new IllegalArgumentException("Unknown state: " + stateName);
        }
    }
}
