@file:Suppress("unused")

package com.tsubasa.core.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * app组件
 * Created by tsubasa on 2017/11/4.
 */

class CoreActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    private var onActivityPaused: ((Activity) -> Unit)? = null

    override fun onActivityPaused(activity: Activity?) {
        activity?.let { onActivityPaused?.invoke(it) }
    }

    fun onActivityPaused(action: ((Activity) -> Unit)) {
        onActivityPaused = action
    }


    private var onActivityResumed: ((Activity) -> Unit)? = null

    override fun onActivityResumed(activity: Activity?) {
        activity?.let { onActivityResumed?.invoke(it) }
    }

    fun onActivityResumed(action: ((Activity) -> Unit)) {
        onActivityResumed = action
    }


    private var onActivityStarted: ((Activity) -> Unit)? = null

    override fun onActivityStarted(activity: Activity?) {
        activity?.let { onActivityStarted?.invoke(it) }
    }

    fun onActivityStarted(action: ((Activity) -> Unit)) {
        onActivityStarted = action
    }


    private var onActivityDestroyed: ((Activity) -> Unit)? = null

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.let { onActivityDestroyed?.invoke(it) }
    }

    fun onActivityDestroyed(action: ((Activity) -> Unit)) {
        onActivityDestroyed = action
    }


    private var onActivitySaveInstanceState: ((Activity, Bundle) -> Unit)? = null

    override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {
        if ((activity != null).and(bundle != null)) {
            onActivitySaveInstanceState?.invoke(activity!!, bundle!!)
        }
    }

    fun onActivitySaveInstanceState(action: ((Activity, Bundle) -> Unit)) {
        onActivitySaveInstanceState = action
    }

    private var onActivityStopped: ((Activity) -> Unit)? = null

    override fun onActivityStopped(activity: Activity?) {
        activity?.let { onActivityStopped?.invoke(it) }
    }

    fun onActivityStopped(action: ((Activity) -> Unit)) {
        onActivityStopped = action
    }


    private var onActivityCreated: ((Activity, Bundle?) -> Unit)? = null

    override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {
        activity?.let { onActivityCreated?.invoke(activity, bundle) }
    }

    fun onActivityCreated(action: ((Activity, Bundle?) -> Unit)) {
        onActivityCreated = action
    }

}

fun Application.activityLifecycleCallbacks(init: CoreActivityLifecycleCallbacks.() -> Unit) {
    val callbacks = CoreActivityLifecycleCallbacks()
    init.invoke(callbacks)
    registerActivityLifecycleCallbacks(callbacks)
}


internal val activityList: MutableList<Activity> = mutableListOf()
@SuppressLint("StaticFieldLeak")
internal var activityCurrent: Activity? = null

fun Application.initManager(init: Application.() -> Unit) {
    activityList.clear()
    activityLifecycleCallbacks {
        onActivityCreated { activity, _ ->
            activityList.add(activity)
            activityCurrent = activity
        }

        onActivityDestroyed { activity ->
            activityList.remove(activity)
        }
    }

    init.invoke(this)
}

fun getCurrentActivity(): Activity? {
    return activityCurrent
}