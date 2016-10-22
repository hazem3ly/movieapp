package com.example.android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Details_Movie_Activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details__movie_);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private String mTitle;
        private String mPoster;
        private String mOverView;
        private String mRealseDate;


        public PlaceholderFragment() { setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_details__movie_, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("title")){

               mTitle = intent.getStringExtra("title");
                mOverView = intent.getStringExtra("overView");
                mPoster = intent.getStringExtra("poster");
                mRealseDate = intent.getStringExtra("relaseDate");
                ImageView poster = (ImageView)rootView.findViewById(R.id.image_poster) ;
                TextView title = (TextView) rootView.findViewById(R.id.text_title);
                TextView overView = (TextView) rootView.findViewById(R.id.text_over_view);
                TextView relaseDate = (TextView) rootView.findViewById(R.id.text_relase_date);
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/"+mPoster).into(poster);
                title.setText(mTitle);overView.setText(mOverView);relaseDate.setText(mRealseDate);
            }
            return rootView;
        }
    }
}
