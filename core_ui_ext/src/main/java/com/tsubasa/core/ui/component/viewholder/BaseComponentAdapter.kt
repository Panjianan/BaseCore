package com.tsubasa.core.ui.component.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tsubasa.core.ui.component.BaseComponent
import com.tsubasa.core.ui.component.recyclerview.RecyclerViewComponent

/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.ui.component.viewholder
 * <br> Description 适配器
 * <br> Version 1.0
 * <br> Author Tsubasa
 * <br> Creation 2017/11/11 15:56
 * <br> Mender Tsubasa
 * <br> Modification 2017/11/11 15:56
 * <br> Copyright Copyright © 2012 - 2017 Tsubasa.All Rights Reserved.
 */
class BaseComponentAdapter<DATA, UIComponent : BaseComponent<*>>(private val createHolderBlock: (() -> UIComponentViewHolder<DATA, UIComponent>)) : BaseQuickAdapter<DATA, UIComponentViewHolder<DATA, UIComponent>>(0) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): UIComponentViewHolder<DATA, UIComponent> {
        return createHolderBlock.invoke()
    }

    override fun convert(helper: UIComponentViewHolder<DATA, UIComponent>?, item: DATA) {
        helper?.observer?.onChanged(item)
        Log.e("convert", "height = ${helper?.itemView?.height}   width = ${helper?.itemView?.width}")
    }
}

fun <DATA, UIComponent : BaseComponent<*>> Context.createAdapter(block: (() -> UIComponent)): BaseComponentAdapter<DATA, UIComponent> {
    return BaseComponentAdapter {
        createViewHolder<DATA, UIComponent>(block.invoke()).apply {
            val childCount = (itemView as ViewGroup).childCount
        }
    }
}
