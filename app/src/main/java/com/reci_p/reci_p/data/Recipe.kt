package com.reci_p.reci_p.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel

/**
 *
 *
 * Created by Eric Marshall on 11/11/17
 */
data class Recipe (
    @SerializedName("ingredients") var ingredients: List<String>,
    @SerializedName("title") var title: String,
    @SerializedName("description") var description: String,
    @SerializedName("prep_time") var prepTime: String,
    @SerializedName("cook_time") var cookTime: String,
    @SerializedName("instructions") var instructions: List<String>,
    @SerializedName("photo") var photo: String,
    @SerializedName("id") var id: String,
    @SerializedName("creator") val creator: String,
    @SerializedName("owner") var owner: String,
    @SerializedName("creation_ts") var creationTS: Long,
    @SerializedName("creation_ts") var modifiedTS: Long,
    @SerializedName("rating") var rating: Float
) : RealmModel