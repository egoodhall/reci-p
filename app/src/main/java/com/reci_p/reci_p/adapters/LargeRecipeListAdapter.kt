package com.reci_p.reci_p.adapters

/**
 * Created by Laura on 11/17/17.
 */

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.reci_p.reci_p.R
import com.reci_p.reci_p.data.Recipe

import com.google.firebase.storage.FirebaseStorage
import com.facebook.drawee.view.SimpleDraweeView

class LargeRecipeListAdapter(val mContext: Context, val recipeList: List<Recipe>?) : RecyclerView.Adapter<LargeRecipeListAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LargeRecipeListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_large, null)
        //View view = view.findViewById(R.id.recyclerviewRecipes_recyclerview);
        return CustomViewHolder(view)
    }

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: SimpleDraweeView
        var recipeTitle: TextView
        var recipeAuthor: TextView
        var recipeTime: TextView
        protected var description: TextView
        protected var authorLabel: TextView
        protected var descriptionLabel: TextView
        protected var timeLabel: TextView

        init {
            this.image = view.findViewById<View>(R.id.imageView) as SimpleDraweeView
            this.recipeTitle = view.findViewById<View>(R.id.textView2) as TextView
            this.recipeAuthor = view.findViewById<View>(R.id.textView3) as TextView
            this.recipeTime = view.findViewById<View>(R.id.textView5) as TextView
            this.description = view.findViewById<View>(R.id.textView6) as TextView
            this.authorLabel = view.findViewById<View>(R.id.textView7) as TextView
            this.descriptionLabel = view.findViewById<View>(R.id.textView8) as TextView
            this.timeLabel = view.findViewById<View>(R.id.textView9) as TextView
        }
    }

    override fun onBindViewHolder(holder: LargeRecipeListAdapter.CustomViewHolder, position: Int) {
        //get recipe
        val recipe = recipeList!![position]

        //set values in view
        //first get the recipe image from Firebase
        val urlString = "gs://bucket/" + recipe.photo
        holder.image.setImageURI(FirebaseStorage.getInstance().getReferenceFromUrl(urlString).downloadUrl.result)

        //then set the TextViews
        holder.recipeTitle.text = recipe.title
        holder.recipeAuthor.text = recipe.owner
        holder.recipeTime.text = recipe.cookTime
    }

    override fun getItemCount(): Int {
        return recipeList?.size ?: 0
    }
}
