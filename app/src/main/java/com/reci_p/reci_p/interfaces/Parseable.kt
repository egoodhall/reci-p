package com.reci_p.reci_p.interfaces

/**
 *
 *
 * Created by Eric Marshall on 11/28/17
 */
interface Parseable<T> : Deserializable<T>, Serializable<T> {

}

interface Deserializable<out T> {
    fun parse(s: String) : T
}

interface Serializable<in T> {
    fun json(t: T) : String
}