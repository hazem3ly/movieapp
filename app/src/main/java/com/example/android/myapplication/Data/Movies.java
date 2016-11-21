package com.example.android.myapplication.Data;

/**
 * Created by Hazem on 10/21/2016.
 */

public class Movies {
    private int id, index;
    private String poster, overView, releaseDate, title, voteAverage, URL, url_name, reviews, backdrop;

    public Movies(int id, String movieURL, String url_name) {
        this.id = id;
        this.URL = movieURL;
        this.url_name = url_name;
    }

    public Movies(int id, String reviews) {
        this.id = id;
        this.reviews = reviews;
    }

    public Movies(int id, String poster, String overView,
                  String releaseDate, String title, String voteAverage, String backdrop) {
        this.id = id;
        this.poster = poster;
        this.overView = overView;
        this.releaseDate = releaseDate;
        this.backdrop = backdrop;
        this.title = title;
        this.voteAverage = voteAverage;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUrl_name() {
        return url_name;
    }

    public void setUrl_name(String url_name) {
        this.url_name = url_name;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }
}
