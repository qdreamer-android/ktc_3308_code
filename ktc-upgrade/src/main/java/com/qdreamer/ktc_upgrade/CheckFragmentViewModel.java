package com.qdreamer.ktc_upgrade;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.pwong.uiframe.base.FViewModel;

public class CheckFragmentViewModel extends FViewModel {

    public ObservableBoolean isRecording = new ObservableBoolean(false);
    public ObservableField<String> checkResult = new ObservableField<>();

}
