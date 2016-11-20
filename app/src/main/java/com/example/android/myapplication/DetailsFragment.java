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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public  class DetailsFragment extends Fragment implements DataReadyInterface {
    private String mTitle;
    private String mPoster;
    private String mBackDrop;
    private String mOverView;
    private String mRealseDate;
    private String mVoteAverage;
    private int mMovieId;
    private boolean isFavourite;
    private CheckBox imgButton;
    private ImageView poster;
    private ImageView imageTitle;
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
    View rootView;
    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_moviedetails, container, false);
        poster = (ImageView) rootView.findViewById(R.id.image_poster);
        imageTitle = (ImageView) rootView.findViewById(R.id.image_title);
        title = (TextView) rootView.findViewById(R.id.text_title);
        overView = (TextView) rootView.findViewById(R.id.text_over_view);
        relaseDate = (TextView) rootView.findViewById(R.id.text_relase_date);
        voteRateAverage = (TextView) rootView.findViewById(R.id.text_vote_average);
        imgButton = (CheckBox) rootView.findViewById(R.id.favorite_Button);
//        youtube1 = (Button) rootView.findViewById(R.id.youtube1);
//        youtube2 = (Button) rootView.findViewById(R.id.youtube2);
 //       reviews = (TextView) rootView.findViewById(R.id.review);
       // spinner = (ProgressBar) rootView.findViewById(R.id.Detail_ProgressBar);
//        youtube1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (url1.contains("https://www.youtube.com/watch?v=")) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url1)));
//                } else {
//                    Toast.makeText(getActivity(), "No Videos Available", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//        youtube2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (url2.contains("https://www.youtube.com/watch?v=")) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url1)));
//                } else {
//                    Toast.makeText(getActivity(), "No Videos Available", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
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
            mBackDrop = movie.getString("back_drop");
            mRealseDate = movie.getString("relaseDate");
            mVoteAverage = movie.getString("voteAverage");
            mMovieId = movie.getInt("id");
            getActivity().setTitle(mTitle);
            imgButton.setChecked(isFavourite = readState());
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w780/" + mBackDrop).placeholder(R.drawable.poster_loading).into(poster);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w300/" + mPoster).placeholder(R.drawable.title_loading).into(imageTitle);
            title.setText(mTitle);
            overView.setText(mOverView);
            relaseDate.setText(mRealseDate);
            voteRateAverage.setText(mVoteAverage + "/10");

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
            movies.setBack_Drop(mBackDrop);
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
        //spinner.setVisibility(View.VISIBLE);

    }

    @Override
    public void onFetchFinish() {
       // spinner.setVisibility(View.GONE);

    }

    @Override
    public void onMovieDataReady(List<Movies> jsonStr) {

    }

    @Override
    public void onVideoDataReady(List<Movies> result) {
        LinearLayout buttonContainer = (LinearLayout) rootView.findViewById(R.id.button_container);
        if (result == null) {
            Toast.makeText(getActivity(), "No Network Connection", Toast.LENGTH_SHORT).show();
            Realm realm = Realm.getDefaultInstance();
            PopMovies pop = realm.where(PopMovies.class).equalTo("id", mMovieId).findFirst();
            if (pop != null) {
                Log.d("Size of videos on pop", String.valueOf(pop.getReviews().size()));
                if (pop.getTrailers().size() == 0) {
                   final TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText("   No Videos Available For This Movie");
                    txt.setId(0);
                    buttonContainer.addView(txt);
                }
                for (int i = 0; i < pop.getTrailers().size(); i++) {
                    final TrailersData s = pop.getTrailers().get(i);
                   final Button bttn = new Button(getActivity());
                    bttn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bttn.setText(s.getName());
                    bttn.setId(i);
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
            TopMovies top = realm.where(TopMovies.class).equalTo("id", mMovieId).findFirst();
            if (top != null) {
                Log.d("Size of videos on top", String.valueOf(top.getReviews().size()));
                if (top.getTrailers().size() == 0) {
                    TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText("   No Videos Available For This Movie");
                    buttonContainer.addView(txt);
                }
                for (int i = 0; i < top.getTrailers().size(); i++) {
                    final TrailersData s = top.getTrailers().get(i);
                   final Button bttn = new Button(getActivity());
                    bttn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bttn.setText(s.getName());
                    bttn.setId(i);
                    bttn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s.getUrl())));
                        }
                    });
                    buttonContainer.addView(bttn);
                    return;
                }
            }}
            if (result != null) {
                Log.d("Size of trailers", String.valueOf(result.size()));
                if (result.size() == 0) {
                    TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText("  No Videos Available For This Movie");
                    buttonContainer.addView(txt);
                }
                for (int i = 0; i < result.size(); i++) {
                    Movies m = result.get(i); //Find first item on list
                    final String url = m.getURL();
                    String name = m.getUrl_name();
                    final Button bttn = new Button(getActivity());
                    bttn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    bttn.setText(name);
                    bttn.setId(i);
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
        LinearLayout reviewContainer = (LinearLayout) rootView.findViewById(R.id.reviews_containers);
        if (result == null) {
            Realm realm = Realm.getDefaultInstance();
            PopMovies pop = realm.where(PopMovies.class).equalTo("id", mMovieId).findFirst();
            if (pop != null) {
                Log.d("Size of reviews on pop", String.valueOf(pop.getReviews().size()));
                if (pop.getReviews().size() == 0){
                    TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText("No Reviews Available For This Movie");
                    reviewContainer.addView(txt);
                }
                for (int i=0;i < pop.getReviews().size();i++){
                    UserReviews s = pop.getReviews().get(i);
                    TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText(s.getReviews());
                    reviewContainer.addView(txt);
                    Log.d("review",String.valueOf(s.getReviews()));
                }
                return;
            }
            TopMovies top = realm.where(TopMovies.class).equalTo("id", mMovieId).findFirst();
            if (top != null) {
                Log.d("Size of reviews on top", String.valueOf(top.getReviews().size()));
                if (top.getReviews().size() == 0){
                    TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText("No Reviews Available For This Movie");
                    reviewContainer.addView(txt);
                }
                for (int i=0;i < top.getReviews().size();i++){
                    UserReviews s = top.getReviews().get(i);
                    TextView txt = new TextView(getActivity());
                    txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    txt.setText(s.getReviews());
                    reviewContainer.addView(txt);
                    Log.d("review",String.valueOf(s.getReviews()));
                }
                return;
            }

        }
        if (result != null) {
            Log.d("Size of reviews", String.valueOf(result.size()));
            if (result.size() == 0){
                TextView txt = new TextView(getActivity());
                txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                txt.setText("No Reviews Available For This Movie");
                reviewContainer.addView(txt);
            }
            for (int i = 0; i < result.size(); i++) {
                Movies m = result.get(i); //Find first item on list
                TextView txt = new TextView(getActivity());
                txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                txt.setText(m.getReviews());
                reviewContainer.addView(txt);
                //reviews we got from jsonString
                Log.d("review",String.valueOf(m.getReviews()));

            }
        }
    }
}