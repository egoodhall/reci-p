package com.reci_p.reci_p.helpers

import android.content.Context
import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.reci_p.reci_p.R
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.data.User
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jetbrains.anko.coroutines.experimental.bg
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
        val parser = JsonParser()
        lateinit var resources: Resources

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
        fun createUser(user: User, cb: (success: Boolean) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/users")
            val req = Request.Builder()
                    .post(RequestBody.create(json, gson.toJson(user)))
                    .url(endpoint).build()

            async(UI) {
                val res = bg {
                    val response = client.newCall(req).execute()
                    parser.parse(response.body()?.string()).asJsonObject
                }.await()
                cb(res.get("success").asBoolean)
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

            async(UI) {
                val res = bg {
                    val response = client.newCall(req).execute()
                    parser.parse(response.body()?.string()).asJsonObject
                }.await()
                cb(res.get("success").asBoolean)
            }
        }

        /**
         * Stop following a user
         *
         * @param consumer The user that will stop following the producer
         * @param producer The user that will stop being followed by the consumer
         * @param cb A callback that receives whether or not the query was successful
         */
        fun unfollow(consumer: String, producer: String, cb: (success: Boolean) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/users/$consumer/follow/$producer")
            var req = Request.Builder()
                    .delete()
                    .url(endpoint).build()

            async(UI) {
                val res = bg {
                    val response = client.newCall(req).execute()
                    parser.parse(response.body()?.string()).asJsonObject
                }.await()
                cb(res.get("success").asBoolean)
            }
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
        fun searchUsers(own: String, query: String, page: Int = 0, cb: (users: List<User>) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url)
                .plus("/users/search/${URLEncoder.encode(query, "UTF-8")}&ignore=$own&page=$page")
            var req = Request.Builder()
                    .get()
                    .url(endpoint).build()

            async(UI) {
                val res = bg {
                    val response = client.newCall(req).execute()
                    parser.parse(response.body()?.string()).asJsonObject
                }.await()

                val data = ArrayList<User>()
                res.getAsJsonArray("data").forEach { elem ->
                    gson.fromJson(elem, User::class.java)
                }

                cb(data)
            }
        }

        /**
         * Get a user whose id matches the desired one
         *
         * @param uid The id of the user to find in the database
         * @param cb A callback function for handling the returned user
         */
        fun getUser(uid: String, cb: (user: User) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/users/$uid")
            var req = Request.Builder()
                    .get()
                    .url(endpoint).build()

            async(UI) {
                val res = bg {
                    val response = client.newCall(req).execute()
                    parser.parse(response.body()?.string()).asJsonObject
                }.await()

                val data = gson.fromJson(res, User::class.java)

                cb(data)
            }
        }

        /**
         * Create a recipe in the backend
         *
         * @param recipe A populated recipe object
         * @param cb A callback that receives whether or not the query was successful
         */
        fun createRecipe(recipe: Recipe, cb: (success: Boolean) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/recipes")
            var req = Request.Builder()
                    .post(RequestBody.create(json, gson.toJson(recipe)))
                    .url(endpoint).build()

            async(UI) {
                val res = bg {
                    val response = client.newCall(req).execute()
                    parser.parse(response.body()?.string()).asJsonObject
                }.await()
                cb(res.get("success").asBoolean)
            }
        }

        /**
         * Updates the recipe in the backend whose id matches the one passed to the function. The
         * other values in the recipe are updated
         *
         * @param recipe A populated recipe object to update the values for
         * @param cb A callback that receives whether or not the query was successful
         */
        fun updateRecipe(recipe: Recipe, cb: (success: Boolean) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/recipes")
            var req = Request.Builder()
                    .put(RequestBody.create(json, gson.toJson(recipe)))
                    .url(endpoint).build()

            async(UI) {
                val res = bg {
                    val response = client.newCall(req).execute()
                    parser.parse(response.body()?.string()).asJsonObject
                }.await()
                cb(res.get("success").asBoolean)
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

            async(UI) {
                val res: Deferred<JSONObject> = bg {
                    val response = client.newCall(req).execute()
                    JSONObject(response.body()?.string())
                }
                cb(res.await().getBoolean("success"))
            }
        }

        /**
         * Get all recipes for a given user
         *
         * @param uid The id of the user whose recipes should be retrieved
         * @param cb A callback that takes in the list of recipes that the query returns
         */
        fun getRecipesForUser(uid: String, page: Int = 0, cb: (recipes: List<Recipe>) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/users/$uid/recipes?page=$page")
            var req = Request.Builder()
                    .get()
                    .url(endpoint).build()

            async(UI) {
                val res = bg {
                    val response = client.newCall(req).execute()
                    JsonParser().parse(response.body()?.string()).asJsonObject
                }.await()

                val data = ArrayList<Recipe>()
                res.getAsJsonArray("data").forEach { elem ->
                    gson.fromJson(elem, Recipe::class.java)
                }

                cb(data)
            }
        }

        /**
         * Retrieves a recipe with the given id
         *
         * @param id The id of the recipe to retrieve
         * @param cb A callback that takes in the retrieved recipe
         */
        fun getRecipe(id: String, cb: (recipe: Recipe) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/recipes/$id")
            var req = Request.Builder()
                    .get()
                    .url(endpoint).build()

            async(UI) {
                val res = bg {
                    val response = client.newCall(req).execute()
                    JsonParser().parse(response.body()?.string()).asJsonObject
                }.await()

                val data = gson.fromJson(res, Recipe::class.java)

                cb(data)
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
        fun getFeed(uid: String, page: Int = 0, cb: (recipes: List<Recipe>) -> Unit) {
            val endpoint = resources.getString(R.string.api_base_url).plus("/users/$uid/feed?page=$page")
            var req = Request.Builder()
                    .get()
                    .url(endpoint).build()

            async(UI) {
                val res = bg {
                    val response = client.newCall(req).execute()
                    JsonParser().parse(response.body()?.string()).asJsonObject
                }.await()

                val data = ArrayList<Recipe>()
                res.getAsJsonArray("data").forEach { elem ->
                    gson.fromJson(elem, Recipe::class.java)
                }

                cb(data)
            }
        }
    }
}