package com.example.android.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapplication.Data.FavMovies;
import com.example.android.myapplication.Data.Movies;
import com.example.android.myapplication.Data.PopMovies;
import com.example.android.myapplication.Data.TopMovies;
import com.example.android.myapplication.Data.UserReviews;
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
    private ImageView poster;
    TextView overView;
    TextView relaseDate;
    TextView voteRateAverage;
    TextView title;
    private ProgressBar spinner;
    private Button youtube1;
    private Button youtube2;
    private TextView reviews;
    private String url1 = "";
    private String url2 = "";
    private String review = "";

    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_moviedetails, container, false);
        poster = (ImageView) rootView.findViewById(R.id.image_poster);
        title = (TextView) rootView.findViewById(R.id.text_title);
        overView = (TextView) rootView.findViewById(R.id.text_over_view);
        relaseDate = (TextView) rootView.findViewById(R.id.text_relase_date);
        voteRateAverage = (TextView) rootView.findViewById(R.id.text_vote_average);
        imgButton = (CheckBox) rootView.findViewById(R.id.favorite_Button);
        youtube1 = (Button) rootView.findViewById(R.id.youtube1);
        youtube2 = (Button) rootView.findViewById(R.id.youtube2);
        reviews = (TextView) rootView.findViewById(R.id.review);
        spinner = (ProgressBar) rootView.findViewById(R.id.Detail_ProgressBar);
        youtube1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url1.contains("https://www.youtube.com/watch?v=")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url1)));
                } else {
                    Toast.makeText(getActivity(), "No Videos Available", Toast.LENGTH_SHORT).show();

                }
            }
        });
        youtube2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url2.contains("https://www.youtube.com/watch?v=")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url1)));
                } else {
                    Toast.makeText(getActivity(), "No Videos Available", Toast.LENGTH_SHORT).show();

                }
            }
        });
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
        Bundle movie = getArguments();
        mTitle = movie.getString("title");
        mOverView = movie.getString("overView");
        mPoster = movie.getString("poster");
        mRealseDate = movie.getString("relaseDate");
        mVoteAverage = movie.getString("voteAverage");
        mMovieId = movie.getInt("id");
        getActivity().setTitle(mTitle);
        imgButton.setChecked(isFavourite = readState());
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500/" + mPoster).into(poster);
        title.setText("Title: " + mTitle);
        overView.setText("Story: " + mOverView);
        relaseDate.setText("Release Date: " + mRealseDate);
        voteRateAverage.setText("Vote Average = " + mVoteAverage);

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
        videoTask.execute(mMovieId + "/videos", "videos");
        Log.d("finished  to parsing ?", "video");
        reviews.setOnListener(this);
        // to start execute and send the second param to say i requsting videos
        Log.d("start to parsing ?", "reviews");
        reviews.execute(mMovieId + "/reviews", "reviews");
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
        if (result == null) {
            Toast.makeText(getActivity(), "No Network Connection", Toast.LENGTH_SHORT).show();
            Realm realm = Realm.getDefaultInstance();
            PopMovies pop = realm.where(PopMovies.class).equalTo("id", mMovieId).findFirst();
            if (pop != null) {
                url1 = String.valueOf(pop.getURL1());
                url2 = String.valueOf(pop.getURL2());
                return;
            }
            TopMovies top = realm.where(TopMovies.class).equalTo("id", mMovieId).findFirst();
            if (top != null) {
                url1 = String.valueOf(top.getUrl1());
                url2 = String.valueOf(top.getUrl2());
                return;
            }
        }
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                Movies m = result.get(i);
                List<String> url = m.getURL();
                if (url.size() == 1) {
                    url1 = url.get(0);
                    url2 = "no video";
                }
                if (url.size() >= 2) {
                    url1 = url.get(0);
                    url2 = url.get(1);
                }
            }
        }
    }

    @Override
    public void onReviewDataReady(List<Movies> result) {
        if (result == null) {
            Realm realm = Realm.getDefaultInstance();
            PopMovies pop = realm.where(PopMovies.class).equalTo("id", mMovieId).findFirst();
            if (pop != null) {
                Log.d("Size of reviews on pop", String.valueOf(pop.getReviews().size()));
                for (int i=0;i < pop.getReviews().size();i++){
                    UserReviews s = pop.getReviews().get(i);
                    review = review + "\n" + s.getReviews();
                    Log.d("review",String.valueOf(s.getReviews()));
                }
                reviews.setText(review);
                return;
            }
            TopMovies top = realm.where(TopMovies.class).equalTo("id", mMovieId).findFirst();
            if (top != null) {
                Log.d("Size of reviews on top", String.valueOf(top.getReviews().size()));
                for (int i=0;i < top.getReviews().size();i++){
                    UserReviews s = top.getReviews().get(i);
                    review = review + "\n" + s.getReviews();
                    Log.d("review",String.valueOf(s.getReviews()));
                }
                reviews.setText(review);
                return;
            }

        }
        if (result != null) {
            Log.d("Size of reviews", String.valueOf(result.size()));
            for (int i = 0; i < result.size(); i++) {
                Movies m = result.get(i); //Find first item on list
                review  = review + "\n\n\n\n" +m.getReviews();            //reviews we got from jsonString
                Log.d("review",String.valueOf(m.getReviews()));

            }
            reviews.setText(review);
        }
    }
}