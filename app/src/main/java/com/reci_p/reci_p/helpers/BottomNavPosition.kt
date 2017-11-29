package com.reci_p.reci_p.helpers

import com.reci_p.reci_p.R
/**
 * Created by Sienna Mosher on 11/29/2017.
 */

enum class BottomNavPosition(val position: Int, val id: Int) {
    MY_RECPIES(0, R.id.recipes),
    MY_PROFILE(1, R.id.profile),
    RECIPE_FEED(2, R.id.feed),
}
