package com.example.android.myapplication.Data;

import io.realm.RealmObject;

/**
 * Created by Hazem on 11/19/2016.
 */
// as realm db does not support String list so i create this class to use when creating Trailers list
public class TrailersData extends RealmObject {
    private String url;
    private String name;

    public TrailersData() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
