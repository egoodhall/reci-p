package com.reci_p.reci_p.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reci_p.reci_p.R
import com.reci_p.reci_p.activities.EditorActivity
import com.reci_p.reci_p.activities.MainActivity

/**
 * Updated by Sienna Mosher on 11/29/17.
 */



class MyRecipesFragment : Fragment() {

    companion object {
        val TAG: String = MyRecipesFragment::class.java.simpleName
        fun newInstance() = MyRecipesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity.title = getString(R.string.title_my_recipes)
        val view = inflater?.inflate(R.layout.fragment_my_recipes, container, false)
        view!!.findViewById<FloatingActionButton>(R.id.fragmentMyRecipes_FAB).setOnClickListener {
            val intent = Intent(activity.applicationContext, EditorActivity::class.java)
            startActivity(intent)
        }
        return view
    }


}