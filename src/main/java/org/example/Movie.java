package org.example;

public class Movie {
    private String title;
    private String runtime;
    private String genre;
    private double imdbRating;
    private String imdbUrl;  // URL do IMDb
    private String userReview;
    private String poster;
    private Integer userRating = null;
    private MovieState state;

    public Movie(String title, String runtime, String genre, double imdbRating, String imdbUrl, String poster) {
        this.title = title;
        this.runtime = runtime;
        this.genre = genre;
        this.imdbRating = imdbRating;
        this.imdbUrl = imdbUrl;
        this.state = new UnwatchedState();
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getGenre() {
        return genre;
    }

    public double getImdbRating() {
        return imdbRating;
    }


    public String getImdbUrl() {
        return imdbUrl;
    }

    public void setUserReview(String userReview) {
        this.userReview = userReview;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getUserReview() {
        return userReview;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public MovieState getState() {
        return state;
    }

    public void setState(MovieState state) {
        this.state = state;
    }

    public String getStateName(){
        return state.getStateName();
    }

    public String getPoster() {
        return poster;
    }


    @Override
    public String toString() {
        return "<html>" +
                "<div style='text-align: center;'>" +
                "<img src='" + poster + "' alt='Poster' width='150' height='200'><br>"  +
                "<b>Title:</b> " + title + "<br>" +
                "<b>Runtime:</b> " + runtime + "<br>" +
                "<b>Genre:</b> " + genre + "<br>" +
                "<b>IMDb Rating:</b> " + imdbRating + "<br>" +
                "<b>IMDb Link:</b> <a href='" + imdbUrl + "'>" + imdbUrl + "</a>" +
                "</div>" +
                "</html>";
    }



}
