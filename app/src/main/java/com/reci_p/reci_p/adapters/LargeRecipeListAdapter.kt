package com.reci_p.reci_p.adapters

/**
 * Created by Laura on 11/17/17.
 */

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.ramotion.foldingcell.FoldingCell
import com.reci_p.reci_p.R
import com.reci_p.reci_p.activities.EditorActivity
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.helpers.DataManager
import java.util.*

class LargeRecipeListAdapter(val recipeList: List<Recipe>?, val onSelect: (recipe: Recipe) -> Unit) : RecyclerView.Adapter<LargeRecipeListAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LargeRecipeListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_flip_large, parent, false)
        return CustomViewHolder(view)
    }

    class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var image = view.findViewById<SimpleDraweeView>(R.id.imageView)
        var recipeTitle = view.findViewById<TextView>(R.id.recipeLarge_title)
        var recipeAuthor = view.findViewById<TextView>(R.id.recipeLarge_author)
        var recipeTime = view.findViewById<TextView>(R.id.recipeLarge_time)

        var image_unfold = view.findViewById<SimpleDraweeView>(R.id.imageView_unfold)
        var recipeTitleUnfold = view.findViewById<TextView>(R.id.recipeLarge_title_unfold)
        var recipeAuthorUnfold = view.findViewById<TextView>(R.id.recipeLarge_author_unfold)
        var ratingUnfold = view.findViewById<RatingBar>(R.id.ratingBar_unfold)
        var cookTimeUnfold = view.findViewById<TextView>(R.id.recipeLarge_cook_time_unfold)
        var prepTimeUnfold = view.findViewById<TextView>(R.id.recipeLarge_prep_time_unfold)
        var ingredientHolderUnfold = view.findViewById<LinearLayout>(R.id.recipeLarge_ingredients_holder_unfold)
        var instructionHolderUnfold = view.findViewById<LinearLayout>(R.id.recipeLarge_instructions_holder_unfold)
        var saveBtnUnfold = view.findViewById<Button>(R.id.recipeLarge_btn_save)
        var editBtnUnfold = view.findViewById<Button>(R.id.recipeLarge_btn_edit)
        var recipeDescUnfold = view.findViewById<TextView>(R.id.recipeLarge_description_unfold)
    }

    override fun onBindViewHolder(holder: LargeRecipeListAdapter.CustomViewHolder, position: Int) {
        //get recipe
        val recipe = recipeList!![position]

        //set values in view
        //first get the recipe image from Firebase

        if (recipe.photo != "") {
            val urlString = "gs://${FirebaseApp.getInstance()!!.options!!.storageBucket}/${recipe.photo}"
            FirebaseStorage.getInstance().getReferenceFromUrl(urlString).downloadUrl.addOnSuccessListener { uri ->
                holder.image.setImageURI(uri)
                holder.image_unfold.setImageURI(uri)
            }.addOnFailureListener { exception ->
                Log.e("Reci-P", "Error getting URI for image: ${exception.localizedMessage}")
            }
        }

        //then set the TextViews
        holder.recipeTitle.text = recipe.title
        holder.recipeTitleUnfold.text = recipe.title
        holder.recipeDescUnfold.text = recipe.description
        DataManager.getUser(recipe.creator, save = true) { owner ->
            if (owner != null) {
                holder.recipeAuthor.text = owner.displayName
                holder.recipeAuthorUnfold.text = owner.displayName
            } else {
                holder.recipeAuthor.text = ""
                holder.recipeAuthorUnfold.text = ""
            }
        }
        holder.view.setOnClickListener {
            holder.view.findViewById<FoldingCell>(R.id.recipe_folding_cell).toggle(false)
        }
        holder.recipeTime.text = recipe.cookTime

        holder.ratingUnfold.rating = recipe.rating
        holder.cookTimeUnfold.text = recipe.cookTime
        holder.prepTimeUnfold.text = recipe.prepTime

        for (i in recipe.ingredients) {
            val view = LayoutInflater.from(holder.view.context).inflate(R.layout.ingredient, null)
            view.findViewById<TextView>(R.id.ingredient_text).text = i
            holder.ingredientHolderUnfold.addView(view)
        }

        for (i in recipe.instructions) {
            val view = LayoutInflater.from(holder.view.context).inflate(R.layout.instruction, null)
            view.findViewById<TextView>(R.id.instruction_text).text = i
            holder.instructionHolderUnfold.addView(view)
        }

        if (recipe.owner == FirebaseAuth.getInstance().currentUser!!.uid) {
            holder.saveBtnUnfold.visibility = View.GONE
        }

        holder.saveBtnUnfold.setOnClickListener {
            val newRecipe = Recipe(FirebaseAuth.getInstance().currentUser!!.uid, UUID.randomUUID().toString(), recipe)
            DataManager.createRecipe(newRecipe, {
                holder.saveBtnUnfold.text = "Saved"
            })

        }

        holder.editBtnUnfold.setOnClickListener {
            onSelect(recipe)
        }

    }

    override fun getItemCount(): Int {
        return recipeList?.size ?: 0
    }
}
