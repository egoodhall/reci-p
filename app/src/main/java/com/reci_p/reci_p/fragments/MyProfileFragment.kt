package com.reci_p.reci_p.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.google.firebase.auth.FirebaseAuth
import com.reci_p.reci_p.R
import com.reci_p.reci_p.activities.LoginActivity
import com.reci_p.reci_p.adapters.FollowingListAdapter
import com.reci_p.reci_p.data.User
import com.reci_p.reci_p.helpers.DataManager
import java.util.*

/**
 * Updated by Sienna Mosher on 11/29/17.
 */
class MyProfileFragment : Fragment() {

    lateinit var followingList : RecyclerView

    lateinit var data : ArrayList<User>

    var loadedPagesTo = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_my_profile, container, false)

        val profile = view.findViewById<View>(R.id.fragmentMyProfile_profileLarge)

        // Get current user
        val currentUser = FirebaseAuth.getInstance().currentUser!!

        // Set log out button action and text
        val logoutButton = profile.findViewById<Button>(R.id.profileLarge_actionButton)
        logoutButton.setOnClickListener { view: View -> logOut(view) }
        logoutButton.text = "Log Out"
        logoutButton.isEnabled = true

        // Set displayed values for username and display name
        profile.findViewById<TextView>(R.id.profileLarge_userName).text = currentUser.email
        profile.findViewById<TextView>(R.id.profileLarge_displayName).text = currentUser.displayName

        // Set Profile image for current user
        val controller = Fresco.newDraweeControllerBuilder().setUri(currentUser.photoUrl)
        val profileImg = profile.findViewById<SimpleDraweeView>(R.id.profileLarge_photo)
        profileImg.controller = controller.setOldController(profileImg.controller).build()

        // Set up the recyclerview of users being followed
        profile.findViewById<TextView>(R.id.profileLarge_title).text = "Following:"
        followingList = view.findViewById<RecyclerView>(R.id.fragmentMyProfile_followingList)
        populateFollowingList(currentUser.uid, followingList)

        return view
    }

    fun populateFollowingList(uid: String, list: RecyclerView) {
        data = ArrayList()

        val unsubscribeHandler = { view: View, pos: Int ->
            (view as Button).text = ""
            data.removeAt(pos)
            list.adapter.notifyItemRemoved(pos)
        }

        val launchUserProfile = { user: User ->
            // TODO: Launch activity
        }

        list.adapter = FollowingListAdapter(data, launchUserProfile, unsubscribeHandler)

        list.layoutManager = LinearLayoutManager(activity.applicationContext)

        // Populate data
        DataManager.getFollowing(uid) { users ->
            val adapter = list.adapter as FollowingListAdapter
            Log.d("Reci-P", "${adapter.data.size} before")
            users!!.forEach {
                (list.adapter as FollowingListAdapter).data.add(it)
                list.adapter.notifyDataSetChanged()
                Log.d("Reci-P", "add ${it.toString()}")
            }
            Log.d("Reci-P", "${adapter.data.size} after")
        }
    }

    fun logOut(view: View) {
        val intent = Intent(activity.applicationContext, LoginActivity::class.java)
        intent.putExtra("signOut", "")
        activity.startActivity(intent)
        activity.finish()
    }

    companion object {

        val TAG: String = MyProfileFragment::class.java.simpleName

        fun newInstance(): MyProfileFragment {
            val fragment = MyProfileFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
