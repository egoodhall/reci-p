package com.reci_p.reci_p.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.reci_p.reci_p.R
import com.reci_p.reci_p.adapters.SearchResultsAdapter
import com.reci_p.reci_p.data.User
import com.reci_p.reci_p.helpers.DataManager
import io.realm.RealmList

class SearchActivity : AppCompatActivity() {

    var currentPage = 0
    val data = RealmList<User>()
    lateinit var resultsList : RecyclerView
    lateinit var loggedInUser : FirebaseUser
    var lastSearch : Search? = null
    var loadingPage = false
    lateinit var searchEditText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        loggedInUser = FirebaseAuth.getInstance().currentUser!!

        searchEditText = findViewById<EditText>(R.id.toolbarSearch_editText)
        searchEditText.addTextChangedListener(SearchWatcher())

        resultsList = findViewById(R.id.activitySearch_resultList)
        resultsList.layoutManager = LinearLayoutManager(applicationContext)
        val subscribeHandler = { view: View, pos: Int ->
            val btn = (view as Button)
            if (btn.text == "Unsubscribe") {
                DataManager.unfollow(loggedInUser.uid, data[pos]!!.id) { success ->
                    if (success) {
                        btn.text = "Subscribe"
                    }
                    DataManager.getFollowing(loggedInUser.uid, true) {}
                }
            } else {
                DataManager.follow(loggedInUser.uid, data[pos]!!.id) { success ->
                    if (success) {
                        btn.text = "Unsubscribe"
                    }
                    DataManager.getFollowing(loggedInUser.uid, true) {}
                }
            }
        }

        val launchUserProfile = { user: User ->
            val intent = Intent(applicationContext, UserProfileActivity::class.java)
            intent.putExtra("displayUser", User.json(user))
            startActivity(intent)
        }

        resultsList.adapter = SearchResultsAdapter(data, launchUserProfile, subscribeHandler)

        resultsList.addOnScrollListener(ScrollListener(resultsList.layoutManager as LinearLayoutManager))
    }

    fun updateSearch(query: String, clear: Boolean = true, nextPage: Boolean = false) {
        if (lastSearch != null) {
            (lastSearch as Search).active = false
        }
        lastSearch = Search { active, users ->
            if (nextPage) {
                currentPage++
            } else {
                currentPage = 0
            }
            if (active) {
                if (users != null) {
                    Log.d("Reci-P", "Received ${users.size} users")
                    if (clear) {
                        data.clear()
                    }
                    data.addAll(users)
                    resultsList.adapter.notifyDataSetChanged()
                }
            }
            lastSearch = null
        }
        DataManager.searchUsers(loggedInUser.email!!, query, page = currentPage) { users ->
            if (lastSearch != null && users != null) {
                (lastSearch as Search).handle(users)
            }
        }
    }

    inner class SearchWatcher : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            updateSearch(text.toString())
        }

    }

    inner class ScrollListener(val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            if (loadingPage)
                return
            val visibleItemCount = layoutManager.getChildCount()
            val totalItemCount = layoutManager.getItemCount()
            val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
            if (pastVisibleItems + visibleItemCount >= totalItemCount && data.size % 10 == 0) {
                updateSearch(searchEditText.text.toString(), clear = false, nextPage = true)
            }
        }
    }

    class Search(val cb : (active: Boolean, users: MutableList<User>) -> Unit) {
        var active = true
        fun handle(users: MutableList<User>) {
            cb(active, users)
        }
    }
}
