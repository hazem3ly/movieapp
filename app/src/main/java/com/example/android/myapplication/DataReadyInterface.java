package com.example.android.myapplication;

import com.example.android.myapplication.Data.Movies;

import java.util.List;

/**
 * Created by Hazem on 11/7/2016.
 */

public interface DataReadyInterface {
    public void onFetchProgress();

    public void onFetchFinish();

    public void onMovieDataReady(List<Movies> jsonStr);

    public void onVideoDataReady(List<Movies> jsonStrz);

    public void onReviewDataReady(List<Movies> jsonStr);

}