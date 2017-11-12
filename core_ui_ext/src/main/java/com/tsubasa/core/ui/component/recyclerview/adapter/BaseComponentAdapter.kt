package com.tsubasa.core.ui.component.recyclerview.adapter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tsubasa.core.common.base.yes
import com.tsubasa.core.ui.component.BaseComponent
import com.tsubasa.core.ui.component.recyclerview.viewholder.UIComponentViewHolder

/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.ui.component.recyclerview.viewholder
 * <br> Description 适配器
 * <br> Version 1.0
 * <br> Author Tsubasa
 * <br> Creation 2017/11/11 15:56
 * <br> Mender Tsubasa
 * <br> Modification 2017/11/11 15:56
 * <br> Copyright Copyright © 2012 - 2017 Tsubasa.All Rights Reserved.
 */
class ComponentAdapter<DATA, UIComponent : BaseComponent<*>>(
        createHolderBlock: (() -> UIComponent),
        createObserverBlock: (UIComponent.() -> Observer<DATA>)) :
        RecyclerView.Adapter<UIComponentViewHolder<DATA, UIComponent>>() {

    private val delegate: BaseComponentAdapter<DATA, UIComponent> = BaseComponentAdapter(createHolderBlock, createObserverBlock)

    var data: List<DATA>
        get() = delegate.data
        set(value) {
            delegate.setNewData(value)
            notifySizeChange()
        }

    private val dataRange = IntRange(0, data.size.minus(1))

    val size: LiveData<Int> = MutableLiveData<Int>()

    private fun notifySizeChange() {
        (size as MutableLiveData<Int>).value = data.size
    }

    fun addData(data: DATA, index: Int = 0) {
        dataRange.contains(index).yes {
            delegate.addData(index, data)
            notifySizeChange()
        }
    }

    fun addDatas(dataList: List<DATA>, index: Int = 0) {
        dataRange.contains(index).yes {
            delegate.addData(index, dataList)
            notifySizeChange()
        }
    }

    fun removeData(dataList: List<DATA>) {
        dataList.forEach {
            removeData(it)
        }
    }

    fun removeData(data: DATA) {
        removeData(this.data.indexOf(data))
    }

    fun removeData(index: Int) {
        dataRange.contains(index).yes {
            delegate.remove(index)
            notifySizeChange()
        }
    }


    override fun getItemId(position: Int): Long {
        return delegate.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return delegate.getItemViewType(position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        delegate.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        delegate.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: UIComponentViewHolder<DATA, UIComponent>?, position: Int, payloads: MutableList<Any>?) {
        delegate.onBindViewHolder(holder, position, payloads)
    }

    override fun onFailedToRecycleView(holder: UIComponentViewHolder<DATA, UIComponent>?): Boolean {
        return delegate.onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: UIComponentViewHolder<DATA, UIComponent>?) {
        delegate.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: UIComponentViewHolder<DATA, UIComponent>?) {
        delegate.onViewDetachedFromWindow(holder)
    }

    override fun onViewRecycled(holder: UIComponentViewHolder<DATA, UIComponent>?) {
        delegate.onViewRecycled(holder)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver?) {
        delegate.registerAdapterDataObserver(observer)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        delegate.setHasStableIds(hasStableIds)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver?) {
        delegate.unregisterAdapterDataObserver(observer)
    }

    override fun onBindViewHolder(holder: UIComponentViewHolder<DATA, UIComponent>?, position: Int) {
        delegate.onBindViewHolder(holder, position)
    }

    override fun getItemCount(): Int = size.value ?: 0

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UIComponentViewHolder<DATA, UIComponent> {
        return delegate.createViewHolder(parent, viewType)
    }

}

internal class BaseComponentAdapter<DATA, UIComponent : BaseComponent<*>>(
        private val createItemUIBlock: (() -> UIComponent),
        private val createObserverBlock: (UIComponent.() -> Observer<DATA>)) :
        BaseQuickAdapter<DATA, UIComponentViewHolder<DATA, UIComponent>>(0) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): UIComponentViewHolder<DATA, UIComponent> {
        val uiComponent = createItemUIBlock.invoke()
        val observer = createObserverBlock.invoke(uiComponent)
        return UIComponentViewHolder(uiComponent, observer, parent!!.context)
    }

    override fun convert(helper: UIComponentViewHolder<DATA, UIComponent>?, item: DATA) {
        helper?.observer?.onChanged(item)
    }
}

fun <DATA, UIComponent : BaseComponent<*>> createAdapter(
        initUI: (() -> UIComponent),
        initObserver: (UIComponent.() -> Observer<DATA>) = {
            @Suppress("UNCHECKED_CAST")
            this as Observer<DATA>
        }):
        ComponentAdapter<DATA, UIComponent> {
    return ComponentAdapter(initUI, initObserver)
}
