package com.reci_p.reci_p.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
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
import com.reci_p.reci_p.helpers.RecipeDiffCb
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
            intent.putExtra("recipeId", recipe.id)
            startActivity(intent)
        }

//        updateDataSet()

        val swipeContainer = view.findViewById<SwipeRefreshLayout>(R.id.recyclerviewRecipes_swipeRefreshLayout)
        setupSwipeContainer(swipeContainer)

        swipeContainer.post {
            swipeContainer.isRefreshing = true
            updateDataSet {
                swipeContainer.isRefreshing = false
            }
        }

        val simpleCallback = SwipeDeleter()
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(myRecipes) //set swipe to recylcerview

        return view
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

    fun updateDataSet(cb: (() -> Unit)? = null) {
        DataManager.getRecipesForUser(currentUser.uid, save = true, refresh = true) { recipes ->
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

    inner class SwipeDeleter : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition

            if (direction == ItemTouchHelper.RIGHT) {
                val builder = AlertDialog.Builder(activity)
                        .setMessage("Delete ${data[position]!!.title}?")
                        .setPositiveButton("DELETE", DialogInterface.OnClickListener { dialog, which ->
                            DataManager.deleteRecipe(data[position]!!.id) { success ->
                                if (success) {
                                    data.remove(data[position])
                                    myRecipes.adapter.notifyItemRemoved(position)
                                }
                            }

                        })
                        .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
                            myRecipes.adapter.notifyItemRemoved(position + 1)
                            myRecipes.adapter.notifyItemRangeChanged(position, myRecipes.adapter.getItemCount())
                        }).show()
            }
        }
    }

    companion object {
        val TAG: String = MyRecipesFragment::class.java.simpleName
        fun newInstance() = MyRecipesFragment()
    }
}