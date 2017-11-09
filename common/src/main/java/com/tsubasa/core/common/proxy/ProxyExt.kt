package com.tsubasa.core.common.proxy

import java.lang.reflect.Proxy

/**
 * 动态代理的工具类
 * Created by tsubasa on 2017/11/7.
 */
object ProxyExt {
    fun <T> mockInterface(interfaceClz: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(interfaceClz.classLoader, arrayOf<Class<*>>(interfaceClz)) { _, _, _ -> "" } as T
    }
}