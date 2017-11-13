package com.tsubasa.core.util.network

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.tsubasa.core.app.AppExecutors
import com.tsubasa.core.common.base.otherwise
import com.tsubasa.core.common.base.yes
import com.tsubasa.core.model.Resource
import com.tsubasa.core.model.Status
import com.tsubasa.core.model.api.ApiResponse
import com.tsubasa.core.util.lifecycle.NullLiveData


/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.util.network
 * <br> Description 用来处理数据处理状态的
 * <br> Version 1.0
 * <br> Author Administrator
 * <br> Creation 2017/11/13 9:58
 * <br> Mender Administrator
 * <br> Modification 2017/11/13 9:58
 * <br> Copyright Copyright © 2012 - 2017 ZhongWangXinTong.All Rights Reserved.
 */
abstract class NetworkBoundResource<ResultType, RequestType>(
        private val saveCallResult: ((RequestType) -> Boolean)? = null,
        private val loadFromDb: (() -> LiveData<ResultType>)? = null,
        private val shouldFetch: ((ResultType?) -> Boolean)? = null
) {

    var loadingMsg: CharSequence? = ""

    var successMsg: CharSequence? = ""

    private val result: MediatorLiveData<Resource<ResultType>> = MediatorLiveData()

    @Suppress("unused")
    val asLiveData: LiveData<Resource<ResultType>>
        get() = result

    init {
        // 先去数据库拿数据
        @Suppress("LeakingThis")
        loadFromDb().let { dbSource ->
            // 结果数据观察数据库结果
            result.addSource(dbSource) { data ->
                // 先取消观察
                result.removeSource(dbSource)
                // 判断是否需要从网络获取
                shouldFetch(data).yes {
                    // 观察网络返回的数据
                    fetchFromNetWork(dbSource)
                }.otherwise {
                    // 可以从数据库拿数据，重新观察数据库的数据
                    result.addSource(dbSource) { newData ->
                        setValue(Resource(Status.STATUS_SUCCESS, successMsg, newData))
                    }
                }
            }
        }
    }

    private fun setValue(newValue: Resource<ResultType>) {
        (result.value != newValue).yes {
            result.value = newValue
        }
    }

    private fun fetchFromNetWork(dbSource: LiveData<ResultType>) {
        // 调用具体的请求实现
        val apiResponse = createCall()
        // 观察数据变化，返回正在请求中的状态
        result.addSource(dbSource) { setValue(Resource(Status.STATUS_LOADING, loadingMsg, it)) }
        result.addSource(apiResponse) { response ->
            // 去掉网络和数据库的observer
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            // 判断请求是否成功
            (response?.isSuccessful() ?: false).yes {
                AppExecutors.diskIO.execute {
                    // 在io线程处理数据，从请求结果中拿出数据
                    val requestType = response?.processResponse()
                    // 判断是否有持久化的操作
                    val hasSaveDB = if (requestType == null) {
                        false
                    } else {
                        saveCallResult(requestType)
                    }
                    // 在主线程返回结果
                    AppExecutors.mainThread.execute {
                        if (hasSaveDB) {
                            // 有持久化操作，直接观察数据库的数据
                            result.addSource(loadFromDb()) {
                                setValue(Resource(Status.STATUS_SUCCESS, successMsg, it))
                            }
                        } else {
                            // 否则直接设置结果数据
                            result.value = Resource(Status.STATUS_SUCCESS, successMsg, requestType.processResultType())
                        }
                    }
                }
            }.otherwise {
                result.addSource(dbSource) {
                    setValue(Resource(Status.STATUS_ERROR, response?.errorMessage, it))
                }
            }
        }
    }

    /**
     * 由于有时候请求回来的数据和本地保存的数据是不一样的，这个方法是用来
     */
    @Suppress("MemberVisibilityCanPrivate")
    open fun ApiResponse<RequestType>.processResponse(): RequestType? {
        return body
    }

    /**
     * 网络请求数据转化为返回的数据
     */
    open fun RequestType?.processResultType(): ResultType? {
        @Suppress("UNCHECKED_CAST")
        return this as? ResultType
    }

    /**
     * 具体的网络请求实现
     */
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    /**
     * 数据持久化的操作
     * @return 是否有持久化的操作，有得话就会观察持久化的数据，否则直接观察网络请求返回的数据
     */
    @WorkerThread
    private fun saveCallResult(item: RequestType): Boolean {
        return (saveCallResult == null).or(loadFromDb == null).yes {
            false
        }.otherwise {
            saveCallResult!!.invoke(item)
        }
    }

    /**
     * 从数据库拿数据
     */
    @MainThread
    private fun loadFromDb(): LiveData<ResultType> = loadFromDb?.invoke() ?: NullLiveData()

    /**
     * 是否需要从网络获取数据
     * @return true:需要 false:不需要
     */
    @MainThread
    private fun shouldFetch(data: ResultType?): Boolean {
        return (saveCallResult == null).or(loadFromDb == null).yes {
            true
        }.otherwise {
            shouldFetch?.invoke(data) ?: (data == null)
        }
    }
}