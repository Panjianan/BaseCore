package com.tsubasa.core.app

import android.app.Activity
import android.util.Log
import com.alibaba.android.arouter.utils.ClassUtils
import com.tsubasa.core.template.IInitializer
import com.tsubasa.core.template.IInitializerRoot
import com.tsubasa.core.template.IRouteRoot
import dagger.android.DispatchingAndroidInjector

/**
 * 保存子模块相关信息的路由仓库
 * Created by tsubasa on 2017/11/15.
 */
object Warehouse {

    val DOT = ""

    val SEPARATOR = "$$"

    val ROUTE_ROOT_PAKCAGE = "com.tsubasa.core.app.di.routes"

    val SUFFIX_ROOT = "Root"

    val SUFFIX_INITIALIZER = "Initializer"

    val SDK_NAME = "TsBaseCore"

    val group: Map<Class<*>, String> = mutableMapOf()

    val initializer: Map<String, Set<Class<IInitializer>>> = object : LinkedHashMap<String, Set<Class<IInitializer>>>() {
        override fun get(key: String): Set<Class<IInitializer>>? {
            return super.get(key) ?: mutableSetOf()
        }
    }

    val injector: Map<String, DispatchingAndroidInjector<Activity>> = mutableMapOf()

    internal fun initGroup() {
        val routerMap: Set<String> = ClassUtils.getFileNameByPackageName(getApplication(), ROUTE_ROOT_PAKCAGE)
        routerMap.filter { it.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_ROOT) }
                .forEach {
                    // This one of root elements, load root.
                    (Class.forName(it).getConstructor().newInstance() as IRouteRoot).loadInto(group)
                }

        Log.e("InjectRouter", "${group}")
    }

    internal fun iniModuleInitializer() {
        val routerMap: Set<String> = ClassUtils.getFileNameByPackageName(getApplication(), ROUTE_ROOT_PAKCAGE)
        routerMap.filter { it.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_INITIALIZER) }
                .forEach {
                    // This one of root elements, load root.
                    (Class.forName(it).getConstructor().newInstance() as IInitializerRoot).loadInto(initializer)
                }
        initializer.forEach {
            val moduleName = it.key
            it.value.forEach {
                val iInitializer = it.getConstructor().newInstance()
                iInitializer.init(getApplication())
                (iInitializer.provideAndroidInjector(Activity::class.java) as? DispatchingAndroidInjector<Activity>)?.let {
                    (injector as MutableMap).put(moduleName, it)
                }
            }
        }
        Log.e("InjectRouter", "${group}")
    }
}