package com.example.android.myapplication;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hazem on 11/9/2016.
 */

public class MyAsyncTask extends AsyncTask<String, Void, String> {
    DataReadyInterface listener;
    String Data;
    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JsonString = null;
        try {
            Data = params[1]; // set the compare string to see if movie or video or reviews
            final String MovieDB_BASE_URL = ("http://api.themoviedb.org/3/movie/" + params[0]);
            final String API_Key_PARAM = "api_key";
            final String My_Key = "39dbc8225484ac7c6ceca6ff3701b74b";
            Uri builtUri = Uri.parse(MovieDB_BASE_URL).buildUpon()
                    .appendQueryParameter(API_Key_PARAM,My_Key)
                    .build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            JsonString = buffer.toString();
        } catch (IOException e) {
            Log.e("Network Connection ", "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Error", " closing stream", e);
                }
            }}
        return JsonString;
    }



    @Override
    protected void onPreExecute() {
        listener.onFetchProgress();
//        mGridView.setVisibility(View.GONE);
//        spinner.setVisibility(View.VISIBLE);

    }
    @Override
    protected void onPostExecute(String result) {
        listener.onFetchFinish();
//        mGridView.setVisibility(View.VISIBLE);
//        spinner.setVisibility(View.GONE);
        if (Data.equals("movies")){
            listener.onMovieDataReady(result);
        }
        if (Data.equals("videos")){
            listener.onVideoDataReady(result);
        }
        if (Data.equals("reviews")){
            listener.onReviewDataReady(result);
        }

    }
    public void setOnListener(DataReadyInterface listener) {
        this.listener = listener;

    }


}