package com.tsubasa.core.ui.component.recyclerview

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.tsubasa.core.ui.component.BaseComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.recyclerview.v7._RecyclerView
import org.jetbrains.anko.recyclerview.v7.recyclerView

/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.ui.component.recyclerview
 * <br> Description RecyclerView组件
 * <br> Version 1.0
 * <br> Author Tsubasa
 * <br> Creation 2017/11/11 14:57
 * <br> Mender Tsubasa
 * <br> Modification 2017/11/11 14:57
 * <br> Copyright Copyright © 2012 - 2017 Tsubasa.All Rights Reserved.
 */
class RecyclerViewComponent(private val initBlock: (RecyclerView.() -> RecyclerView.Adapter<*>)) : BaseComponent<_RecyclerView>() {

    override fun createContainer(context: AnkoContext<Any>): _RecyclerView = context.recyclerView() as _RecyclerView

    override fun createContent(parent: _RecyclerView) {
        val adapter = initBlock.invoke(parent)
        if (parent.layoutManager == null) {
            parent.layoutManager = LinearLayoutManager(parent.context)
        }
        parent.adapter = adapter
    }

    override fun bindData(lifecycleOwner: LifecycleOwner) {}
}