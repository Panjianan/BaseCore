package com.example.tsubasa.demo

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.tsubasa.core.ui.callback.Status
import com.tsubasa.core.ui.component.BaseComponent
import com.tsubasa.core.ui.component.statuslayout.StatusUIComponentFactory
import com.tsubasa.core.ui.component.statuslayout.getAdapter
import com.tsubasa.core.ui.component.statuslayout.getSwipeToLoadComponent
import com.tsubasa.core.ui.component.viewholder.createAdapter
import com.tsubasa.core.util.lifecycle.bind
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val standardListComponent = StatusUIComponentFactory.standardListComponent {
            createAdapter<String, CustomMainListItemComponent> {
                CustomMainListItemComponent()
            }
//            object :BaseQuickAdapter<String, BaseViewHolder>(R.layout.item) {
//                override fun convert(helper: BaseViewHolder?, item: String?) {
//                    (helper?.itemView as? TextView)?.text = item
//                }
//            }
        }
        standardListComponent.setContentView(this)

        standardListComponent.status.value = Status.STATUS_LOADING
        standardListComponent.container?.postDelayed({
            standardListComponent.status.value = Status.STATUS_SUCCESS
            standardListComponent.getSwipeToLoadComponent()?.initStatus?.value = Status.STATUS_SUCCESS
            val dataList = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,a,b,c,d,e,f,g,h,i,j,k,l,m,n,a,b,c,d,e,f,g,h,i,j,k,l,m,n".split(",").toMutableList()

            standardListComponent.getAdapter<String>()?.setNewData(dataList)
        }, 1000)
    }
}

class CustomMainListItemComponent : BaseComponent<LinearLayout>(), Observer<String> {

    val data: MutableLiveData<String> = MutableLiveData()

    override fun onChanged(t: String?) {
        data.value = t
    }

    override fun createContainer(context: AnkoContext<Any>): LinearLayout = context.linearLayout().apply {
        layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
    }

    override fun createContent(parent: LinearLayout) {
        parent.apply {
            backgroundColor = Color.BLUE
            textView {
                textColor = Color.WHITE
                text = "null"
                data.bind(owner) {
                    text = data.value
                }
            }.apply {
                layoutParams = LinearLayout.LayoutParams(matchParent, dip(30))
            }
        }
    }
}