package com.reci_p.reci_p.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.reci_p.reci_p.R
import com.reci_p.reci_p.adapters.SmallRecipeListAdapter
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.data.User
import com.reci_p.reci_p.helpers.DataManager
import com.reci_p.reci_p.helpers.DataManager.Companion.gson
import com.reci_p.reci_p.helpers.DataManager.Companion.realm
import java.util.*
import kotlin.collections.ArrayList

class UserProfileActivity : AppCompatActivity() {

    lateinit var recipeList : RecyclerView
    lateinit var data : MutableList<Recipe>
    lateinit var displayedUser: User
    lateinit var loggedInUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Currently logged in user
        loggedInUser = FirebaseAuth.getInstance().currentUser!!

        if (!intent.hasExtra("displayUser")) {
            throw IllegalStateException("User not passed in intent as 'displayUser' extra")
        }

        // User to be displayed
        displayedUser = gson.fromJson(intent.getStringExtra("displayUser"), User::class.java)

        val profile = findViewById<View>(R.id.activityUserProfile_profileLarge)

        // Set log out button action and text
        val subscribeButton = profile.findViewById<Button>(R.id.profileLarge_actionButton)
        subscribeButton.text = "Subscribe"
        subscribeButton.setOnClickListener { view: View -> toggleFollowing(view) }
        DataManager.getFollowing(loggedInUser.uid) { users ->
            val isSubscribed = users?.find { it.id == displayedUser.id && it.id != loggedInUser.uid } != null
            subscribeButton.text = if (isSubscribed) "Unsubscribe" else "Subscribe"
            subscribeButton.isEnabled = true
        }

        // Set displayed values for username and display name
        profile.findViewById<TextView>(R.id.profileLarge_userName).text = displayedUser.userName
        profile.findViewById<TextView>(R.id.profileLarge_displayName).text = displayedUser.displayName

        // Set Profile image for displayed user
        val controller = Fresco.newDraweeControllerBuilder().setUri(displayedUser.photo)
        val profileImg = profile.findViewById<SimpleDraweeView>(R.id.profileLarge_photo)
        profileImg.controller = controller.setOldController(profileImg.controller).build()

        // Set up the recycler view of recipes being followed
        profile.findViewById<TextView>(R.id.profileLarge_title).text = "Recipes:"
        recipeList = findViewById<RecyclerView>(R.id.activityUserProfile_recipesList)
        populateRecipeList(displayedUser.id, recipeList)
    }

    fun toggleFollowing(view: View) {
        val button = view as Button
        if (button.text == "Unsubscribe") {
            DataManager.unfollow(loggedInUser.uid, displayedUser.id) { success ->
                if (success) button.text = "Subscribe"
            }
        } else {
            DataManager.follow(loggedInUser.uid, displayedUser.id) { success ->
                if (success) button.text = "Unsubscribe"
            }
        }
    }

    fun populateRecipeList(uid: String, list: RecyclerView) {
        data = ArrayList()

        // Create a copy of the recipe with a new id and owner
        val saveRecipe = { view: View, pos: Int ->
            val recipe = realm.copyFromRealm(data[pos])
            recipe.id = UUID.randomUUID().toString()
            recipe.owner = uid
            DataManager.createRecipe(recipe, {})
        }

        val launchEditor = { recipe: Recipe ->
            // TODO: Launch activity
        }

        // Set adapter
        list.adapter = SmallRecipeListAdapter(data, launchEditor, saveRecipe)

        // Populate data
        DataManager.getRecipesForUser(uid) { recipes ->
            val before = data.size
            data.addAll(recipes!!.asIterable())
            val after = data.size
            list.adapter.notifyItemRangeInserted(before, after)
            Log.d("Reci-P", "${recipes.size} items inserted")
        }
    }
}
