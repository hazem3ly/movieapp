package com.example.android.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ImageAdapter imageAdapter = new ImageAdapter(getActivity());
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(getActivity()));
        return rootView;
    }

    private class ImageAdapter extends BaseAdapter {
        // Keep all Images in array
        public Integer[] mThumbIds = {
                R.drawable.tst, R.drawable.tst,
                R.drawable.tst, R.drawable.tst,
                R.drawable.tst, R.drawable.tst,
                R.drawable.tst, R.drawable.tst,
                R.drawable.tst, R.drawable.tst,
                R.drawable.tst, R.drawable.tst,
                R.drawable.tst, R.drawable.tst,
                R.drawable.tst, R.drawable.tst,
                R.drawable.tst, R.drawable.tst,
                R.drawable.tst, R.drawable.tst,
        };
        private Context mContext;

        // Constructor
        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View imageView = convertView;
            if (imageView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                imageView = inflater.inflate(R.layout.image_list_items, null);
                ImageView img = (ImageView) imageView.findViewById(R.id.image_list_item_imageView);
                img.setImageResource(mThumbIds[position]);
            } else {
                imageView = (View) convertView;
            }
            return imageView;
        }

    }
}

