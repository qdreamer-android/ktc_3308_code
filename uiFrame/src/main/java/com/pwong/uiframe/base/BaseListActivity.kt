package com.pwong.uiframe.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.pwong.uiframe.R
import com.pwong.uiframe.databinding.AbsActivityListBinding
import com.pwong.uiframe.listener.OnViewModelClickListener
import kotlinx.android.synthetic.main.abs_activity_list.*

abstract class BaseListActivity<itemDB : ViewDataBinding, VM : BaseListViewModel, ItemVM : IViewModel>
    : BaseActivity<AbsActivityListBinding, VM>(), OnViewModelClickListener<ItemVM>
    , SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    protected companion object {
        const val DEFAULT_PAGE_NO = 1
        const val DEFAULT_PAGE_SIZE = 15
    }

    private val mAdapter by lazy {
        object : BaseAdapter<itemDB, ItemVM>(layoutItemView()) {
            override fun onConvert(binding: itemDB, model: ItemVM) {
                onItemConvert(binding, model)
            }
        }
    }

    protected var pageNo: Int = DEFAULT_PAGE_NO

    override fun layoutView() = R.layout.abs_activity_list

    override fun initView() {
        rcyActivity?.layoutManager = layoutManager()
        mAdapter.bindToRecyclerView(rcyActivity)
        emptyView()?.let {
            mAdapter.emptyView = it
        }
        if (loadingEnabled()) {
            mAdapter.setOnLoadMoreListener(this@BaseListActivity, rcyActivity)
            mAdapter.disableLoadMoreIfNotFullPage(rcyActivity)
        }
        mAdapter.addOnItemClickListener(this@BaseListActivity)

        refreshActivity?.setColorSchemeResources(R.color.colorPrimary)

        refreshActivity?.setOnRefreshListener(this)

        if (autoRefresh() && refreshActivity.isEnabled) {
            refreshActivity?.isRefreshing = true
            onRefresh()
        } else {
            loadContentList()
        }
    }

    override fun onRefresh() {
        pageNo = DEFAULT_PAGE_NO
        if (loadingEnabled()) {
            mAdapter.setEnableLoadMore(false)
        }
        if (!onRefreshCallback()) {
            loadContentList()
        }
    }

    override fun onLoadMoreRequested() {
        getViewModel().updateRefreshEnable.set(false)
        if (!onLoadingCallback()) {
            loadContentList()
        }
    }

    protected open fun onItemConvert(binding: itemDB, model: ItemVM) {}
    protected open fun emptyView(): View? = null
    protected open fun autoRefresh() = true
    protected open fun loadingEnabled() = true
    protected open fun onRefreshCallback() = false
    protected open fun onLoadingCallback() = false
    protected open fun layoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(this)

    @LayoutRes
    protected abstract fun layoutItemView(): Int

    protected abstract fun loadContentList()

    protected fun setNewData(list: MutableList<ItemVM>) {
        mAdapter.setNewData(list)
    }

    protected fun addData(model: ItemVM) {
        mAdapter.addData(model)
    }

    protected fun addDataList(list: MutableList<ItemVM>) {
        mAdapter.addData(list)
    }

    protected fun removeData(model: ItemVM) {
        if (!mAdapter.data.isNullOrEmpty()) {
            val index = mAdapter.data.indexOf(model)
            if (index >= 0 && index < mAdapter.data.size) {
                mAdapter.remove(index)
            }
        }
    }

    protected fun finishRefresh() {
        refreshActivity?.isRefreshing = false
        if (loadingEnabled()) {
            mAdapter.setEnableLoadMore(true)
        }
    }

    protected fun finishLoading(hasMore: Boolean = true, success: Boolean = true) {
        getViewModel().updateRefreshEnable.set(true)
        if (success) {
            if (hasMore) {
                mAdapter.loadMoreComplete()
            } else {
                mAdapter.loadMoreEnd()
            }
        } else {
            mAdapter.loadMoreFail()
        }
    }

}