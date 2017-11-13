package com.tsubasa.core.app

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.app
 * <br> Description Global executor pools for the whole application.
 * <br> Version 1.0
 * <br> Author Tsubasa
 * <br> Creation 2017/11/13 10:59
 * <br> Mender Tsubasa
 * <br> Modification 2017/11/13 10:59
 * <br> Copyright Copyright © 2012 - 2017 ZhongWangXinTong.All Rights Reserved.
 */
object AppExecutors {

    val diskIO: Executor = Executors.newSingleThreadExecutor()
    val networkIO: Executor = Executors.newFixedThreadPool(3)
    val mainThread: Executor = MainThreadExecutor()

    internal class MainThreadExecutor : Executor {

        private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

        override fun execute(p0: Runnable?) {
            mainThreadHandler.post(p0)
        }

    }
}

