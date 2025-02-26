package org.example;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MovieSearch {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String BASE_URL = "http://www.omdbapi.com/";

    public static String searchMovie(String title) {
        try {
            String url = BASE_URL + "?apikey=" + API_KEY + "&t=" + title.replace(" ", "+");
            return sendRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Wyszukiwanie listy filmów według gatunku
    public static List<String> searchMoviesByGenre(String genre) {
        try {
            String url = BASE_URL + "?apikey=" + API_KEY + "&type=movie&s=" + genre.replace(" ", "+");
            String response = sendRequest(url);

            return parseMoviesByGenre(response, genre);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static String sendRequest(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static List<String> parseMoviesByGenre(String response, String genre) {
        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
        List<String> movieList = new ArrayList<>();

        if (!jsonResponse.has("Search")) {
            return movieList;
        }

        JsonArray searchResults = jsonResponse.getAsJsonArray("Search");

        for (var element : searchResults) {
            JsonObject movie = element.getAsJsonObject();

            String movieDetails = fetchMovieDetails(movie.get("imdbID").getAsString());
            if (movieDetails == null) continue;

            JsonObject movieDetailsJson = gson.fromJson(movieDetails, JsonObject.class);
            if (!movieDetailsJson.has("Genre")) continue;

            String genres = movieDetailsJson.get("Genre").getAsString();
            if (genres.toLowerCase().contains(genre.toLowerCase())) {
                movieList.add(movie.get("Title").getAsString());
            }
        }

        return movieList;
    }

    private static String fetchMovieDetails(String imdbID) {
        try {
            String url = BASE_URL + "?apikey=" + API_KEY + "&i=" + imdbID;
            return sendRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
