package org.example;

public class UnwatchedState implements MovieState{
    @Override
    public void handle(Movie movie) {
        System.out.println(movie.getTitle() + " is now marked as not watched.");
    }

    @Override
    public String getStateName() {
        return "Unwatched";
    }

}
