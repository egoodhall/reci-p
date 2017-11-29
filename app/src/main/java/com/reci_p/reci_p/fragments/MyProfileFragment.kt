package com.reci_p.reci_p.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

    lateinit var currentUser : FirebaseUser

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_my_profile, container, false)

        val profile = view.findViewById<View>(R.id.fragmentMyProfile_profileLarge)

        // Get current user
        currentUser = FirebaseAuth.getInstance().currentUser!!

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

        // Following list title
        profile.findViewById<TextView>(R.id.profileLarge_title).text = "Following:"

        // Set up swipe container
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.fragmentMyProfile_swipeRefreshLayout)
        setupSwipeContainer(swipeRefreshLayout)

        // Set up the recyclerview of users being followed
        followingList = swipeRefreshLayout.findViewById<RecyclerView>(R.id.fragmentMyProfile_followingList)
        populateFollowingList(currentUser.uid, followingList)

        return view
    }

    private fun setupSwipeContainer(swipeContainer: SwipeRefreshLayout) {

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener {
            DataManager.getFollowing(currentUser.uid) { users ->
                val adapter = followingList.adapter as FollowingListAdapter
                data.clear()
                users!!.forEach {
                    data.add(it)
                }
                adapter.notifyDataSetChanged()
                swipeContainer.isRefreshing = false
            }
        }
    }

    fun populateFollowingList(uid: String, list: RecyclerView) {
        data = ArrayList()

        val unsubscribeHandler = { view: View, pos: Int ->
            DataManager.unfollow(currentUser.uid, data[pos].id) { success ->
                if (success) {
                    (view as Button).text = ""
                    data.removeAt(pos)
                    list.adapter.notifyItemRemoved(pos)
                } else {
                    Toast.makeText(activity.applicationContext, "Unfollowing failed.", Toast.LENGTH_SHORT)
                }
            }
        }

        val launchUserProfile = { user: User ->
            // TODO: Launch activity
        }

        list.adapter = FollowingListAdapter(data, launchUserProfile, unsubscribeHandler)

        list.layoutManager = LinearLayoutManager(activity.applicationContext)

        // Populate data
        DataManager.getFollowing(uid) { users ->
            val adapter = list.adapter as FollowingListAdapter
            users!!.forEach {
                adapter.data.add(it)
            }
            adapter.notifyDataSetChanged()
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
