package com.reci_p.reci_p.data

/**
 *
 *
 * Created by Eric Marshall on 11/11/17
 */
data class Recipe (
        var ingredients: List<String>,
        var specialtyTools: List<String>,
        var title: String,
        var description: String,
        var prepTime: String,
        var cookTime: String,
        var instructions: List<String>,
        var photo: String,
        var id: String,
        val creator: String,
        var owner: String,
        var creationDate: String,
        var modifiedDate: String,
        var rating: Float
)