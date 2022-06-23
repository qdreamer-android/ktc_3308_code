package com.qdreamer.ktc_upgrade;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.pwong.uiframe.base.FViewModel;

/**
 * @Author: Pen
 * @Create: 2022-05-24 16:24:07
 * @Email: bug@163.com
 */
public class UpgradeFragmentViewModel extends FViewModel {

    public ObservableField<String> upgradePkgPath = new ObservableField<>();
    public ObservableBoolean inUpgrade = new ObservableBoolean(false);

}
