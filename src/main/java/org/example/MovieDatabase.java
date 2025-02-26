package org.example;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MovieDatabase {
    private static MovieDatabase instance = null;
    private List<Movie> movies;

    private MovieDatabase() {
        movies = new ArrayList<Movie>();
    }

    public static MovieDatabase getInstance() {
        if (instance == null) {
            instance = new MovieDatabase();
        }
        return instance;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }
    public void removeMovie(Movie movie) {
        movies.remove(movie);
    }
    public void saveToJsonFile(String filename) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MovieState.class, new MovieStateAdapter())
                .setPrettyPrinting()
                .create();

        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(movies, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromJsonFile(String filename) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MovieState.class, new MovieStateAdapter())
                .create();

        try (Reader reader = new FileReader(filename)) {
            Type movieListType = new TypeToken<List<Movie>>(){}.getType();
            movies = gson.fromJson(reader, movieListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    public Iterator<Movie> iterator() {
        return new MovieIterator();
    }


    private class MovieIterator implements Iterator<Movie> {
        private int index = 0;

        public boolean hasNext() {
            return index < movies.size();
        }

        public Movie next() {
            if (hasNext()) {
                return movies.get(index++);
            } else {
                throw new IndexOutOfBoundsException();
            }

        }
    }
}