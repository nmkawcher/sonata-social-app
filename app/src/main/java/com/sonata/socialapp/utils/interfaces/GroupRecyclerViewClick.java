package com.sonata.socialapp.utils.interfaces;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;

public interface GroupRecyclerViewClick {
    void onReloadImageClick(int position, RoundKornerRelativeLayout reloadLayout, ProgressBar progressBar, ImageView imageView);
    void onImageClick(int position, ImageView imageView, int pos);
    void onLinkClick(int position);
    void onOptionsClick(int position, TextView commentNumber);
    void onUpvoteClick(int position, ImageView upvoteImage,ImageView downvoteImage, TextView voteNumber);
    void onDownvoteClick(int position, ImageView upvoteImage,ImageView downvoteImage, TextView voteNumber);
    void onOpenComments(int position);
    void onGoToProfileClick(int position);
    void onSocialClick(int position, int clickType, String text);

}
