package com.example.android.myapplication.Data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Hazem on 11/8/2016.
 */

public class FavMovies extends RealmObject {
    @PrimaryKey
    private int id;
    private String poster;
    private String overView;
    private String releaseDate;
    private String title;
    private String Back_Drop;
    private String voteAverage;
    private RealmList<UserReviews> reviews;
    private RealmList<TrailersData> trailers;

    public FavMovies(){
    }
    public FavMovies(int id, String poster, String overView,
                     String releaseDate, String title, String voteAverage) {
        this.poster = poster;
        this.overView = overView;
        this.releaseDate = releaseDate;
        this.title = title;
        this.voteAverage = voteAverage;
    }
    public int getId() {
        return id;
    }

    public String getBack_Drop() {
        return Back_Drop;
    }

    public RealmList<UserReviews> getReviews() {
        return reviews;
    }

    public void setReviews(RealmList<UserReviews> reviews) {
        this.reviews = reviews;
    }

    public RealmList<TrailersData> getTrailers() {
        return trailers;
    }

    public void setTrailers(RealmList<TrailersData> trailers) {
        this.trailers = trailers;
    }

    public void setBack_Drop(String back_Drop) {
        Back_Drop = back_Drop;
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

