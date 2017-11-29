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



class MyProfileFragment : Fragment() {

    companion object {
        val TAG: String = MyProfileFragment::class.java.simpleName
        fun newInstance() = MyProfileFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity.title = getString(R.string.title_my_profile)
        val view = inflater?.inflate(R.layout.fragment_my_profile, container, false)
        return view
    }

}