package com.sonata.socialapp.utils.VideoUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.MyApp;

import cn.jzvd.JzvdStd;

public class VideoPlayer extends JzvdStd {




    public VideoPlayer(Context context) {
        super(context);
        posterImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        posterImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }


    @Override
    public void showWifiDialog() {
        //super.showWifiDialog();
        if (state == STATE_PAUSE) {
            startButton.performClick();
        } else {
            startVideo();
        }
    }

    @Override
    public void onStatePlaying() {

        super.onStatePlaying();
        if(!MyApp.ses){
            mediaInterface.setVolume(0,0);
            fullscreenButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_mute));
        }
        else{
            mediaInterface.setVolume(1,1);
            fullscreenButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_unmute));
        }
        fullscreenButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMute();
            }
        });
    }

    @Override
    public void onPrepared() {
        super.onPrepared();



    }

    public void setMute(){
        if(!MyApp.ses){
            mediaInterface.setVolume(1,1);

            MyApp.ses=true;
            fullscreenButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_unmute));
        }
        else{
            mediaInterface.setVolume(0,0);
            MyApp.ses=false;
            fullscreenButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_mute));
        }
    }
}
