package com.reci_p.reci_p.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.google.firebase.auth.FirebaseAuth
import com.reci_p.reci_p.R
import com.reci_p.reci_p.data.User
import com.reci_p.reci_p.helpers.DataManager

/**
 * Created by Laura on 11/30/17.
 */
class SearchResultsAdapter(val data: MutableList<User>,
                           val onClickCell : (User) -> Unit,
                           val onClickAction : (View, Int) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.UserProfileViewHolder>() {

    var ctx: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UserProfileViewHolder {
        if (ctx == null) ctx = parent?.context
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.profile_small, parent, false)
        return UserProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserProfileViewHolder?, position: Int) {
        holder?.img?.setImageURI(data[position].photo, ctx)
        holder?.userName?.text = data[position].userName
        holder?.displayName?.text = data[position].displayName
        DataManager.getFollowing(FirebaseAuth.getInstance().currentUser!!.uid) { users ->
            if (users != null) {
                val find = users.find { user -> user.id == data[position].id }
                holder?.actionButton?.text = if (find != null) "Unsubscribe" else "Subscribe"
            }
        }
        holder?.actionButton?.setOnClickListener { view -> onClickAction(view, position) }
        holder?.view?.setOnClickListener { onClickCell(data[position]) }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class UserProfileViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<SimpleDraweeView>(R.id.profileSmall_photo)
        val userName = view.findViewById<TextView>(R.id.profileSmall_userName)
        val displayName = view.findViewById<TextView>(R.id.profileSmall_displayName)
        val actionButton = view.findViewById<Button>(R.id.profileSmall_actionButton)
    }
}