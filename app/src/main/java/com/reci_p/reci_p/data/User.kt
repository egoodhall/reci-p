package com.reci_p.reci_p.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel

/**
 *
 *
 * Created by Eric Marshall on 11/11/17
 */
data class User (
    @SerializedName("photo") var photo: String,
    @SerializedName("id") var id: String,
    @SerializedName("username") var userName: String,
    @SerializedName("displayname") var displayName: String,
    @SerializedName("following") var following: List<String>
) : RealmModel