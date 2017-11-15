package com.tsubasa.core.template

import android.content.Context
import dagger.android.AndroidInjector

interface IInitializer {
    fun init(context: Context)

    fun <T> provideAndroidInjector(clazz: Class<T>): AndroidInjector<T>? = null
}
