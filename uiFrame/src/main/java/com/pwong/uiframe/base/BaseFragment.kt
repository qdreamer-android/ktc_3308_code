package com.pwong.uiframe.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pwong.uiframe.BR
import com.pwong.uiframe.utils.TUtil

abstract class BaseFragment<DB : ViewDataBinding, VM : FViewModel> : Fragment() {

    private var mBinding: DB? = null
    
    private val mViewModel: VM by lazy {
        ViewModelProvider(this).get(TUtil.getT<VM>(this, 1)!!::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, layoutView(), container, false)
        mBinding!!.setVariable(BR.viewModel, mViewModel)
        mBinding!!.executePendingBindings()
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    @LayoutRes
    protected abstract fun layoutView(): Int

    protected abstract fun initView()

    protected fun getBinding() = mBinding
    protected fun getViewModel() = mViewModel

}