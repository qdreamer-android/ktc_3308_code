package com.pwong.uiframe.base

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pwong.uiframe.BR
import com.pwong.uiframe.listener.OnViewModelClickListener

/**
 * @author android
 * @date 20-5-9
 * @instruction fucking bugs
 */
abstract class BaseAdapter<DB : ViewDataBinding, VM : IViewModel>(layoutId: Int) :
    BaseQuickAdapter<VM, BaseViewHolder>(layoutId) {

    private var mItemListener: OnViewModelClickListener<VM>? = null
    private var mViewModelListener: OnViewModelClickListener<IViewModel>? = null

    override fun convert(helper: BaseViewHolder?, item: VM?) {
        helper?.itemView?.let { view ->
            DataBindingUtil.bind<DB>(view)?.let { binding ->
                if (item != null) {
                    binding.setVariable(BR.viewModel, item)
                    binding.setVariable(BR.itemListener, object : OnViewModelClickListener<VM> {
                        override fun onFastClick(v: View, model: VM) {
                            mItemListener?.onFastClick(v, model)
                            mViewModelListener?.onFastClick(v, model)
                        }
                    })
                    onConvert(binding, item)
                }
            }
        }
    }

    abstract fun onConvert(binding: DB, model: VM)

    override fun setNewData(data: MutableList<VM>?) {
        super.setNewData(data)
        if (recyclerView != null) {     // need bind recyclerView first
            disableLoadMoreIfNotFullPage()
        }
    }

    fun addOnItemClickListener(listener: OnViewModelClickListener<VM>?) {
        mItemListener = listener
    }

    fun addOnViewModelClickListener(listener: OnViewModelClickListener<IViewModel>?) {
        mViewModelListener = listener
    }

}