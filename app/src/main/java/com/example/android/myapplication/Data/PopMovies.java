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
    private String poster;
    private int index;
    private String overView;
    private String releaseDate;
    private String title;
    private String voteAverage;
    private String URL1;
    private String URL2;
    private RealmList<UserReviews> reviews;

    public RealmList<UserReviews> getReviews() {
        return reviews;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setReviews(RealmList<UserReviews> reviews) {
        this.reviews = reviews;
    }

    public String getURL1() {
        return URL1;
    }

    public void setURL1(String URL1) {
        this.URL1 = URL1;
    }

    public String getURL2() {
        return URL2;
    }

    public void setURL2(String URL2) {
        this.URL2 = URL2;
    }

    private boolean isFavorite;

    public PopMovies() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPoster() {
        return poster;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getOverView() {
        return overView;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}



