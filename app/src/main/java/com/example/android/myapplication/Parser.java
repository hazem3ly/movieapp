package com.example.android.myapplication;

import android.util.Log;

import com.example.android.myapplication.Data.Movies;
import com.example.android.myapplication.Data.PopMovies;
import com.example.android.myapplication.Data.TopMovies;
import com.example.android.myapplication.Data.UserReviews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Hazem on 11/9/2016.
 */

public class Parser {
    MainFragment mainFragment;
    List<Movies> videoUrl;
    List<Movies> reviewsList;
    List<Movies> moviesList;
    List<String> url;
    public List<Movies> movieJsonStrParsing(String movieJsonString,String SortingBy) throws Exception {
        JSONObject response = new JSONObject(movieJsonString);
        moviesList = new ArrayList<>();
        response.getInt("page");
        JSONArray result = response.getJSONArray("results");
        for (int i = 0; i < result.length(); i++) {
            JSONObject obj = result.getJSONObject(i);
            Movies movies = new Movies(obj.getInt("id"), obj.getString("poster_path"), obj.getString("overview"),
                    obj.getString("release_date"), obj.getString("title"), obj.getString("vote_average"));
            moviesList.add(movies);
        }
        saveToDatabase(moviesList,SortingBy);
        return moviesList;
    }
    public List<Movies> videoJsonStrParsing (String videoJsonStr,String sortingBy) throws Exception {
        videoUrl = new ArrayList<>();
        url = new ArrayList<>();
        String baseVideoUrl = "https://www.youtube.com/watch?v=";
        JSONObject response = new JSONObject(videoJsonStr);
        JSONArray results = response.getJSONArray("results");
        for (int i= 0; i< results.length(); i++){
           JSONObject obj = results.getJSONObject(i);
            String fullVideoUrl =baseVideoUrl + obj.getString("key");
            url.add(fullVideoUrl);
        }
        Movies moviesTrailer = new Movies(response.getInt("id"),url);
        videoUrl.add(moviesTrailer);
        saveToDatabase(videoUrl,sortingBy);
        return videoUrl;
    }
    public List<Movies> reviewsJsonStrParsing (String reviewJsonStr,String sortingBy) throws Exception {
        reviewsList = new ArrayList<>();
        JSONObject response = new JSONObject(reviewJsonStr);
        JSONArray results = response.getJSONArray("results");
        for (int i=0; i < results.length(); i++){
            JSONObject obj = results.getJSONObject(i);
            String review = obj.getString("author" ) + ": " + obj.getString("content");
            Movies movieReview = new Movies(response.getInt("id"),review);
            reviewsList.add(movieReview);
        }
        saveToDatabase(reviewsList,sortingBy);
        return reviewsList;
    }
    public void saveToDatabase(List<Movies> moviesL,String unitType) {
        Realm realm = Realm.getDefaultInstance();
        if (unitType.equals("popular")) {
                for (int i=0;i<moviesL.size();i++){
                  Movies m = moviesL.get(i);
                final PopMovies movies = new PopMovies();
                //we data get update it set old url which saved before null
                //so i check when updating movies for url and revies if they are exist on old data
                // and set them to new data with same id
                // first check if the movie with that id is exist on db if exist it will save old data from to be null(url & reviews)
                PopMovies pop = realm.where(PopMovies.class).equalTo("id", m.getId()).findFirst();
                    if (pop!=null){
                        //saving url
                        movies.setURL1(pop.getURL1());
                        movies.setURL2(pop.getURL2());
                        //check if movie reviews is not empty if so i save them
                        if (pop.getReviews().size()!= 0){
                            RealmList<UserReviews> userReviewses = new RealmList<>();
                            for (int x=0 ;x<pop.getReviews().size();x++){
                                UserReviews s = pop.getReviews().get(x);
                                userReviewses.add(s);

                            }
                            movies.setReviews(userReviewses);
                        }
                    }
                        movies.setIndex(i);
                        movies.setId(m.getId());
                        movies.setTitle(m.getTitle());
                        movies.setVoteAverage(m.getVoteAverage());
                        movies.setReleaseDate(m.getReleaseDate());
                        movies.setOverView(m.getOverView());
                        movies.setPoster(m.getPoster());

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // This will create a new object in Realm or throw an exception if the
                        // object already exists (same primary key)
                        // realm.copyToRealm(obj);

                        // This will update an existing object with the same primary key
                        // or create a new object if an object with no primary key
                        realm.copyToRealmOrUpdate(movies);
                    }
                });
            }}
        if (unitType.equals("top_rated")) {
            for (int i=0;i<moviesL.size();i++){
                Movies m = moviesL.get(i);
                final TopMovies movies = new TopMovies();
                TopMovies top = realm.where(TopMovies.class).equalTo("id", m.getId()).findFirst();
                if (top!=null){
                    //saving url
                    movies.setUrl1(top.getUrl1());
                    movies.setUrl2(top.getUrl2());
                    //check if movie reviews is not empty if so i save them
                    if (top.getReviews().size()!= 0){
                        RealmList<UserReviews> userReviewses = new RealmList<>();
                        for (int x=0 ;x<top.getReviews().size();x++){
                            UserReviews s = top.getReviews().get(x);
                            userReviewses.add(s);
                        }
                        movies.setReviews(userReviewses);
                    }

                }
                movies.setIndex(i);
                movies.setId(m.getId());
                movies.setTitle(m.getTitle());
                movies.setVoteAverage(m.getVoteAverage());
                movies.setReleaseDate(m.getReleaseDate());
                movies.setOverView(m.getOverView());
                movies.setPoster(m.getPoster());
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // This will create a new object in Realm or throw an exception if the
                        // object already exists (same primary key)
                        // realm.copyToRealm(obj);

                        // This will update an existing object with the same primary key
                        // or create a new object if an object with no primary key
                        realm.copyToRealmOrUpdate(movies);
                    }
                });
            }
        }
         if (unitType.contains("videos")) {
             for (int i = 0; i < moviesL.size(); i++) {
                 Movies m = moviesL.get(i); //Find first item on list
//                 int mMovieId = m.getId();  // get id of selected movie
                 final List<String> url = m.getURL(); // get url list of selected movie
                 // first search for movie id on db and if not found it will return null
                 PopMovies pop = realm.where(PopMovies.class).equalTo("id", m.getId()).findFirst();
                 if (pop != null) {
                     // this mean we found movie id on this db
                     if (url.size() == 1) {
                         // if there is only one item on list we set only one url
                         realm.beginTransaction();
                         pop.setURL1(url.get(0));
                         Log.d("Video url", url.get(0));
                         realm.commitTransaction();
                     }
                     if (url.size() >= 2) {
                         // if there are more than or equal to 2 url we will save only two links
                         realm.beginTransaction();
                         pop.setURL1(url.get(0));
                         pop.setURL2(url.get(1));
                         Log.d("Video url", String.valueOf(pop.getURL1()));
                         Log.d("Video url", String.valueOf(pop.getURL2()));
                         realm.commitTransaction();
                     }
                     return;
                 }
                 TopMovies top = realm.where(TopMovies.class).equalTo("id", m.getId()).findFirst();
                 if (top != null) {
                     // this mean we found movie id on this db
                     if (url.size() == 1) {
                         // if there is only one item on list we set only one url
                         realm.beginTransaction();
                         top.setUrl1(url.get(0));
                         Log.d("Video url", url.get(0));
                         realm.commitTransaction();
                     }
                     if (url.size() >= 2) {
                         // if there are more than or equal to 2 url we will save only two links
                         realm.beginTransaction();
                         top.setUrl1(url.get(0));
                         top.setUrl2(url.get(1));
                         Log.d("Video url", String.valueOf(top.getUrl1()));
                         Log.d("Video url", String.valueOf(top.getUrl2()));
                         realm.commitTransaction();
                     }
                     return;
                 }
             }
         }

        if (unitType.contains("reviews")) {
            for (int i = 0; i < moviesL.size(); i++) {
                Movies m = moviesL.get(i); //Find first item on list
                m.getId();                 //get movie id to set reviews
                m.getReviews();            //reviews we got from jsonString
                //check if the movie id on popular movies db
                final PopMovies pop = realm.where(PopMovies.class).equalTo("id", m.getId()).findFirst();
                if (pop != null){
                    // start storing to db
                    realm.beginTransaction();
                    if (i == 0){pop.setReviews(null);}//delete old data to prevent duplicate
                    //then we create object from UserReview to set the reviews
                    UserReviews userReviews = realm.createObject(UserReviews.class);
                    // set review on UserReviews db
                    userReviews.setReviews(m.getReviews());
                    //add this review to the movie review list
                    pop.getReviews().add(userReviews);
                    // commit db storing
                    realm.commitTransaction();
                }

                //check if the movie id on top rated movies db
                TopMovies top = realm.where(TopMovies.class).equalTo("id", m.getId()).findFirst();
                if (top != null){
                    // start storing to db
                    realm.beginTransaction();
                    if (i == 0){top.setReviews(null);}//delete old data to prevent duplicate
                    //then we create object from UserReview to set the reviews
                    UserReviews userReviews = realm.createObject(UserReviews.class);
                   // userReviews.setIndex(i);
                    // set review on UserReviews db
                    userReviews.setReviews(m.getReviews());
                    //add this review to the movie review list
                    top.getReviews().add(userReviews);
                    // commit db storing
                    realm.commitTransaction();
                }

            }

        }
    }
}
