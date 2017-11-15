package com.tsubasa.core.app

import android.app.Activity
import android.app.Application
import android.content.ContextWrapper
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * app的代理
 * Created by tsubasa on 2017/11/14.
 */
class ApplicationWrap private constructor(application: Application) : ContextWrapper(application) {

    @Inject
    lateinit var dispatchingAndroidInject: DispatchingAndroidInjector<Activity>


    companion object {

        lateinit var instance: ApplicationWrap

        internal fun createAppWrap(application: Application) {
            instance = ApplicationWrap(application)
        }
    }


    fun inject(activity: Activity) {
        if (activity is HasSupportFragmentInjector) {
            // TODO
            dispatchingAndroidInject.inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object : android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentCreated(fm: android.support.v4.app.FragmentManager?, f: Fragment?, savedInstanceState: Bundle?) {
                    // TODO
                    AndroidSupportInjection.inject(f)
                }
            }, true)
        }
    }

}