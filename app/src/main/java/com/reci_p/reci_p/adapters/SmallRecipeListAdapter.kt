package com.reci_p.reci_p.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.reci_p.reci_p.R
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.data.User

/**
 *
 *
 * Created by Eric Marshall on 11/27/17
 */
class SmallRecipeListAdapter(val data: MutableList<Recipe>,
                           val onClickCell : (Recipe) -> Unit,
                           val onClickAction : (View, Int) -> Unit
) : RecyclerView.Adapter<SmallRecipeListAdapter.RecipeViewHolder>() {

    var ctx: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecipeViewHolder {
        if (ctx == null) ctx = parent?.context
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.recipe_small, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder?, position: Int) {
        holder?.img?.setImageURI(data[position].photo, ctx)
        holder?.detail?.text = data[position].cookTime
        holder?.title?.text = data[position].title
        holder?.actionButton?.text = "Save"
        holder?.actionButton?.setOnClickListener { view -> onClickAction(view, position) }
        holder?.view?.setOnClickListener { onClickCell(data[position]) }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class RecipeViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<SimpleDraweeView>(R.id.recipeSmall_photo)
        val detail = view.findViewById<TextView>(R.id.recipeSmall_detail)
        val title = view.findViewById<TextView>(R.id.recipeSmall_title)
        val actionButton = view.findViewById<Button>(R.id.recipeSmall_actionButton)
    }
}