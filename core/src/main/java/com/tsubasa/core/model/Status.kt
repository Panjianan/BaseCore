package com.tsubasa.core.model

/**
 * Status of a resource that is provided to the UI.
 * <p>
 * These are usually created by the Repository classes where they return
 * {@code LiveData<Resource<T>>} to pass back the latest data to the UI with its fetch status.
 * Created by tsubasa on 2017/11/12.
 */
enum class Status {
    STATUS_SUCCESS,
    STATUS_SUCCESS_NO_MORE,
    STATUS_ERROR,
    STATUS_EMPTY,
    STATUS_LOADING
}