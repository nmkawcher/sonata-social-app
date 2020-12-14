package com.sonata.socialapp.utils.interfaces;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;

public interface CommentReplyAdapterClick {
    void onReloadImageClick(int position, RoundKornerRelativeLayout reloadLayout, ProgressBar progressBar, ImageView imageView);
    void onImageClick(int position,ImageView imageView,int pos);


    void onGoToProfileClick(int position);

    void onCommentOptionsClick(int position);
    void onCommentUpvoteClick(int position, ImageView upvoteimage, ImageView downvoteimage, TextView votecount);
    void onCommentDownvoteClick(int position, ImageView upvoteimage, ImageView downvoteimage, TextView votecount);
    void onCommentReply(int position);
    void onCommentSocialClick(int position, int clickType, String text);

}
