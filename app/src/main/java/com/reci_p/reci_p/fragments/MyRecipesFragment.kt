package com.reci_p.reci_p.fragments

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.reci_p.reci_p.R
import com.reci_p.reci_p.activities.EditorActivity
import com.reci_p.reci_p.adapters.LargeRecipeListAdapter
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.helpers.DataManager
import io.realm.RealmList

/**
 * Updated by Sienna Mosher on 11/29/17.
 */



class MyRecipesFragment : Fragment() {

    lateinit var currentUser: FirebaseUser

    lateinit var myRecipes : RecyclerView

    val data = RealmList<Recipe>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity.title = getString(R.string.title_my_recipes)
        val view = inflater?.inflate(R.layout.fragment_my_recipes, container, false)
        view!!.findViewById<FloatingActionButton>(R.id.fragmentMyRecipes_FAB).setOnClickListener {
            val intent = Intent(activity.applicationContext, EditorActivity::class.java)
            startActivity(intent)
        }



        myRecipes = view.findViewById<RecyclerView>(R.id.recyclerviewRecipes_recyclerview)

        currentUser = FirebaseAuth.getInstance().currentUser!!

        myRecipes.layoutManager = LinearLayoutManager(activity.applicationContext)
        myRecipes.adapter = LargeRecipeListAdapter(data) { recipe ->
            val intent = Intent(activity.applicationContext, EditorActivity::class.java)
            // TODO: Launch editor for activity
            startActivity(intent)
        }

        DataManager.getRecipesForUser(currentUser.uid, save = true) { recipes ->
            if (recipes != null) {
                data.clear()
                data.addAll(recipes)
                myRecipes.adapter.notifyDataSetChanged()
            }
        }

        val swipeContainer = view.findViewById<SwipeRefreshLayout>(R.id.recyclerviewRecipes_swipeRefreshLayout)
        setupSwipeContainer(swipeContainer)

        return view
    }

    private fun setupSwipeContainer(swipeContainer: SwipeRefreshLayout) {

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener {
            DataManager.getRecipesForUser(currentUser.uid, save = true, refresh = true) { recipes ->
                Log.d("Reci-P", "Done")
                val adapter = myRecipes.adapter as LargeRecipeListAdapter
                if (recipes != null) {
                    data.clear()
                    data.addAll(recipes)
                    adapter.notifyDataSetChanged()
                }
                Log.d("Reci-P", "${data.size}")
                swipeContainer.isRefreshing = false
            }
        }
    }

    companion object {
        val TAG: String = MyRecipesFragment::class.java.simpleName
        fun newInstance() = MyRecipesFragment()
    }
}