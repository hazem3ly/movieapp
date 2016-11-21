package com.example.android.myapplication.Data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Hazem on 11/8/2016.
 */

public class PopMovies extends RealmObject {
    @PrimaryKey
    private int id;
    private int index;
    private String poster,overView,releaseDate,title,voteAverage,backdrop_path;
    private RealmList<UserReviews> reviews;
    private RealmList<TrailersData> trailers;

    public PopMovies() {
    }

    public RealmList<UserReviews> getReviews() {
        return reviews;
    }

    public void setReviews(RealmList<UserReviews> reviews) {
        this.reviews = reviews;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public RealmList<TrailersData> getTrailers() {
        return trailers;
    }

    public void setTrailers(RealmList<TrailersData> trailers) {
        this.trailers = trailers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}



