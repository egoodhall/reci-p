package com.reci_p.reci_p.helpers

import android.support.v7.util.DiffUtil
import com.reci_p.reci_p.data.User

/**
 *
 *
 * Created by Eric Marshall on 12/2/17
 */
class UserDiffCb(val old: List<User>, val new: List<User>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return new[newItemPosition].id == old[oldItemPosition].id
    }
    override fun getOldListSize(): Int {
        return old.size
    }
    override fun getNewListSize(): Int {
        return new.size
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].id == new[newItemPosition].id
    }
}