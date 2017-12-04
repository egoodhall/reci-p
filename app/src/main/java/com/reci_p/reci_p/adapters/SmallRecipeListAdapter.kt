package com.reci_p.reci_p.adapters

import android.content.Context
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
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.helpers.DataManager
import java.util.*

/**
 *
 *
 * Created by Eric Marshall on 11/27/17
 */
class SmallRecipeListAdapter(val data: MutableList<Recipe>,
                           val onClickEdit : (Recipe) -> Unit
) : RecyclerView.Adapter<SmallRecipeListAdapter.RecipeViewHolder>() {

    var ctx: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecipeViewHolder {
        if (ctx == null) ctx = parent?.context
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.recipe_flip_small, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = data!![position]

        holder.img.setImageURI("")
        if (recipe.photo != "") {
            Log.d("Reci-P", "HERE ${recipe.photo}")
            val urlString = "gs://${FirebaseApp.getInstance()!!.options!!.storageBucket}/${recipe.photo}"
            FirebaseStorage.getInstance().getReferenceFromUrl(urlString).downloadUrl.addOnSuccessListener { uri ->
                Log.d("Reci-P", "URI: ${uri.toString()}")
                holder.img.setImageURI(uri)
                holder.image_unfold.setImageURI(uri)
            }.addOnFailureListener { exception ->
                Log.e("Reci-P", "Error getting URI for image: ${exception.localizedMessage}")
            }
        }

        holder.detail.text = data[position].cookTime
        holder.title.text = data[position].title
        holder.recipeDescUnfold.text = recipe.description

        holder.actionButton.text = "Save"
        holder.actionButton.setOnClickListener {
            val newRecipe = Recipe(FirebaseAuth.getInstance().currentUser!!.uid, UUID.randomUUID().toString(), recipe)
            DataManager.createRecipe(newRecipe, {
                holder.actionButton.text = "Saved"
                holder.saveBtnUnfold.text = "Saved"
            })
        }
        holder.view.setOnClickListener {
            holder.view.findViewById<FoldingCell>(R.id.recipe_folding_cell).toggle(false)
        }

        holder.recipeTitleUnfold.text = recipe.title

        DataManager.getUser(recipe.creator, save = true) { owner ->
            if (owner != null) {
                holder.recipeAuthorUnfold.text = owner.displayName
            } else {
                holder.recipeAuthorUnfold.text = ""
            }
        }
        holder.view.setOnClickListener {
            holder.view.findViewById<FoldingCell>(R.id.recipe_folding_cell).toggle(false)
        }

        holder.ratingUnfold.rating = recipe.rating
        holder.cookTimeUnfold.text = recipe.cookTime
        holder.prepTimeUnfold.text = recipe.prepTime

        holder.ingredientHolderUnfold.removeAllViews()
        for (i in recipe.ingredients) {
            val view = LayoutInflater.from(holder.view.context).inflate(R.layout.ingredient, null)
            view.findViewById<TextView>(R.id.ingredient_text).text = i
            holder.ingredientHolderUnfold.addView(view)
        }

        holder.instructionHolderUnfold.removeAllViews()
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
            onClickEdit(recipe)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class RecipeViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<SimpleDraweeView>(R.id.recipeSmall_photo)
        val detail = view.findViewById<TextView>(R.id.recipeSmall_detail)
        val title = view.findViewById<TextView>(R.id.recipeSmall_title)
        val actionButton = view.findViewById<Button>(R.id.recipeSmall_actionButton)

        var image_unfold = view.findViewById<SimpleDraweeView>(R.id.imageView_unfold)
        var recipeTitleUnfold = view.findViewById<TextView>(R.id.recipeLarge_title_unfold)
        var recipeDescUnfold = view.findViewById<TextView>(R.id.recipeLarge_description_unfold)
        var recipeAuthorUnfold = view.findViewById<TextView>(R.id.recipeLarge_author_unfold)
        var ratingUnfold = view.findViewById<RatingBar>(R.id.ratingBar_unfold)
        var cookTimeUnfold = view.findViewById<TextView>(R.id.recipeLarge_cook_time_unfold)
        var prepTimeUnfold = view.findViewById<TextView>(R.id.recipeLarge_prep_time_unfold)
        var ingredientHolderUnfold = view.findViewById<LinearLayout>(R.id.recipeLarge_ingredients_holder_unfold)
        var instructionHolderUnfold = view.findViewById<LinearLayout>(R.id.recipeLarge_instructions_holder_unfold)
        var saveBtnUnfold = view.findViewById<Button>(R.id.recipeLarge_btn_save)
        var editBtnUnfold = view.findViewById<Button>(R.id.recipeLarge_btn_edit)
    }
}