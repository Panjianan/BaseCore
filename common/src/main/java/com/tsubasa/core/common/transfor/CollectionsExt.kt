package com.tsubasa.core.common.transfor

import com.tsubasa.core.common.base.yes

/**
 * 集合的相关扩展
 * Created by tsubasa on 2017/11/7.
 */

/**
 * 集合转字符串
 */
fun <T> Collection<T>?.trans2Str(separator: String = ",", toStringBlock: ((T) -> String?)): String {
    (this?.isEmpty() ?: true).yes {
        return ""
    }
    return StringBuilder().apply {
        this@trans2Str!!.forEachIndexed { index, t ->
            append(toStringBlock(t))
            (index != size.minus(1)).yes { append(separator) }
        }
    }.toString()
}

/**
 * 字符串转list
 */
fun String?.trans2List(separator: String = ","): List<String> {
    val str = this ?: ""
    str.trim().isEmpty().yes {
        return emptyList()
    }
    return str.split(separator).filterNot { it.isEmpty() }.toList()
}