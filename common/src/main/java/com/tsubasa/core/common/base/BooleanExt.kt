package com.tsubasa.core.common.base

/**
 * Boolean的相关扩展
 * Created by tsubasa on 2017/11/7.
 */
sealed class BooleanExt<out T> constructor(val boolean: Boolean)


object Otherwise : BooleanExt<Nothing>(true)
class WithData<out T>(val data: T) : BooleanExt<T>(false)

inline fun <T> Boolean.yes(block: () -> T) = when {
    this -> {
        WithData(block())
    }
    else -> Otherwise
}

inline fun <T> Boolean.no(block: () -> T) = when {
    this -> Otherwise
    else -> {
        WithData(block())
    }
}

inline infix fun <T> BooleanExt<T>.otherwise(block: () -> T): T {
    return when (this) {
        is Otherwise -> block()
        is WithData<T> -> this.data
        else -> {
            throw  IllegalAccessException()
        }
    }
}

/**
 * exp: val bool = false
 * bool {
 *      // do something
 * }
 */
inline operator fun <T> Boolean?.invoke(block: () -> T) = this?.yes(block)


// -------------------------- 可为空的boolean
sealed class OptionalBooleanExt<out T> constructor(val boolean: Boolean?)

class OptionalWithData<out T>(val data: T) : OptionalBooleanExt<T>(false)
object OptionalOtherwise : OptionalBooleanExt<Nothing>(true)
object Null : OptionalBooleanExt<Nothing>(true)

inline fun <T> Boolean?.yesNoNull(block: () -> T) = when {
    this == null -> Null
    this -> OptionalWithData(block())
    else -> OptionalOtherwise
}

inline fun <T> Boolean?.noNoNull(block: () -> T) = when {
    this == null -> Null
    this -> OptionalOtherwise
    else -> OptionalWithData(block())
}

inline infix fun <T> OptionalBooleanExt<T>.otherwise(block: () -> T): OptionalBooleanExt<T> {
    return when (this) {
        is OptionalOtherwise -> OptionalWithData(block())
        else -> this
    }
}

inline infix fun <T> OptionalBooleanExt<T>.isNull(block: () -> T): T {
    return when (this) {
        is OptionalWithData -> this.data
        is Null -> block()
        else -> {
            throw  IllegalAccessException()
        }
    }
}

/**
 * 转化为boolean
 */
fun String?.trans2Boolean(default: Boolean = false): Boolean {
    (this == null).yes {
        return default
    }
    return when (this!!.toLowerCase().trim()) {
        "0", "false" -> false
        "1", "true" -> true
        else -> default
    }
}

/**
 * 对一个String按照boolean取反
 * 例如『0』变『1』，『true』变『false』
 */
fun String?.transNagate(default: String = "0"): String {
    (this == null).yes {
        return default
    }
    return when (this!!.toLowerCase().trim()) {
        "0" -> "1"
        "1" -> "0"
        "false" -> "true"
        "true" -> "false"
        else -> default
    }
}

