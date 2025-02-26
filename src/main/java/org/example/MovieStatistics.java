package org.example;

import java.util.*;

public  class MovieStatistics {


    public static int sumWatchTime(MovieDatabase movieDatabase) {
        int sum=0;
        Iterator<Movie> movieIterator = movieDatabase.iterator();
        while(movieIterator.hasNext()) {
            Movie movie = movieIterator.next();
            if("Watched".equals(movie.getState().getStateName())){
                sum += JsonMovieParser.parseRuntime(movie.getRuntime());
            }
        }
        return sum;
    }
    public static String getFavoriteGenre(MovieDatabase movieDatabase) {
        Map<String, Integer> genreCount = new HashMap<>();

        Iterator<Movie> movieIterator = movieDatabase.iterator();
        while (movieIterator.hasNext()) {
            Movie movie = movieIterator.next();
            if ("Watched".equals(movie.getState().getStateName())) {
                String[] genres = movie.getGenre().split(", ");
                for (String genre : genres) {
                    genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
                }
            }
        }

        int maxCount = Collections.max(genreCount.values());


        List<String> favoriteGenres = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : genreCount.entrySet()) {
            if (entry.getValue() == maxCount) {
                favoriteGenres.add(entry.getKey());
            }
        }

        return String.join(", ", favoriteGenres);
    }
}
