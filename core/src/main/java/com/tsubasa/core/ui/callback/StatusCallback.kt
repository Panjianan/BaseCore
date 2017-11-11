package com.tsubasa.core.ui.callback

/**
 * 状态回调
 * Created by tsubasa on 2017/11/4.
 */


enum class Status {
    STATUS_SUCCESS,
    STATUS_SUCCESS_NO_MORE,
    STATUS_ERROR,
    STATUS_EMPTY,
    STATUS_LOADING
}

data class StatusData(val status: Status, val msg: String? = "")
