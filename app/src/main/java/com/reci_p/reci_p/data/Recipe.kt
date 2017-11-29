package com.reci_p.reci_p.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
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
        @SerializedName("creation_ts") var creationTS: Long = -1,
        @SerializedName("creation_ts") var modifiedTS: Long = -1,
        @SerializedName("rating") var rating: Float = 0f
) : RealmObject(), Serializable