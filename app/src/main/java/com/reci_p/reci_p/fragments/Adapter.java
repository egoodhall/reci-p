package com.reci_p.reci_p.fragments;

/**
 * Created by Laura on 11/17/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reci_p.reci_p.R;
import com.reci_p.reci_p.data.Recipe;
import java.util.List;

import com.google.firebase.storage.FirebaseStorage;

public class Adapter extends RecyclerView.Adapter<Adapter.CustomViewHolder> {

    private List<Recipe> recipeList;
    private Context mContext;

    public Adapter(Context context, List<Recipe> myrecipeList) {
        this.recipeList = myrecipeList;
        this.mContext = context;
    }

    @Override
    public Adapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_large, null);
        //View view1 = view.findViewById(R.id.recyclerviewRecipes_recyclerview);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView recipeTitle;
        protected TextView recipeAuthor;
        protected TextView recipeTime;
        protected TextView description;
        protected TextView authorLabel;
        protected TextView descriptionLabel;
        protected TextView timeLabel;

        public CustomViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.imageView);
            this.recipeTitle = (TextView) view.findViewById(R.id.textView2);
            this.recipeAuthor = (TextView) view.findViewById(R.id.textView3);
            this.recipeTime = (TextView) view.findViewById(R.id.textView5);
            this.description = (TextView) view.findViewById(R.id.textView6);
            this.authorLabel = (TextView) view.findViewById(R.id.textView7);
            this.descriptionLabel = (TextView) view.findViewById(R.id.textView8);
            this.timeLabel = (TextView) view.findViewById(R.id.textView9);
        }
    }

    @Override
    public void onBindViewHolder(Adapter.CustomViewHolder holder, int position) {
        //get recipe
        Recipe recipe = recipeList.get(position);

        //set values in view
        //first get the recipe image from Firebase
        String urlString = "gs://bucket/" + recipe.getPhoto();
        holder.image.setImageURI(FirebaseStorage.getInstance().getReferenceFromUrl(urlString).getDownloadUrl().getResult());

        //then set the TextViews
        holder.recipeTitle.setText(recipe.getTitle());
        holder.recipeAuthor.setText(recipe.getOwner());
        holder.recipeTime.setText(recipe.getCookTime());
    }

    @Override
    public int getItemCount() {
        if (recipeList == null) {
            return 0;
        }
        else {
            return recipeList.size();
        }
    }
}
