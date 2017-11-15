package com.reci_p.reci_p.data

/**
 *
 *
 * Created by Eric Marshall on 11/11/17
 */
data class User (
    var photo: String,
    var uid: String,
    var userName: String,
    var displayName: String,
    var following: ArrayList<String>
) {
    constructor() : this(
            photo="",
            uid="",
            userName="",
            displayName="",
            following=ArrayList()
    ) {}
}