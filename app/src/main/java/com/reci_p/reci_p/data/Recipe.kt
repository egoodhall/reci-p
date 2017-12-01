package com.reci_p.reci_p.data

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.reci_p.reci_p.helpers.DataManager.Companion.gson
import com.reci_p.reci_p.interfaces.Parseable
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.json.JSONObject
import java.util.*

/**
 *
 *
 * Created by Eric Marshall on 11/11/17
 */
open class Recipe (
        @SerializedName("title") var title: String = "",
        @SerializedName("description") var description: String = "",
        @SerializedName("prep_time") var prepTime: String = "",
        @SerializedName("cook_time") var cookTime: String = "",
        @SerializedName("ingredients") var ingredients: RealmList<String> = RealmList(),
        @SerializedName("instructions") var instructions: RealmList<String> = RealmList(),
        @SerializedName("photo") var photo: String = "",
        @SerializedName("id") @PrimaryKey var id: String = UUID.randomUUID().toString(),
        @SerializedName("creator") var creator: String = "",
        @SerializedName("owner") var owner: String = "",
        @SerializedName("creation_ts") var creationTS: Long = Date().time,
        @SerializedName("modification_ts") var modifiedTS: Long = Date().time,
        @SerializedName("rating") var rating: Float = 0f
) : RealmObject() {

    constructor(newOwner: String, newId: String, toCopy: Recipe) : this() {
        title = toCopy.title
        description = toCopy.description
        prepTime = toCopy.prepTime
        cookTime = toCopy.cookTime
        ingredients = toCopy.ingredients
        instructions = toCopy.instructions
        photo = toCopy.photo
        id = newId
        creator = toCopy.creator
        owner = newOwner
        creationTS = toCopy.creationTS
        modifiedTS = toCopy.modifiedTS
        rating = toCopy.rating
    }

    companion object : Parseable<Recipe> {

        override fun json(recipe: Recipe): String {
            return gson.toJson(recipe)
        }

        override fun parse(json: String): Recipe {

            val obj = JSONObject(json)
            val recipe = Recipe()
            if (obj.getString("title") != null) recipe.title = obj.getString("title") else ""
            if (obj.getString("description") != null) recipe.description = obj.getString("description") else ""
            if (obj.getString("prep_time") != null) recipe.prepTime = obj.getString("prep_time") else ""
            if (obj.getString("cook_time") != null) recipe.cookTime = obj.getString("cook_time") else ""

            if (obj.has("ingredients")) {
                Log.d("BETWEEN", "A")
                Log.d("INGREDIENTS", "${obj["ingredients"]}")
                Log.d("BETWEEN", "B")
//                val ingr = JSONArray(obj.getString("ingredients"))
//                for (idx in 0..ingr.length()-1) {
//                    recipe.ingredients.add(ingr.getString(idx))
//                }
            }
            if (obj.has("instructions")) {
                Log.d("INSTRUCTIONS", "${obj["instructions"]}")
//                val instr = JSONArray(obj.getString("instructions"))
//                for (idx in 0..instr.length()-1) {
//                    recipe.ingredients.add(instr.getString(idx))
//                }
            }
            if (obj.getString("photo") != null) recipe.photo = obj.getString("photo") else ""
            if (obj.getString("id") != null) recipe.id = obj.getString("id") else ""
            if (obj.getString("creator") != null) recipe.creator = obj.getString("creator") else ""
            if (obj.getString("owner") != null) recipe.owner = obj.getString("owner") else ""
            recipe.creationTS = obj.getLong("creation_ts")
            recipe.modifiedTS = obj.getLong("modification_ts")
            try {
                recipe.rating = obj.getDouble("rating").toFloat()
            } catch (exception: Exception) {
                Log.e("RECIPE", "${exception.localizedMessage}")
            }
            return recipe
        }

    }

    override fun toString(): String {
        return String.format("Recipe(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                title,
                description,
                prepTime,
                cookTime,
                ingredients.toString(),
                instructions.toString(),
                photo,
                id,
                creator,
                owner,
                creationTS.toString(),
                modifiedTS.toString(),
                rating.toString())
    }
}
