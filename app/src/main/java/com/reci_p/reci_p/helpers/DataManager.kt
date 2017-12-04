package com.reci_p.reci_p.helpers

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.google.gson.Gson
import com.reci_p.reci_p.R
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.data.User
import com.reci_p.reci_p.interfaces.Parseable
import io.realm.Realm
import io.realm.Sort
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URLEncoder

/**
 * A static data management client
 *
 * A data handling class for working with the data in the backend server. All functions take a
 * callback and perform tasks asynchronously so the rest of the application doesn't need to worry
 * about it when making queries
 *
 * Created by Eric Marshall on 11/15/17
 */
class DataManager {

    companion object {

        val client = OkHttpClient()
        val gson = Gson()
        val json = MediaType.parse("application/json; charset=utf-8")

        lateinit var resources: Resources
        lateinit var realm: Realm

        /**
         * Initialize the datastore
         *
         * This should be the first function called in the class. It will take in a context that is
         * used to retrieve string resources when making queries
         *
         * @param context An application context that can be used for retrieving string resources
         */
        fun initialize(context: Context) {
            resources = context.resources
            realm = Realm.getDefaultInstance()
        }

        /**
         * Create a user in the backend
         *
         * This should ONLY be called from the login screen - it is used to create a user in the
         * backend on first login
         *
         * @param user A user object that has been populated
         * @param cb A callback that receives whether or not the query was successful
         */
        fun createUser(user: User, cb: (user: User?) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/users")
            val req = Request.Builder()
                    .post(RequestBody.create(json, gson.toJson(user)))
                    .url(endpoint).build()

            // Create on remote
            runForSingle<User>(req, User) { user ->
                if (user != null) {
                    realm.executeTransaction {
                        realm.insert(user)
                    }
                }
                cb(user)
            }
        }

        /**
         * Follow a user
         *
         * @param consumer The user that will start following the producer
         * @param producer The user that will be followed by the consumer
         * @param cb A callback that receives whether or not the query was successful
         */
        fun follow(consumer: String, producer: String, cb: (success: Boolean) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/users/$consumer/follow/$producer")
            val req = Request.Builder()
                    .post(RequestBody.create(json, "{}"))
                    .url(endpoint).build()

            // Unsubscribe on remote
            runForResult(req, cb)
        }

        /**
         * Stop following a user
         *
         * @param consumer The user that will stop following the producer
         * @param producer The user that will stop being followed by the consumer
         * @param cb A callback that receives whether or not the query was successful
         */
        fun unfollow(consumer: String, producer: String, cb: (success: Boolean) -> Unit) {

            realm.executeTransaction {
                realm.where(User::class.java).equalTo("id", producer).findAll().deleteAllFromRealm()
            }

            val endpoint = resources.getString(R.string.api_base_url).plus("/users/$consumer/follow/$producer")
            var req = Request.Builder()
                    .delete()
                    .url(endpoint).build()

            // Unsubscribe on remote
            runForResult(req, cb)
        }

        /**
         * Get a list of users whose usernames contain the given query string
         *
         * The query will ignore the user's own username, and the results are paged, by default at
         * the first page (0)
         *
         * @param own The user's own username
         * @param query The string to query against usernames in the database
         * @param page The page number to query
         * @param cb A callback function for handling the returned list of users
         */
        fun searchUsers(own: String, query: String, page: Int = 0, cb: (users: MutableList<User>?) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url)
                .plus("/users/search/${URLEncoder.encode(query, "UTF-8")}?ignore=${own}&page=${page}")
            var req = Request.Builder()
                    .get()
                    .url(endpoint).build()
            Log.d("${endpoint}", "Searching for users with query ${query}")
            runForListOf<User>(req, User, cb)
        }

        /**
         * Get a user whose id matches the desired one
         *
         * @param uid The id of the user to find in the database
         * @param cb A callback function for handling the returned user
         */
        fun getUser(uid: String, save: Boolean = false, cb: (user: User?) -> Unit) {

            // Try to get from local
            val user = realm.where(User::class.java).equalTo("id", uid).findFirst()

            // If local fails, fetch from remote
            if (user != null) {
                cb(user)
            } else {
                val endpoint = resources.getString(R.string.api_base_url).plus("/users/$uid")
                var req = Request.Builder().get().url(endpoint).build()

                runForSingle<User>(req, User) { user ->
                    // Save to local
                    if (user != null && save) {
                        realm.executeTransaction {
                            realm.insertOrUpdate(user)
                        }
                    }
                    Log.d(endpoint, "Retrieved user")
                    cb(user)
                }
            }
        }

        /**
         * Get all users that a given user follows
         *
         *
         */
        fun getFollowing(uid: String, refresh: Boolean = false, cb: (users: MutableList<User>?) -> Unit) {

            // Try to get following locally
            val following = realm.where(User::class.java).notEqualTo("id", uid)
                    .findAllSorted(arrayOf("displayName", "userName"),
                            arrayOf(Sort.ASCENDING, Sort.ASCENDING)).toMutableList()

            // If there are none locally, go to the remote
            if (!refresh && following.size > 0) {
                cb(following)
            } else {
                val endpoint = resources.getString(R.string.api_base_url).plus("/users/$uid/following")
                var req = Request.Builder().get().url(endpoint).build()
                Log.d("${req.url()}", "Retrieving following from remote")
                runForListOf<User>(req, User) { users ->
                    // Local active
                    if (users != null) {
                        realm.executeTransaction {
                            realm.insertOrUpdate(users)
                        }
                    }
                    cb(users)
                }
            }
        }

        /**
         * Create a recipe in the backend
         *
         * @param recipe A populated recipe object
         * @param cb A callback that receives the created recipe object or null
         */
        fun createRecipe(recipe: Recipe, cb: (recipe: Recipe?) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/recipes")
            var req = Request.Builder()
                    .post(RequestBody.create(json, Recipe.json(recipe)))
                    .url(endpoint).build()
            Log.d("Reci-P", "Sending request to ${endpoint}")
            runForResult(req) { success ->
                Log.d("Reci-P", "${success}")
                // Local active
                if (success) {
                    Log.d("Reci-P", "Created Recipe")
                    realm.executeTransaction {
                        realm.insertOrUpdate(recipe)
                    }
                    cb(recipe)
                } else {
                    cb(null)
                }
            }
        }

        /**
         * Delete a recipe from the backend
         *
         * @param id The id of the recipe to remove from the system
         * @param cb A callback that receives whether or not the query was successful
         */
        fun deleteRecipe(id: String, cb: (success: Boolean) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/recipes/$id")
            var req = Request.Builder()
                    .delete()
                    .url(endpoint).build()

            // Delete from remote
            runForResult(req) { success ->
                if (success) {
                    realm.executeTransaction {
                        realm.where(Recipe::class.java).equalTo("id", id).findAll().deleteAllFromRealm()
                    }
                }
                cb(success)
            }
        }

        /**
         * Get all recipes for a given user
         *
         * @param uid The id of the user whose recipes should be retrieved
         * @param page The page to retrieve (-1 will retrieve all recipes)
         * @param save Save the recipes retrieved locally
         * @param cb A callback that takes in the list of recipes that the query returns
         */
        fun getRecipesForUser(uid: String, save: Boolean = false, refresh: Boolean = false, cb: (recipes: MutableList<Recipe>?) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/users/$uid/recipes")
            var req = Request.Builder().get().url(endpoint).build()

            // Try to get from local
            val recipes = realm.where(Recipe::class.java).equalTo("owner", uid)
                    .findAllSorted("title", Sort.ASCENDING).toMutableList().toMutableList()

            if (!refresh && recipes.size > 0) {
                cb(recipes)
            } else {
                runForListOf<Recipe>(req, Recipe) { recipes ->
                    if (save && recipes != null) {
                        realm.executeTransaction {
                            realm.insertOrUpdate(recipes)
                        }
                    }
                    cb(recipes)
                }
            }
        }

        /**
         * Retrieves a recipe with the given id
         *
         * @param id The id of the recipe to retrieve
         * @param cb A callback that takes in the retrieved recipe
         */
        fun getRecipe(id: String, cb: (recipe: Recipe?) -> Unit) {

            // Try to get from local
            val recipe = realm.where(Recipe::class.java).equalTo("id", id).findFirst()

            // If local fails, try to fetch from remote
            if (recipe != null) {
                cb(recipe)
            } else {
                val endpoint = resources.getString(R.string.api_base_url).plus("/recipes/$id")
                var req = Request.Builder().get().url(endpoint).build()
                runForSingle<Recipe>(req, Recipe, cb)
            }
        }

        /**
         * Retrieves the feed of recipes for a given user
         *
         * A feed is built of the most recent recipes published by the users that one follows.
         *
         * @param uid The id of the user to build the feed for
         * @param cb A callback that takes in the list of recipes retrieved from the system
         */
        fun getFeed(uid: String, page: Int = 0, cb: (recipes: MutableList<Recipe>?) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/users/$uid/feed?page=$page")
            var req = Request.Builder().get().url(endpoint).build()

            // Run the request
            runForListOf<Recipe>(req, Recipe, cb)
        }

        //=============================//
        // Helpers for making requests //
        //=============================//

        private inline fun <reified Type> runForListOf(request: Request, parseable: Parseable<Type>, crossinline cb: (result: MutableList<Type>?) -> Unit) {
            val client = OkHttpClient()
            async(UI) {

            }
            doAsync( exceptionHandler = { exception ->
                Log.e("RUN_FOR_LIST", "${exception.localizedMessage}")
            }) {
                val response = client.newCall(request).execute()
                val d = response.body()?.string()

                val res = JSONObject(d)

                val data = ArrayList<Type>()
                val jsonData = res.getJSONArray("data")
                for (idx in 0..jsonData.length()-1) {
                    val elem = jsonData.getJSONObject(idx)
                    val item = parseable.parse(elem.toString())
                    data.add(item)
                }
                Log.d("${request.url()}", "data: ${data.size}")
                uiThread {
                    cb(data)
                }
            }
        }

        private inline fun <reified Type> runForSingle(request: Request, parseable: Parseable<Type>, crossinline cb: (result: Type?) -> Unit) {
            doAsync(exceptionHandler = { exception ->
                Log.e("${request.url()}", "${exception.localizedMessage}")
            }) {
                val response = client.newCall(request).execute()
                val d = response.body()?.string()
                Log.d("${request.url()}", "Received: ${d}")
                val res = JSONObject(d)
                val data = parseable.parse(res.getString("data"))
                uiThread {
                    cb(if (res.getBoolean("success")) data else null)
                }
            }
        }

        private inline fun runForResult(request: Request, crossinline cb: (result: Boolean) -> Unit) {
            Log.d("${request.url()}", "Posting for result")
            doAsync(exceptionHandler = { exception ->
                Log.e("RUN_FOR_RESULT", "${exception.localizedMessage}")
            }) {
                val response = client.newCall(request).execute()
                val d = response.body()?.string()

                Log.d("${request.url()}", "Received: ${d}")

                val res = JSONObject(d)
                uiThread {
                    cb(res.getBoolean("success"))
                }
            }
        }

    }
}