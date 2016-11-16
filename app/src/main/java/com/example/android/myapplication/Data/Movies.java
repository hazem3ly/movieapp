package com.example.android.myapplication.Data;

import java.util.List;

/**
 * Created by Hazem on 10/21/2016.
 */

public class Movies  {
    private int id;
    private String poster;
    private String overView;
    private String releaseDate;
    private String title;
    private int index;
    private String voteAverage;
    private List<String> URL;
    private String reviews;
    public Movies(int id,List<String> movieURL){
        this.id = id;
        this.URL = movieURL;
    }
    public Movies(int id,String reviews){
        this.id = id;
        this.reviews = reviews;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getURL() {
        return URL;
    }

    public void setURL(List<String> URL) {
        this.URL = URL;
    }

    public Movies(int id, String poster, String overView,
                  String releaseDate, String title, String voteAverage) {
        this.id=id;
        this.poster = poster;
        this.overView = overView;
        this.releaseDate = releaseDate;

        this.title = title;
        this.voteAverage = voteAverage;
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
    public void setTitle(String title) {
        this.title = title;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }
    public void setOverView(String overView) {
        this.overView = overView;
    }
    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public String getPoster() {
        return poster;
    }
    public String getOverView() {
        return overView;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public String getTitle() {
        return title;
    }
    public String getVoteAverage() {
        return voteAverage;
    }
}
