package com.reci_p.reci_p.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reci_p.reci_p.R

/**
 * Updated by Sienna Mosher on 11/29/17.
 */



class RecipeFeedFragment : Fragment() {

    companion object {
        val TAG: String = RecipeFeedFragment::class.java.simpleName
        fun newInstance() = RecipeFeedFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity.title = getString(R.string.title_recipe_feed)
        val view = inflater?.inflate(R.layout.fragment_recipe_feed, container, false)
        return view
    }

}