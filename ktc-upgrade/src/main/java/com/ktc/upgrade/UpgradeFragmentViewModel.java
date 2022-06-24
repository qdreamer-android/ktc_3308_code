package com.ktc.upgrade;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.pwong.uiframe.base.FViewModel;

public class UpgradeFragmentViewModel extends FViewModel {

    public ObservableBoolean switching = new ObservableBoolean(false);
    public ObservableBoolean algorithmSwitch = new ObservableBoolean(true);
    public ObservableField<String> upgradePkgPath = new ObservableField<>();
    public ObservableBoolean inUpgrade = new ObservableBoolean(false);

}
