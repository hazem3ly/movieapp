package com.example.android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapplication.Data.FavMovies;
import com.example.android.myapplication.Data.Movies;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Hazem on 11/11/2016.
 */

public  class DetailsFragment extends Fragment implements DataReadyInterface {
    private String mTitle;
    private String mPoster;
    private String mOverView;
    private String mRealseDate;
    private String mVoteAverage;
    private int mMovieId;
    private boolean isFavourite;
    private CheckBox imgButton;
    private ProgressBar spinner;
    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_moviedetails, container, false);
        imgButton = (CheckBox) rootView.findViewById(R.id.favorite_Button);

        spinner = (ProgressBar) rootView.findViewById(R.id.Detail_ProgressBar);

        imgButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isFavourite = readState();
                if (isFavourite) {
                    //imgButton.setBackgroundResource(R.drawable.ic_star_rate_black_18dp);
                    isFavourite = false;
                    saveState(isFavourite);
                } else {
                    //  imgButton.setBackgroundResource(R.drawable.tst);
                    isFavourite = true;
                    saveState(isFavourite);
                }
            }
        });
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("title")) {
            mTitle = intent.getStringExtra("title");
            mOverView = intent.getStringExtra("overView");
            mPoster = intent.getStringExtra("poster");
            mRealseDate = intent.getStringExtra("relaseDate");
            mVoteAverage = intent.getStringExtra("voteAverage");
            mMovieId = Integer.parseInt(intent.getStringExtra("id"));
            getActivity().setTitle(mTitle);
            imgButton.setChecked(isFavourite = readState());
            ImageView poster = (ImageView) rootView.findViewById(R.id.image_poster);
            TextView title = (TextView) rootView.findViewById(R.id.text_title);
            TextView overView = (TextView) rootView.findViewById(R.id.text_over_view);
            TextView relaseDate = (TextView) rootView.findViewById(R.id.text_relase_date);
            TextView voteRateAverage = (TextView) rootView.findViewById(R.id.text_vote_average);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500/" + mPoster).into(poster);
            title.setText("Title: " + mTitle);
            overView.setText("Story: " + mOverView);
            relaseDate.setText("Release Date: " + mRealseDate);
            voteRateAverage.setText("Vote Average = " + mVoteAverage);


        }
        MyAsyncTask videoTask = new MyAsyncTask();
        MyAsyncTask reviews = new MyAsyncTask();
        videoTask.setOnListener(this);
        Log.d("start to parsing ?", "video");
        videoTask.execute(mMovieId+"/videos","videos");
        Log.d("finished  to parsing ?", "video");
        reviews.setOnListener(this);
        // to start execute and send the second param to say i requsting videos
        Log.d("start to parsing ?", "reviews");
        reviews.execute(mMovieId+"/reviews","reviews");
        Log.d("finished  to parsing ?", "reviews");


        return rootView;
    }

    private void saveState(boolean isFavourite) {
        Realm realm = Realm.getDefaultInstance();
        if (!isFavourite) {

            final RealmResults<FavMovies> result2 = realm.where(FavMovies.class)
                    .equalTo("id", mMovieId)
                    .findAll();
            Log.d("favorite?", String.valueOf(result2.size()));
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // Delete all matches
                    result2.deleteAllFromRealm();
                    Toast.makeText(getActivity(), "Removed From Favorite", Toast.LENGTH_SHORT).show();


                }
            });
        }
        if (isFavourite) {
            final FavMovies movies = new FavMovies();
            movies.setId(mMovieId);
            movies.setTitle(mTitle);
            movies.setVoteAverage(mVoteAverage);
            movies.setReleaseDate(mRealseDate);
            movies.setOverView(mOverView);
            movies.setPoster(mPoster);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // This will create a new object in Realm or throw an exception if the
                    // object already exists (same primary key)
                    // realm.copyToRealm(obj);

                    // This will update an existing object with the same primary key
                    // or create a new object if an object with no primary key
                    realm.copyToRealmOrUpdate(movies);
                    Toast.makeText(getActivity(), "Added To Favorite", Toast.LENGTH_SHORT).show();


                }
            });
        }
    }

    private boolean readState() {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<FavMovies> result2 = realm.where(FavMovies.class)
                .equalTo("id", mMovieId)
                .findAll();
        Log.d("no of favorit movie is?", String.valueOf(result2.size()));
        Log.d("id offavorit movie is?", String.valueOf(mMovieId));

        if (result2.size() == 0) return false;
        else return true;

    }


    @Override
    public void onFetchProgress() {
        spinner.setVisibility(View.VISIBLE);

    }

    @Override
    public void onFetchFinish() {
        spinner.setVisibility(View.GONE);

    }

    @Override
    public void onMovieDataReady(List<Movies> jsonStr) {

    }

    @Override
    public void onVideoDataReady(List<Movies> result) {
        if (result==null){
//            Realm realm = Realm.getDefaultInstance();
//                           PopMovies poop = realm.where(PopMovies.class).equalTo("id", mMovieId).findFirst();
//                           Log.d("Video url", String.valueOf(poop.getURL1()));
//                           Log.d("Video url", String.valueOf(poop.getURL2()));
//                Toast.makeText(getActivity(), "No Network Connection", Toast.LENGTH_SHORT).show();
        }
        if (result != null){

        }
    }

    @Override
    public void onReviewDataReady(List<Movies> result) {
        if (result == null) {


//            List<String> reviewContent = new ArrayList<>();
//            Realm realm = Realm.getDefaultInstance();
//            RealmResults<UserReviews> UserReviewResult = realm.where(UserReviews.class).findAll();
//            Log.d("Size of top",String.valueOf(UserReviewResult.size()));
//            for (UserReviews reviews : UserReviewResult){
//                Log.d("Reviews",reviews.getReviews());
//                reviewContent.add(reviews.getReviews());
//            }
//            Log.d("Size of list reviews",String.valueOf(reviewContent.size()));
//            return;
        }
        if (result != null) {
            for (Movies m : result) {
//            Realm realm = Realm.getDefaultInstance();
//            final UserReviews movies = new UserReviews();
//            realm.beginTransaction();
//            movies.setId(m.getId());
//            movies.setReviews(m.getReviews());
//            Log.d("Reviews",m.getReviews());
//            realm.commitTransaction();
////                movies.setTitle(m.getTitle());
////                movies.setVoteAverage(m.getVoteAverage());
////                movies.setReleaseDate(m.getReleaseDate());
////                movies.setOverView(m.getOverView());
////                movies.setPoster(m.getPoster());


//        }
//        for (int i = 0; i<result.size();i++){
//            Movies m = result.get(i);
//            Log.d("Auther", m.getReviewAuthor());
//            Log.d("Content",m.getReviews());
//        }

            }
        }
    }
}
