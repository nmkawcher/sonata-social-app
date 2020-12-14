package com.sonata.socialapp.utils.interfaces;

import android.widget.TextView;

import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;

public interface BlockedAdapterClick {

    void goToProfileClick(int position);
    void buttonClick(int position, TextView buttonText, RoundKornerRelativeLayout buttonLay);
}
