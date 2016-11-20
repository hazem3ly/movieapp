package com.example.android.myapplication;

import com.example.android.myapplication.Data.Movies;
import com.example.android.myapplication.Data.PopMovies;
import com.example.android.myapplication.Data.TopMovies;
import com.example.android.myapplication.Data.TrailersData;
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
                    obj.getString("release_date"), obj.getString("title"), obj.getString("vote_average"),obj.getString("backdrop_path"));
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
            Movies moviesTrailer = new Movies(response.getInt("id"),fullVideoUrl,obj.getString("name"));
            videoUrl.add(moviesTrailer);
        }
        saveToDatabase(videoUrl,sortingBy);
        return videoUrl;
    }
    public List<Movies> reviewsJsonStrParsing (String reviewJsonStr,String sortingBy) throws Exception {
        reviewsList = new ArrayList<>();
        JSONObject response = new JSONObject(reviewJsonStr);
        JSONArray results = response.getJSONArray("results");
        for (int i=0; i < results.length(); i++){
            JSONObject obj = results.getJSONObject(i);
            String review = obj.getString("author" ) + ":-\n\t\t\t\t\t " + obj.getString("content");
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
                        if (pop.getTrailers().size()!= 0){
                            RealmList<TrailersData> trailersData = new RealmList<>();
                            for (int c=0 ;c<pop.getTrailers().size();c++){
                                TrailersData t = pop.getTrailers().get(c);
                                trailersData.add(t);
                            }
                            movies.setTrailers(trailersData);
                        }
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
                        movies.setBackdrop_path(m.getBackdrop());
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
                    if (top.getTrailers().size()!= 0){
                        RealmList<TrailersData> trailersData = new RealmList<>();
                        for (int c=0 ;c<top.getTrailers().size();c++){
                            TrailersData t = top.getTrailers().get(c);
                            trailersData.add(t);
                        }
                        movies.setTrailers(trailersData);
                    }
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
                movies.setBackdrop_path(m.getBackdrop());
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
                 // first search for movie id on db and if not found it will return null
                 PopMovies pop = realm.where(PopMovies.class).equalTo("id", m.getId()).findFirst();
                 if (pop != null) {
                     // this mean we found movie id on this db
                     realm.beginTransaction();
                     if (i == 0){pop.setTrailers(null);}//delete old data to prevent duplicate
                     TrailersData trailersData = realm.createObject(TrailersData.class);
                            trailersData.setUrl(m.getURL());
                            trailersData.setName(m.getUrl_name());
                     pop.getTrailers().add(trailersData);
                     realm.commitTransaction();
                 }
                 TopMovies top = realm.where(TopMovies.class).equalTo("id", m.getId()).findFirst();
                 if (top != null) {
                     // this mean we found movie id on this db
                     realm.beginTransaction();
                     if (i == 0){top.setTrailers(null);}//delete old data to prevent duplicate
                     //then we create object from TrailersData to set the trailer
                     TrailersData trailersData = realm.createObject(TrailersData.class);
                            trailersData.setUrl(m.getURL());
                            trailersData.setName(m.getUrl_name());
                     //add this trailer to the movie trailer list
                     top.getTrailers().add(trailersData);
                     // commit db storing
                     realm.commitTransaction();
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
