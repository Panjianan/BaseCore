package com.tsubasa.core.ui.component

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext

/**
 * 组件基类
 * Created by tsubasa on 2017/11/4.
 */
abstract class BaseComponent<T : ViewGroup> : AnkoComponent<Context> {

    /**
     * 在别的组件中，通过createContent，可以创建多个控件，
     * 但是一个组件只会持有第一个创建的控件
     */
    var container: ViewGroup? = null
        private set(value) {
            if (field == null) {
                field = value
            }
        }

    protected var owner: LifecycleOwner? = null

    open fun bind(owner: LifecycleOwner) {
    }

    open fun unBind() {

    }

    /**
     * 提供容器的方法
     */
    abstract protected fun AnkoContext<Any>.createContainer(): T

    /**
     * 创建组件内的内容
     */
    abstract protected fun createContent(parent: T)

    /**
     * AnkoComponent默认要实现的方法
     */
    override final fun createView(ui: AnkoContext<Context>): View {
        return ui.createContainer().apply { let { createContentInContainer(it) } }
    }

    /**
     * 创建包含容器的整个布局
     */
    fun createComponent(parent: ViewGroup): ViewGroup {
        if (container != null) {
            throw Exception("zhe view has been created， you can reuse the view or recreate a component")
        }
        return with(AnkoContext.Companion.createDelegate(parent)) {
            createContainer().apply { let { createContentInContainer(it) } }
        }
    }

    private fun createContentInContainer(parent: T) {
        createContent(parent)
        container = parent
        (parent.context as? LifecycleOwner)?.let {
            this@BaseComponent.owner = it
            bind(it)
        }
    }

}