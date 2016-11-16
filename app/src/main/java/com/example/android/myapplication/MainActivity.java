package com.example.android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieSelectInterface  {
    boolean dualPane = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainFragment mMainFragment = new MainFragment();
        mMainFragment.setMovieSelectInterface(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.flMain,mMainFragment,"").commit();
        if (null != findViewById(R.id.flDetails)){
            dualPane = true;
        }
      }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent setting = new Intent(this,Settings_Activity.class);
            startActivity(setting);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setSelectedMovie(Bundle movie) {
        if (!dualPane){
            Intent details = new Intent(this, DetailsActivity.class)
                        .putExtra("title", movie.getString("title")).putExtra("relaseDate", movie.getString("relaseDate"))
                        .putExtra("poster", movie.getString("poster")).putExtra("overView", movie.getString("overView"))
                        .putExtra("id", String.valueOf(movie.getInt("id"))).putExtra("voteAverage", movie.getString("voteAverage"));
                startActivity(details);
        } else {
            DetailsFragment mDetailsFragment = new DetailsFragment();
            mDetailsFragment.setArguments(movie);
            getSupportFragmentManager().beginTransaction().replace(R.id.flDetails,mDetailsFragment,"").commit();
        }
    }
}
