package com.reci_p.reci_p.helpers



/**
 * Created by SiennaMosher on 11/29/2017.
 */


object BottomNavHelper {

    fun findPositionById(id: Int): BottomNavPosition = when(id) {
        BottomNavPosition.MY_RECPIES.id -> BottomNavPosition.MY_RECPIES
        BottomNavPosition.MY_PROFILE.id -> BottomNavPosition.MY_PROFILE
        BottomNavPosition.RECIPE_FEED.id -> BottomNavPosition.RECIPE_FEED
        else -> BottomNavPosition.MY_PROFILE
    }
}