package org.example;

public interface MovieState {
    void handle(Movie movie);
    String getStateName();
}
