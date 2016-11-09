package com.example.android.myapplication;

/**
 * Created by Hazem on 11/7/2016.
 */

public interface DataReadyInterface {
    public void onFetchProgress();
    public void onFetchFinish();
    public void onMovieDataReady(String jsonStr);
    public void onVideoDataReady(String jsonStr);
    public void onReviewDataReady(String jsonStr);

}
