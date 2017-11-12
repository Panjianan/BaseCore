package com.tsubasa.core.model

/**
 * A generic class that holds a value with its loading status.
 * Created by tsubasa on 2017/11/12.
 */
class StatusResponse<out DATA>(val status: Status, val msg: CharSequence? = "", val data: DATA? = null)