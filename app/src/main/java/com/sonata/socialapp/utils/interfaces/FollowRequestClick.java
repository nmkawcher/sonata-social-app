package com.sonata.socialapp.utils.interfaces;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;

public interface FollowRequestClick {

    void goToProfileClick(int position);
    void buttonClick(int position, TextView buttonText, RoundKornerRelativeLayout buttonLay,RoundKornerRelativeLayout rejectLayout);
    void rejectClick(int position, TextView buttonText, RoundKornerRelativeLayout buttonLay, RoundKornerRelativeLayout rejectLayout, ProgressBar progressBar, RelativeLayout reject);
}
