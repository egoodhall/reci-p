package com.reci_p.reci_p.data

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
open class User (
        @SerializedName("photo") var photo: String = "",
        @SerializedName("id") @PrimaryKey var id: String = UUID.randomUUID().toString(),
        @SerializedName("username") var userName: String = "",
        @SerializedName("displayname") var displayName: String = "",
        @SerializedName("following") var following: RealmList<String> = RealmList()
) : RealmObject() {
    companion object : Parseable<User> {
        override fun json(user: User): String {
            return gson.toJson(user)
        }

        override fun parse(json: String): User {
            val obj = JSONObject(json)

            return User(
                    obj.getString("photo"),
                    obj.getString("id"),
                    obj.getString("username"),
                    obj.getString("displayname")
            )
        }

    }
}