package com.reci_p.reci_p.fragments

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.reci_p.reci_p.R
import com.reci_p.reci_p.activities.EditorActivity
import com.reci_p.reci_p.activities.SearchActivity
import com.reci_p.reci_p.adapters.LargeRecipeListAdapter
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.helpers.DataManager
import com.reci_p.reci_p.helpers.RecipeDiffCb
import io.realm.RealmList

/**
 * Updated by Sienna Mosher on 11/29/17.
 */



class RecipeFeedFragment : Fragment() {

    lateinit var currentUser: FirebaseUser

    lateinit var myRecipes : RecyclerView

    val data = RealmList<Recipe>()

    var page = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity.title = getString(R.string.title_my_recipes)
        val view = inflater?.inflate(R.layout.fragment_recipe_feed, container, false)
        view!!.findViewById<FloatingActionButton>(R.id.fragmentRecipeFeed_FAB).setOnClickListener {
            val intent = Intent(activity.applicationContext, SearchActivity::class.java)
            startActivity(intent)
        }



        myRecipes = view.findViewById<RecyclerView>(R.id.recyclerviewRecipes_recyclerview)

        currentUser = FirebaseAuth.getInstance().currentUser!!

        myRecipes.layoutManager = LinearLayoutManager(activity.applicationContext)
        myRecipes.adapter = LargeRecipeListAdapter(data) { recipe ->
            val intent = Intent(activity.applicationContext, EditorActivity::class.java)
            intent.putExtra("recipeId", recipe.id)
            intent.putExtra("view", 1)
            startActivity(intent)
        }

        val swipeContainer = view.findViewById<SwipeRefreshLayout>(R.id.recyclerviewRecipes_swipeRefreshLayout)
        setupSwipeContainer(swipeContainer)
        swipeContainer.post {
            swipeContainer.isRefreshing = true
            updateDataSet {
                swipeContainer.isRefreshing = false
            }
        }

        return view
    }

    fun updateDataSet(cb: (() -> Unit)? = null) {
        page = 0
        DataManager.getFeed(currentUser.uid, page = page) { recipes ->
            if (recipes != null) {
                data.clear()
                data.addAll(recipes)
                val diff = DiffUtil.calculateDiff(RecipeDiffCb(data, recipes))
                diff.dispatchUpdatesTo(myRecipes.adapter)
            }
            if (cb != null) {
                cb()
            }
        }
    }

    private fun setupSwipeContainer(swipeContainer: SwipeRefreshLayout) {

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener {
            updateDataSet {
                swipeContainer.isRefreshing = false
            }
        }
    }

    companion object {
        val TAG: String = RecipeFeedFragment::class.java.simpleName
        fun newInstance() = RecipeFeedFragment()
    }
}