package com.example.android.myapplication.Data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Hazem on 11/8/2016.
 */

public class TopMovies extends RealmObject {
    @PrimaryKey
    private int id;
    private String poster;
    private String overView;
    private String releaseDate;
    private String title;
    private String url1;
    private String url2;
    private RealmList<UserReviews> reviews;

    public RealmList<UserReviews> getReviews() {
        return reviews;
    }

    public void setReviews(RealmList<UserReviews> reviews) {
        this.reviews = reviews;
    }

    public String getUrl1() {
        return url1;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public String getUrl2() {
        return url2;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }

    private String voteAverage;
    public TopMovies(){
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


