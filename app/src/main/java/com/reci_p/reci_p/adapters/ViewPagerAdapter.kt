package com.reci_p.reci_p.adapters

/**
 * Created by SiennaMosher on 11/29/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

// 1
class ViewPagerAdapter(fragmentManager: FragmentManager) :

        FragmentPagerAdapter(fragmentManager) {
    private var mFragmentList = ArrayList<Fragment>()


    // 2
    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position)
    }

    // 3
    override fun getCount(): Int {
        return mFragmentList.count()
    }
    public fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

}