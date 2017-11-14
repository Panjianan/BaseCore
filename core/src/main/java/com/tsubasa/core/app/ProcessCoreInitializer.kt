package com.tsubasa.core.app

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.support.annotation.RestrictTo
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.tsubasa.core.di.DaggerAppComponent
import com.tsubasa.core.di.Warehouse

/**
 * 仿照google的套路，用这个ContentProvider执行初始化操作，避免侵入Application
 * Internal class to initialize Core Framework.
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class ProcessCoreInitializer : ContentProvider() {
    override fun onCreate(): Boolean {
        (context.applicationContext as? Application)?.initManager {
            ApplicationWrap.createAppWrap(this)
            DaggerAppComponent.builder().application(this).build().inject(getApplication())
            ARouter.init(this)
            Log.e("ProcessCoreInitializer", "init success")
            Warehouse.initGroup()
        }
        return true
    }

    override fun query(uri: Uri, strings: Array<String>?, s: String?, strings1: Array<String>?,
                       s1: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int {
        return 0
    }
}