package com.tsubasa.core.model.exception

/**
 * 和数据相关的异常
 * Created by tsubasa on 2017/11/12.
 */
class DataException(errorMsg: CharSequence? = "") : RuntimeException(errorMsg?.toString() ?: "")