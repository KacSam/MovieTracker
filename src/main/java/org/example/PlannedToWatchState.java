package org.example;

public class PlannedToWatchState implements MovieState{

    @Override
    public void handle(Movie movie) {
        System.out.println(movie.getTitle() + " is now planned to watch.");
    }

    @Override
    public String getStateName() {
        return "Planned to Watch";
    }
}
