package com.example.android.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapplication.Data.FavMovies;
import com.example.android.myapplication.Data.Movies;
import com.example.android.myapplication.Data.PopMovies;
import com.example.android.myapplication.Data.TopMovies;
import com.example.android.myapplication.Data.TrailersData;
import com.example.android.myapplication.Data.UserReviews;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Hazem on 11/11/2016.
 */

public class DetailsFragment extends Fragment implements DataReadyInterface {
    private String mTitle, mPoster, mBackDrop, mOverView, mReleaseDate, mVoteAverage;
    private TextView overView, releaseDate, voteRateAverage, title;
    private int mMovieId;
    private boolean isFavorite;
    private CheckBox favButton;
    private ImageView poster;
    private View rootView;
    MyAsyncTask videoTask, reviewsTask;

    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_moviedetails, container, false);
        poster = (ImageView) rootView.findViewById(R.id.image_poster);
        title = (TextView) rootView.findViewById(R.id.text_title);
        overView = (TextView) rootView.findViewById(R.id.text_over_view);
        releaseDate = (TextView) rootView.findViewById(R.id.text_relase_date);
        voteRateAverage = (TextView) rootView.findViewById(R.id.text_vote_average);
        favButton = (CheckBox) rootView.findViewById(R.id.favorite_Button);
        // listener for favorite CheckBox
        favButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //First we read fav database for this movie
                isFavorite = readState();
                if (isFavorite) {
                    isFavorite = false;
                    saveState(isFavorite);
                } else {
                    isFavorite = true;
                    saveState(isFavorite);
                }}
        });
        //reading data from bundel
        Bundle movie = getArguments();
        //getting data from bundle and display it on layout
        DataDisplay(movie);
        //Starting Trailers and Reviews fetching
        TrailerAndReviewsTask();
        return rootView;
    }

    private void TrailerAndReviewsTask() {
        videoTask = new MyAsyncTask();
        reviewsTask = new MyAsyncTask();
        videoTask.setOnListener(this);
        //start executing to get videos trailers  and send the second param to say i requsting trailers videos
        videoTask.execute(mMovieId + "/videos", "videos");
        reviewsTask.setOnListener(this);
        // to start execute and send the second param to say i requsting reviews
        reviewsTask.execute(mMovieId + "/reviews", "reviews");

    }

    private void DataDisplay(Bundle movie) {
        mTitle = movie.getString("title");mOverView = movie.getString("overView");
        mPoster = movie.getString("poster");mBackDrop = movie.getString("back_drop");
        mReleaseDate = movie.getString("releaseDate");mVoteAverage = movie.getString("voteAverage");
        mMovieId = movie.getInt("id");
        //reading state of movie if fav to set favButton checked
        favButton.setChecked(isFavorite = readState());
        //setting the title of activity to name of the movie
        getActivity().setTitle(mTitle);
        //using Picasso lib to load image on ImageView
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w780/" + mBackDrop).placeholder(R.drawable.poster_loading).into(poster);
        title.setText(mTitle);overView.setText(mOverView);releaseDate.setText(mReleaseDate);
        voteRateAverage.setText(mVoteAverage + "/10");
    }

    //to save or delete from favorite databas
    private void saveState(boolean isFavourite) {
        Realm realm = Realm.getDefaultInstance();
        // the movie is favorite and want to delete from db
        if (!isFavourite) {
            //First check if this id is existing on fav db and delete it
            final RealmResults<FavMovies> result2 = realm.where(FavMovies.class)
                    .equalTo("id", mMovieId)
                    .findAll();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // Delete all matches
                    result2.deleteAllFromRealm();
                    Toast.makeText(getActivity(), "Removed From Favorite", Toast.LENGTH_SHORT).show();
                }
            });
        }
        // want to add this movie to favorite
        if (isFavourite) {
            final FavMovies movies = new FavMovies();
            movies.setId(mMovieId); movies.setTitle(mTitle);
            movies.setVoteAverage(mVoteAverage); movies.setReleaseDate(mReleaseDate);
            movies.setOverView(mOverView);movies.setPoster(mPoster);
            movies.setBack_Drop(mBackDrop);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(movies);
                    Toast.makeText(getActivity(), "Added To Favorite", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //this method is to check if this movie is favorite or not
    private boolean readState() {
        Realm realm = Realm.getDefaultInstance();
        //searching for id on db if there is a result mean that this movie is a favorite movie
        RealmResults<FavMovies> result2 = realm.where(FavMovies.class)
                .equalTo("id", mMovieId)
                .findAll();
        if (result2.size() == 0) return false;
        else return true;
    }

    @Override
    public void onFetchProgress() {
    }

    @Override
    public void onFetchFinish() {
    }

    @Override
    public void onMovieDataReady(List<Movies> jsonStr) {
    }

    @Override
    public void onVideoDataReady(List<Movies> result) {
        //getting the container for trailer buttons that will be add in
        LinearLayout buttonContainer = (LinearLayout) rootView.findViewById(R.id.button_container);
        //if the result of from AsynckTask is equal to null we read data saved on db
        if (result == null) {
            Realm realm = Realm.getDefaultInstance();
            //first check for id on popular movie db
            PopMovies pop = realm.where(PopMovies.class).equalTo("id", mMovieId).findFirst();
            if (pop != null) {
                //if trailers equals to (0) no trailers display text
                if (pop.getTrailers().size() == 0) {
                    final TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText("   No Videos Available For This Movie");
                    //add this text to buttonContainer
                    buttonContainer.addView(txt);
                }
                // if there is a result i will display them as a button
                for (int i = 0; i < pop.getTrailers().size(); i++) {
                    //first getting trailers from db
                    final TrailersData s = pop.getTrailers().get(i);
                    final Button bttn = new Button(getActivity());
                    bttn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bttn.setText(s.getName());
                    bttn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s.getUrl())));
                        }
                    });
                    buttonContainer.addView(bttn);
                }
                return;
            }
            //if result not found on popular database then searsh on top_related db
            TopMovies top = realm.where(TopMovies.class).equalTo("id", mMovieId).findFirst();
            if (top != null) {
                //if trailers equals to (0) no trailers display text
                if (top.getTrailers().size() == 0) {
                    TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText("   No Videos Available For This Movie");
                    buttonContainer.addView(txt);
                }
                // if there is a result i will display them as a button
                for (int i = 0; i < top.getTrailers().size(); i++) {
                    final TrailersData s = top.getTrailers().get(i);
                    final Button bttn = new Button(getActivity());
                    bttn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bttn.setText(s.getName());
                    bttn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s.getUrl())));
                        }
                    });
                    buttonContainer.addView(bttn);
                    return;
                }
            }
        }
        //if the result from AsynckTask not null means there is data
        if (result != null) {
            //if trailers equals to (0) no trailers display text
            if (result.size() == 0) {
                TextView txt = new TextView(getActivity());
                txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                txt.setText("  No Videos Available For This Movie");
                buttonContainer.addView(txt);
            }
            //loop on the result and get trailers
            for (int i = 0; i < result.size(); i++) {
                Movies m = result.get(i); //Find first item on list
                final String url = m.getURL();
                String name = m.getUrl_name();
                final Button bttn = new Button(getActivity());
                bttn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                bttn.setText(name);
                bttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                });
                buttonContainer.addView(bttn);
            }
        }
    }

    @Override
    public void onReviewDataReady(List<Movies> result) {
        //getting the container for Reviews text that will be add in
        LinearLayout reviewContainer = (LinearLayout) rootView.findViewById(R.id.reviews_containers);
        //if the result of from AsynckTask is equal to null we read data saved on db
        if (result == null) {
            Realm realm = Realm.getDefaultInstance();
            PopMovies pop = realm.where(PopMovies.class).equalTo("id", mMovieId).findFirst();
            if (pop != null) {
                if (pop.getReviews().size() == 0) {
                    final TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText("No Reviews Available For This Movie");
                    reviewContainer.addView(txt);
                }
                for (int i = 0; i < pop.getReviews().size(); i++) {
                    UserReviews s = pop.getReviews().get(i);
                    final TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText(s.getReviews());
                    reviewContainer.addView(txt);
                }
                return;
            }
            TopMovies top = realm.where(TopMovies.class).equalTo("id", mMovieId).findFirst();
            if (top != null) {
                if (top.getReviews().size() == 0) {
                    final TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText("No Reviews Available For This Movie");
                    reviewContainer.addView(txt);
                }
                for (int i = 0; i < top.getReviews().size(); i++) {
                    UserReviews s = top.getReviews().get(i);
                    final TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText(s.getReviews());
                    reviewContainer.addView(txt);
                }
                return;
            }

        }
        if (result != null) {
            if (result.size() == 0) {
                final TextView txt = new TextView(getActivity());
                txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                txt.setText("No Reviews Available For This Movie");
                reviewContainer.addView(txt);
            }
            for (int i = 0; i < result.size(); i++) {
                Movies m = result.get(i); //Find first item on list
                final TextView txt = new TextView(getActivity());
                txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                txt.setText(m.getReviews());
                reviewContainer.addView(txt);

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //if user hit back or select another movie before AsynckTask Completed i will cancel this task
        videoTask.cancel(true);
        reviewsTask.cancel(true);
    }
}