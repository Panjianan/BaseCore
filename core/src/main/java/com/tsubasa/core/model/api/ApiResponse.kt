package com.tsubasa.core.model.api

import com.tsubasa.core.common.base.yes
import java.util.regex.Pattern

/**
 * Common class used by API responses.
 * Created by tsubasa on 2017/11/12.
 */
open class ApiResponse<DATA> {

    private companion object {
        val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        val NEXT_LINK = "next"
    }

    val code: Int
    val errorMessage: String?
    val body: DATA?
    val links: Map<String, String> = mutableMapOf()

    constructor(code: Int, errorMessage: String?, body: DATA? = null) {
        this.code = code
        this.errorMessage = errorMessage
        this.body = body
    }

    constructor(error: Throwable) : this(500, error.message)

    fun isSuccessful(): Boolean {
        return IntRange(200, 299).contains(code)
    }

    fun getNextPage(): Int? {
        val next = links.get(NEXT_LINK)
        (next == null).yes { return null }

        val matcher = PAGE_PATTERN.matcher(next)
        matcher.find().not().or(matcher.groupCount() != 1).yes {
            return null
        }

        return try {
            matcher.group(1).toInt()
        } catch (e: Exception) {
            null
        }
    }
}