package com.reci_p.reci_p.adapters

/**
 * Created by Laura on 11/17/17.
 */

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.reci_p.reci_p.R
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.helpers.DataManager

class LargeRecipeListAdapter(val recipeList: List<Recipe>?, onSelect: (recipe: Recipe) -> Unit) : RecyclerView.Adapter<LargeRecipeListAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LargeRecipeListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_large, parent, false)
        return CustomViewHolder(view)
    }

    class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var image = view.findViewById<SimpleDraweeView>(R.id.imageView)
        var recipeTitle = view.findViewById<TextView>(R.id.recipeLarge_title)
        var recipeAuthor = view.findViewById<TextView>(R.id.recipeLarge_author)
        var recipeTime = view.findViewById<TextView>(R.id.recipeLarge_time)
    }

    override fun onBindViewHolder(holder: LargeRecipeListAdapter.CustomViewHolder, position: Int) {
        //get recipe
        val recipe = recipeList!![position]

        //set values in view
        //first get the recipe image from Firebase

        if (recipe.photo != "") {
            Log.d("Reci-P", "HERE ${recipe.photo}")
            val urlString = "gs://${FirebaseApp.getInstance()!!.options!!.storageBucket}/${recipe.photo}"
            FirebaseStorage.getInstance().getReferenceFromUrl(urlString).downloadUrl.addOnSuccessListener { uri ->
                Log.d("Reci-P", "URI: ${uri.toString()}")
                val controller = Fresco.newDraweeControllerBuilder().setUri(uri)
                holder.image.controller = controller.setOldController(holder.image.controller).build()
            }.addOnFailureListener { exception ->
                Log.e("Reci-P", "Error getting URI for image: ${exception.localizedMessage}")
            }
        }

        //then set the TextViews
        holder.recipeTitle.text = recipe.title
        Log.d("Reci-P", "${recipe.owner}")
        DataManager.getUser(recipe.owner, save = true) { owner ->
            if (owner != null) {
                holder.recipeAuthor.text = owner.displayName
            } else {
                holder.recipeAuthor.text = ""
            }
        }
        holder.recipeTime.text = recipe.cookTime
    }

    override fun getItemCount(): Int {
        return recipeList?.size ?: 0
    }
}
