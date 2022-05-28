package com.pwong.uiframe.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * @author android
 * @date 2018/12/13
 */
class CommonFragmentStateAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val mFragments: MutableList<Fragment> = ArrayList()

    constructor(fm: FragmentManager, vararg fragment: Fragment) : this(fm) {
        fragment.forEach {
            mFragments.add(it)
        }
    }

    constructor(fm: FragmentManager, fragmentList: List<Fragment>) : this(fm) {
        mFragments.addAll(fragmentList)
    }

    fun clearFragments() {
        mFragments.clear()
    }

    fun addFragment(vararg fragment: Fragment) {
        fragment.forEach {
            mFragments.add(it)
        }
    }

    private var mTitles: MutableList<String>? = null

    fun setTitles(titles: MutableList<String>?) {
        mTitles = titles
    }

    fun setTitles(titles: Array<String>) {
        if (mTitles == null) {
            mTitles = ArrayList()
        }
        mTitles!!.clear()
        mTitles!!.addAll(titles)
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (mTitles != null && mTitles!!.size > 0) {
            mTitles!![position]
        } else {
            super.getPageTitle(position)
        }
    }

}