package com.reci_p.reci_p.fragments;

/**
 * Created by Laura on 11/17/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reci_p.reci_p.R;
import com.reci_p.reci_p.data.Recipe;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.CustomViewHolder> {

    private List<Recipe> feedItemList;
    private Context mContext;


    @Override
    public Adapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView recipeTitle;
        protected TextView recipeAuthor;
        protected TextView recipeTime;
        protected TextView description;

        public CustomViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.imageView);
            this.recipeTitle = (TextView) view.findViewById(R.id.textView2);
            this.recipeAuthor = (TextView) view.findViewById(R.id.textView3);
            this.recipeTime = (TextView) view.findViewById(R.id.textView5);
            this.description = (TextView) view.findViewById(R.id.textView6);
        }
    }

    @Override
    public void onBindViewHolder(Adapter.CustomViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
