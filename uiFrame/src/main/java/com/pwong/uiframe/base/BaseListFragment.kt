package com.pwong.uiframe.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.pwong.uiframe.R
import com.pwong.uiframe.databinding.AbsFragmentListBinding
import com.pwong.uiframe.listener.OnViewModelClickListener
import kotlinx.android.synthetic.main.abs_fragment_list.*

/**
 * @author android
 * @date 20-5-7
 * @instruction fucking bugs
 */
abstract class BaseListFragment<itemDB : ViewDataBinding, VM : FListViewModel, ItemVM : IViewModel>
    : BaseFragment<AbsFragmentListBinding, VM>(), OnViewModelClickListener<ItemVM>,
    SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    protected companion object {
        const val DEFAULT_PAGE_NO = 1
        const val DEFAULT_PAGE_SIZE = 15
    }

    private val mAdapter by lazy {
        object : BaseAdapter<itemDB, ItemVM>(layoutItemView()) {
            override fun onConvert(binding: itemDB, model: ItemVM) {

            }
        }
    }

    protected var pageNo: Int = DEFAULT_PAGE_NO

    override fun layoutView() = R.layout.abs_fragment_list

    override fun initView() {
        rcyFragment?.layoutManager = layoutManager()
        mAdapter.bindToRecyclerView(rcyFragment)
        emptyView()?.let {
            mAdapter.emptyView = it
        }
        if (loadingEnabled()) {
            mAdapter.setOnLoadMoreListener(this@BaseListFragment, rcyFragment)
            mAdapter.disableLoadMoreIfNotFullPage(rcyFragment)
        }
        mAdapter.addOnItemClickListener(this@BaseListFragment)

        refreshFragment?.setColorSchemeResources(R.color.colorPrimary)

        refreshFragment?.setOnRefreshListener(this)

        if (autoRefresh() && refreshFragment.isEnabled) {
            refreshFragment?.isRefreshing = true
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

    protected open fun emptyView(): View? = null
    protected open fun autoRefresh() = true
    protected open fun loadingEnabled() = true
    protected open fun onRefreshCallback() = false
    protected open fun onLoadingCallback() = false
    protected open fun layoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

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
        refreshFragment?.isRefreshing = false
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