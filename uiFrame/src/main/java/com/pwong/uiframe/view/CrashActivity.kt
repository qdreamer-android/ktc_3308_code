package com.pwong.uiframe.view

import android.content.Context
import android.content.Intent
import com.pwong.uiframe.R
import com.pwong.uiframe.base.BaseActivity
import com.pwong.uiframe.databinding.ActivityCrashBinding

class CrashActivity : BaseActivity<ActivityCrashBinding, CrashViewModel>() {

    companion object {
        private const val KEY_CRASH_CONTENT = "crashContent"

        fun action(context: Context, content: String) {
            val intent = Intent(context, CrashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(KEY_CRASH_CONTENT, content)
            context.startActivity(intent)
        }
    }

    override fun layoutView() = R.layout.activity_crash

    override fun preInit() = false

    override fun initView() {
        getViewModel().content.set(intent?.getStringExtra(KEY_CRASH_CONTENT) ?: "")
    }

}