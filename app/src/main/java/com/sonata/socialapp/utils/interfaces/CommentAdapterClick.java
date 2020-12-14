package com.sonata.socialapp.utils.interfaces;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;

public interface CommentAdapterClick {
    void onReloadImageClick(int position, RoundKornerRelativeLayout reloadLayout, ProgressBar progressBar, ImageView imageView);
    void onImageClick(int position,ImageView imageView,int pos);
    void onPostLinkClick(int position);
    void onPostOptionsClick(int position, TextView commentNumber);
    void onPostLikeClick(int position, ImageView likeImage, TextView likeNumber);
    void onPostOpenComments(int position);
    void onGoToProfileClick(int position);
    void onPostSocialClick(int position, int clickType, String text);

    void onCommentOptionsClick(int position);
    void onCommentUpvoteClick(int position, ImageView upvoteimage,ImageView downvoteimage, TextView votecount);
    void onCommentDownvoteClick(int position, ImageView upvoteimage,ImageView downvoteimage, TextView votecount);
    void onCommentShowReplies(int position);
    void onCommentReply(int position);
    void onCommentSocialClick(int position, int clickType, String text);

}
