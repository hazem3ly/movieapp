package com.example.android.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.myapplication.Data.Movies;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import java.util.List;


public class ImageAdapter extends BaseAdapter {
    // Keep all Images in array
    List<Movies> mMovies;
    private Context mContext;
    // Constructor
    public ImageAdapter(Context c, List<Movies> movies) {
        mContext = c;
        mMovies = movies;

    }

    public int getCount() {
        return mMovies.size();
    }

    public Movies getItem(int position) {
        return mMovies.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.image_list_items, null);
        }
        ImageView img = (ImageView) view.findViewById(R.id.image_list_item_imageView);
        TextView title = (TextView) view.findViewById(R.id.text_item);
        final Movies movies = mMovies.get(position);
        title.setText(movies.getTitle());
        new Picasso.Builder(mContext)
                .downloader(new OkHttpDownloader(mContext, Integer.MAX_VALUE));
        Picasso picasso = Picasso.with(mContext);
        picasso.setIndicatorsEnabled(true);
        picasso.load("http://image.tmdb.org/t/p/w185/" + movies.getPoster())
                .into(img);
        img.setContentDescription(movies.getTitle());
        return view;

    }
}