package com.tsubasa.core.model.exception

/**
 * 代表请求错误的
 * Created by tsubasa on 2017/11/12.
 */
class APIException(val code: Int, val errorMsg: CharSequence) : RuntimeException(errorMsg.toString())
