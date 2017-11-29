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
import com.reci_p.reci_p.data.User

/**
 *
 *
 * Created by Eric Marshall on 11/27/17
 */
class FollowingListAdapter(val data: MutableList<User>,
                           val onClickCell : (User) -> Unit,
                           val onClickAction : (View, Int) -> Unit
) : RecyclerView.Adapter<FollowingListAdapter.UserProfileViewHolder>() {

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
        holder?.actionButton?.text = "Unsubscribe"
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