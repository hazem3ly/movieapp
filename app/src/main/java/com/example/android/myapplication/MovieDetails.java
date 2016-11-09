package com.example.android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapplication.Data.FavMovies;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmResults;

public class MovieDetails extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moviedetail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details__movie_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent setting = new Intent(this, Settings_Activity.class);
            startActivity(setting);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements DataReadyInterface {
        private String mTitle;
        private String mPoster;
        private String mOverView;
        private String mRealseDate;
        private String mVoteAverage;
        private int mMovieId;
        private boolean isFavourite;
        String whattoparse;
        private boolean isVideo;
        private CheckBox imgButton;
        private ProgressBar spinner;
        public PlaceholderFragment() {
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
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + mPoster).into(poster);
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
//            SharedPreferences aSharedPreferences = this.getSharedPreferences(
//                    "Favourite", Context.MODE_PRIVATE);
//            SharedPreferences.Editor aSharedPreferencesEdit = aSharedPreferences
//                    .edit();
//            aSharedPreferencesEdit.putBoolean("State", isFavourite);
//            aSharedPreferencesEdit.commit();
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
//            SharedPreferences aSharedPreferences = this.getSharedPreferences(
//                    "Favourite", Context.MODE_PRIVATE);
//            return aSharedPreferences.getBoolean("State", true);
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
        public void onMovieDataReady(String jsonStr) {

        }

        @Override
        public void onVideoDataReady(String jsonStr) {
            Toast.makeText(getActivity(), jsonStr, Toast.LENGTH_LONG).show();
            Parser parser = new Parser();
            Log.d("video json is :--->",jsonStr );

            Log.d("now iam parsing","videos" );
            try {
                parser.videoJsonStrParsing(jsonStr);
            } catch (Exception e) {
                Log.e("error parsing", e.getMessage(), e);
                e.printStackTrace();
            }
        }

        @Override
        public void onReviewDataReady(String jsonStr) {
            Parser parser = new Parser();
            Log.d("review json is :--->",jsonStr );

            Log.d("now iam parsing", "reviews");
            try {
                parser.reviewsJsonStrParsing(jsonStr);
            } catch (Exception e) {
                Log.e("error parsing", e.getMessage(), e);
                e.printStackTrace();
            }

        }
    }
}
