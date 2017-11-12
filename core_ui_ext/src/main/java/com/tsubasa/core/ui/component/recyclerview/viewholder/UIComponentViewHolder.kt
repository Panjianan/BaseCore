package com.tsubasa.core.ui.component.recyclerview.viewholder

import android.arch.lifecycle.Observer
import android.content.Context
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.tsubasa.core.ui.component.BaseComponent
import org.jetbrains.anko.*

/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.ui.component.recyclerview.viewholder
 * <br> Description ViewHolder
 * <br> Version 1.0
 * <br> Author Tsubasa
 * <br> Creation 2017/11/11 15:31
 * <br> Mender Tsubasa
 * <br> Modification 2017/11/11 15:31
 * <br> Copyright Copyright © 2012 - 2017 Tsubasa.All Rights Reserved.
 */
open class UIComponentViewHolder<DATA, out T : BaseComponent<*>>
(open val uiComponent: T, open val observer: Observer<DATA>, context: Context) : BaseViewHolder(
        context.UI {
            uiComponent.createContainer(this).let {
                uiComponent.createComponent(it)
            }
        }.view
)