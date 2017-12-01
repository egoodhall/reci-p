package com.reci_p.reci_p.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button

import com.reci_p.reci_p.R
import com.reci_p.reci_p.data.User

import com.google.firebase.storage.FirebaseStorage
import com.facebook.drawee.view.SimpleDraweeView

/**
 * Created by Laura on 11/30/17.
 */
class SearchResultsAdapter(val mContext: Context, val resultsList: List<User>?) : RecyclerView.Adapter<SearchResultsAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_small, null)
        //View view = view.findViewById(R.id.recyclerviewRecipes_recyclerview);
        return CustomViewHolder(view)
    }

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: SimpleDraweeView
        var displayName: TextView
        var userName: TextView
        var subscribeButton: Button

        init {
            this.image = view.findViewById<View>(R.id.profileSmall_photo) as SimpleDraweeView
            this.displayName = view.findViewById<View>(R.id.profileSmall_displayName) as TextView
            this.userName = view.findViewById<View>(R.id.profileSmall_userName) as TextView
            this.subscribeButton = view.findViewById<View>(R.id.profileSmall_actionButton) as Button
        }
    }

    override fun onBindViewHolder(holder: SearchResultsAdapter.CustomViewHolder, position: Int) {
        //get recipe
        val result = resultsList!![position]

        //set values in view
        //first get the recipe image from Firebase
        val urlString = "gs://bucket/" + result.photo
        //the below function is ap
        //holder.image.setImageURI(FirebaseStorage.getInstance().getReferenceFromUrl(urlString).downloadUrl.result)
        holder.image.setImageURI(urlString);

        //then set the TextViews
        holder.displayName.text = result.displayName
        holder.userName.text = result.userName
    }

    override fun getItemCount(): Int {
        return resultsList?.size ?: 0
    }
}