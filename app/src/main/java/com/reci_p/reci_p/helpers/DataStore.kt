package com.reci_p.reci_p.helpers

import android.renderscript.Sampler
import android.util.Log
import com.google.firebase.database.*
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.data.User

/**
 *
 *
 * Created by Eric Marshall on 11/15/17
 */
class DataStore {

    companion object {
        fun createUser(user: User, cb: (success: Boolean) -> Unit) {
            cb(true)
        }

        fun follow(to: String, cb: (success: Boolean) -> Unit) {
            cb(if (Math.random() > 0.75) true else false)
        }

        fun unfollow(from: String, cb: (success: Boolean) -> Unit) {
            cb(if (Math.random() > 0.75) true else false)
        }

        fun searchUsers(query: String, cb: (users: List<User>) -> Unit) {
            val u1 = User("", "em", "eric_marshall", "Eric Marshall", ArrayList<String>())
            val u2 = User("", "js", "jordan_sechler", "Jordan Sechler", ArrayList<String>())
            val u3 = User("", "sm", "sienna_mosher", "Sienna Mosher", ArrayList<String>())
            cb(arrayListOf(u1,u2,u3))
        }

        fun getUser(uid: String, cb: (user: User) -> Unit) {
            cb(User("", "lp", "laura_poulton", "Laura Poulton", ArrayList<String>()))
        }

        fun createRecipe(recipe: Recipe, cb: (success: Boolean) -> Unit) {
            cb(if (Math.random() > 0.75) true else false)
        }

        fun updateRecipe(recipe: Recipe, cb: (success: Boolean) -> Unit) {
            cb(if (Math.random() > 0.75) true else false)
        }

        fun deleteRecipe(id: String, cb: (success: Boolean) -> Unit) {
            cb(if (Math.random() > 0.75) true else false)
        }

        fun getRecipesForUser(uid: String, cb: (recipes: List<Recipe>) -> Unit) {
            cb(arrayListOf())
        }

        fun getRecipe(id: String, cb: (recipe: Recipe) -> Unit) {

        }

        fun getFeed(uid: String, cb: (recipes: List<Recipe>) -> Unit) {
            cb(arrayListOf())
        }

    }
}