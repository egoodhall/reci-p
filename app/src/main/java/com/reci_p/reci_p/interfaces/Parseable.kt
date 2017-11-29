package com.reci_p.reci_p.interfaces

/**
 *
 *
 * Created by Eric Marshall on 11/28/17
 */
interface Parseable<T> {
    fun parse(s: String) : T
    fun json(t: T) : String
}