package org.example;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

public class JsonMovieParser {

    public static Movie parseJsonFromApi(String movieTitle) {
        String jsonResponse = MovieSearch.searchMovie(movieTitle);

        if (jsonResponse == null) {
            System.out.println("Błąd podczas pobierania danych z API.");
            return null;
        }

        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        if (jsonObject.get("Response").getAsString().equals("False")) {
            System.out.println("Nie znaleziono filmu o takim tytule.");
            return null;
        }

        String title = jsonObject.get("Title").getAsString();
        String runtime = jsonObject.get("Runtime").getAsString();
        String genre = jsonObject.get("Genre").getAsString();
        double imdbRating = jsonObject.get("imdbRating").getAsDouble();
        String imdbUrl = "https://www.imdb.com/title/" + jsonObject.get("imdbID").getAsString();
        String poster = jsonObject.get("Poster").getAsString();


        return new Movie(title, runtime, genre, imdbRating, imdbUrl, poster);
    }

    public static int parseRuntime(String runtimeStr) {
        String runtimeNumber = runtimeStr.replaceAll("[^\\d]", "");
        try {
            return Integer.parseInt(runtimeNumber);
        } catch (NumberFormatException e) {
            System.out.println("Błąd przy parsowaniu czasu trwania filmu.");
            return 0;
        }
    }
}



