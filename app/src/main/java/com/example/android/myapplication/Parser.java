package com.example.android.myapplication;

import android.util.Log;

import com.example.android.myapplication.Data.Movies;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Hazem on 11/9/2016.
 */

public class Parser {
    MainActivityFragment fragment = null;
    String videoUrl;
    List<String> reviewsList;
    List<Movies> moviesList;
    public List<Movies> movieJsonStrParsing(String movieJsonString) throws Exception {
        JSONObject response = new JSONObject(movieJsonString);
        response.getInt("page");
        Log.d("id off page movie is?", String.valueOf(response.getInt("page")));
        JSONArray result = response.getJSONArray("results");
        // To prevent Data duplication
        moviesList.clear();
        for (int i = 0; i < result.length(); i++) {
            JSONObject obj = result.getJSONObject(i);
            Movies movies = new Movies(obj.getInt("id"), obj.getString("poster_path"), obj.getString("overview"),
                    obj.getString("release_date"), obj.getString("title"), obj.getString("vote_average"));
            moviesList.add(movies);
            int mMovieId = obj.getInt("id");
            Log.d("id off avorit movie is?", String.valueOf(mMovieId));
            //Log.d("SIZE OF ARRAY MOVIES", String.valueOf(moviesList.size()));
            fragment.saveToDatabase(obj.getInt("id"), obj.getString("poster_path"), obj.getString("overview"),
                    obj.getString("release_date"), obj.getString("title"), obj.getString("vote_average"));
        }
        return moviesList;
    }
    public String videoJsonStrParsing (String videoJsonStr) throws Exception {

    return videoUrl;
    }
    public List<String> reviewsJsonStrParsing (String reviewJsonStr) throws Exception {

        return reviewsList;
    }


}
