package com.qdreamer.ktc_upgrade;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.pwong.uiframe.base.FViewModel;

/**
 * @Author: Pen
 * @Create: 2022-05-24 16:31:11
 * @Email: bug@163.com
 */
public class CheckFragmentViewModel extends FViewModel {

    public ObservableBoolean isRecording = new ObservableBoolean(false);
    public ObservableField<String> checkResult = new ObservableField<>();

}
