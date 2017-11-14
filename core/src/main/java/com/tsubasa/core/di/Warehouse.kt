package com.tsubasa.core.di

import android.util.Log
import com.alibaba.android.arouter.utils.ClassUtils
import com.tsubasa.core.app.getApplication
import com.tsubasa.core.template.IRouteRoot

/**
 * 保存class的路由仓库
 * Created by tsubasa on 2017/11/15.
 */
object Warehouse {

    val DOT = "."

    val SEPARATOR = "$$"

    val ROUTE_ROOT_PAKCAGE = "com.tsubasa.core.app.di.routes"

    val SUFFIX_ROOT = "Root"

    val SDK_NAME = "TsBaseCore"

    val group: Map<Class<*>, String> = mutableMapOf()

    fun initGroup() {
        val routerMap: Set<String> = ClassUtils.getFileNameByPackageName(getApplication(), ROUTE_ROOT_PAKCAGE)
        routerMap.filter { it.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_ROOT) }
                .forEach {
                    // This one of root elements, load root.
                    (Class.forName(it).getConstructor().newInstance() as IRouteRoot).loadInto(Warehouse.group)
                }

        Log.e("InjectRouter", "${Warehouse.group}")
    }

}