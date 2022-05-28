package com.qdreamer.ktc_upgrade;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import com.pwong.uiframe.base.BaseViewModel;

/**
 * @Author: Pen
 * @Create: 2022-05-24 16:06:05
 * @Email: bug@163.com
 */
public class HomeViewModel extends BaseViewModel {

    ObservableBoolean socketConnectSuccess = new ObservableBoolean(false);
    public ObservableInt position = new ObservableInt(0);

}
