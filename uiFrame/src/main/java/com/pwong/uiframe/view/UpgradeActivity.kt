package com.pwong.uiframe.view

import androidx.databinding.ViewDataBinding
import com.pwong.uiframe.base.BaseActivity
import com.pwong.uiframe.base.BaseViewModel
import com.pwong.uiframe.upgrade.OnDownloadListener
import com.pwong.uiframe.upgrade.UpgradeHelper

/**
 * @Author: Pen
 * @Create: 2022-01-05 17:19:34
 * @Email:  Rocking@7189.com
 */
abstract class UpgradeActivity<DB : ViewDataBinding, VM : BaseViewModel> : BaseActivity<DB, VM>(
), OnDownloadListener {

    private var upgradeHelper: UpgradeHelper? = null

    fun startUpgrade(title: String? = null, desc: String? = null, md5: String? = null, apkUrl: String, version: String, forceUpgrade: Boolean) {
        upgradeHelper = UpgradeHelper(this, forceUpgrade)
        upgradeHelper!!.registerInstallReceiver()
        upgradeHelper!!.addOnDownloadListener(this)
    }


}