package com.example.android.myapplication.Data;

import io.realm.RealmObject;

/**
 * Created by Hazem on 11/12/2016.
 */
// as realm db does not support String list so i create this class
public class UserReviews extends RealmObject {
    private int index;
    private String reviews;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }
}
