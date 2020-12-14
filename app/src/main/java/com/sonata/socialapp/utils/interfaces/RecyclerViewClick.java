package com.sonata.socialapp.utils.interfaces;

import android.media.Image;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;

public interface RecyclerViewClick {
    void onReloadImageClick(int position, RoundKornerRelativeLayout reloadLayout, ProgressBar progressBar,ImageView imageView);
    void onImageClick(int position,ImageView imageView,int pos);
    void onLinkClick(int position);
    void onOptionsClick(int position,TextView commentNumber);
    void onLikeClick(int position, ImageView likeImage, TextView likeNumber);
    void onOpenComments(int position);
    void onGoToProfileClick(int position);
    void onSocialClick(int position,int clickType,String text);

}
