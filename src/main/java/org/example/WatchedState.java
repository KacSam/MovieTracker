package org.example;

public class WatchedState implements MovieState{
    @Override
    public void handle(Movie movie) {
        System.out.println(movie.getTitle() + " is now marked as watched.");
    }

    @Override
    public String getStateName() {
        return "Watched";
    }
}
