package com.reci_p.reci_p.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.reci_p.reci_p.R
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
//import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.reci_p.reci_p.fragments.MyProfileFragment
import com.reci_p.reci_p.fragments.MyRecipesFragment
import com.reci_p.reci_p.fragments.RecipeFeedFragment
import com.reci_p.reci_p.adapters.ViewPagerAdapter
import com.reci_p.reci_p.helpers.BottomNavHelper
import com.reci_p.reci_p.helpers.BottomNavPosition
import com.reci_p.reci_p.extension.*
import android.support.v4.view.ViewPager
import android.support.annotation.NonNull



class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val KEY_POSITION = "keyPosition"

    private var navPosition: BottomNavPosition = BottomNavPosition.MY_RECPIES //set initial position for nav

    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var viewPager: ViewPager

    private lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        initFragment(savedInstanceState)
        restoreSaveInstanceState(savedInstanceState)
        setupBottomNavigation()
        setupViewPager()

    }


    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(KEY_POSITION, navPosition.id)
        super.onSaveInstanceState(outState)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navPosition = BottomNavHelper.findPositionById(item.itemId)
        when(item.getItemId()) {
            R.id.recipes -> {
                viewPager.setCurrentItem(0)
                switchFragment(MyRecipesFragment.newInstance(), MyRecipesFragment.TAG)
            }
            R.id.profile -> {
                viewPager.setCurrentItem(1)
                switchFragment(MyProfileFragment.newInstance(), MyProfileFragment.TAG)
            }
            R.id.feed -> {
                viewPager.setCurrentItem(2)
                switchFragment(RecipeFeedFragment.newInstance(), RecipeFeedFragment.TAG)
            }
            else -> return false
        }
        return true
    }

    private fun restoreSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.also {
            val id = it.getInt(KEY_POSITION, BottomNavPosition.MY_RECPIES.id) //this
            navPosition = BottomNavHelper.findPositionById(id) //this
        }
    }

    private fun bindViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }


    private fun setupBottomNavigation() {
        bottomNavigation.disableShiftMode() //do this
        bottomNavigation.active(navPosition.position) //do this
        bottomNavigation.setOnNavigationItemSelectedListener(this)

    }

    private fun setupViewPager() {
        viewPager = findViewById(R.id.viewpager)
        adapter = ViewPagerAdapter(supportFragmentManager)
        var recipeFrag = MyRecipesFragment.newInstance()
        var  profileFrag= MyProfileFragment.newInstance()
        var feedFrag = RecipeFeedFragment.newInstance()

        adapter.addFragment(recipeFrag)
        adapter.addFragment(profileFrag)
        adapter.addFragment(feedFrag)
        viewPager.adapter = adapter
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {


            }

            override fun onPageSelected(position: Int) {


                bottomNavigation.menu.getItem(position).setChecked(true)
                navPosition = BottomNavHelper.findPositionById(position) // do this shit

            }

        })
    }

    /**
     * FRAGMENT OPERATIONS:
     *  init
     *  switch
     *  detach
     *  attach
     */


    private fun initFragment(savedInstanceState: Bundle?) {
        savedInstanceState ?: switchFragment(MyRecipesFragment.newInstance(), MyRecipesFragment.TAG)
    }

    /**
     * Immediately execute transactions with FragmentManager#executePendingTransactions.
     */
    private fun switchFragment(fragment: Fragment, tag: String): Boolean {
        if (fragment.isAdded) return false
        detachFragment()
        attachFragment(fragment, tag)
        supportFragmentManager.executePendingTransactions()
        return true
    }

    private fun detachFragment() {
        supportFragmentManager.findFragmentById(R.id.container)?.also {
            supportFragmentManager.beginTransaction().detach(it).commit()
        }
    }

    private fun attachFragment(fragment: Fragment, tag: String) {
        if (fragment.isDetached) {
            supportFragmentManager.beginTransaction().attach(fragment).commit()
        } else {
            supportFragmentManager.beginTransaction().add(R.id.container, fragment, tag).commit()
        }
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }



}

