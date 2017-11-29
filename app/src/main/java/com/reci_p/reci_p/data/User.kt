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
open class User (
        @SerializedName("photo") var photo: String = "",
        @SerializedName("id") @PrimaryKey var id: String = UUID.randomUUID().toString(),
        @SerializedName("username") var userName: String = "",
        @SerializedName("displayname") var displayName: String = "",
        @SerializedName("following") var following: RealmList<String> = RealmList()
) : RealmObject(), Serializable