package com.sonata.socialapp.utils.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.Comment;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.CommentReplyAdapterClick;
import com.takwolf.android.aspectratio.AspectRatioLayout;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class ComReplyAdapter extends RecyclerView.Adapter<ComReplyAdapter.ViewHolder> {




    List<Comment> list;
    RequestManager glide;

    boolean finish = false;
    public void setFinish(boolean finish){
        this.finish=finish;
    }

    CommentReplyAdapterClick recyclerViewClick;
    public void setContext(List<Comment> list
            ,RequestManager glide,CommentReplyAdapterClick recyclerViewClick){
        this.recyclerViewClick=recyclerViewClick;
        this.list=list;
        this.glide=glide;

    }

    private static final int COMMENT_TYPE_TEXT_FIRST = 1;
    private static final int COMMENT_TYPE_IMAGE_FIRST = 2;
    private static final int COMMENT_TYPE_TEXT = 3;
    private static final int POST_TYPE_EMPTY = 5;
    private static final int POST_TYPE_LOAD = 6;


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int i) {
        if(i==0){
            switch (Objects.requireNonNull(list.get(i).getString("type"))) {
                case "text":
                    return COMMENT_TYPE_TEXT_FIRST;
                case "image":
                    return COMMENT_TYPE_IMAGE_FIRST;
                case "boş":
                    return POST_TYPE_EMPTY;
                default:
                    return POST_TYPE_LOAD;
            }
        }
        else{
            switch (Objects.requireNonNull(list.get(i).getString("type"))) {
                case "text":
                    return COMMENT_TYPE_TEXT;
                case "boş":
                    return POST_TYPE_EMPTY;
                default:
                    return POST_TYPE_LOAD;
            }


        }

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder.profilephoto != null) {
            if(holder.profilephoto.getVisibility()== View.VISIBLE){
                glide.clear(holder.profilephoto);
                holder.profilephoto.setImageDrawable(null);
                holder.profilephoto.setImageBitmap(null);
            }

        }
        if (holder.postimage != null) {
            if(holder.postimage.getVisibility()== View.VISIBLE){
                glide.clear(holder.postimage);
                holder.postimage.setImageDrawable(null);
                holder.postimage.setImageBitmap(null);
            }
        }
    }





    @NonNull
    @Override
    public ComReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==COMMENT_TYPE_TEXT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_LOAD){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_load,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==COMMENT_TYPE_TEXT_FIRST){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_layout_reply_first,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==COMMENT_TYPE_IMAGE_FIRST){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_layout_image_reply_first,viewGroup,false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view,viewGroup,false);
            return new ViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ComReplyAdapter.ViewHolder holder, int positiona) {
        if(getItemViewType(holder.getAdapterPosition())!=POST_TYPE_LOAD&&getItemViewType(holder.getAdapterPosition())!=POST_TYPE_EMPTY){

            Comment post = list.get(holder.getAdapterPosition());


            holder.postdate.setText(DateUtils.getRelativeTimeSpanString(post.getCreatedAt().getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

            if(holder.getAdapterPosition()==0){
                if(post.getReplyCount()==1){
                    holder.howmanyreplies.setText(post.getReplyCount()+" "+holder.itemView.getContext().getString(R.string.replies2tekil));
                }
                else{
                    holder.howmanyreplies.setText(post.getReplyCount()+" "+holder.itemView.getContext().getString(R.string.replies2cogul));
                }
            }




            holder.upvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_upvote));
            holder.downvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_downvote));
            holder.votecount.setTextColor(Color.parseColor("#999999"));
            if(post.getUpvote()){
                holder.upvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_upvote_blue));
                holder.downvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_downvote));

                holder.votecount.setTextColor(Color.parseColor("#2d72bc"));


            }
            if(post.getDownvote()){
                holder.downvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_downvote_red));
                holder.upvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_upvote));

                holder.votecount.setTextColor(Color.parseColor("#a64942"));
            }
            if(post.getVote()<0){
                holder.votecount.setText("-"+ GenelUtil.ConvertNumber((int)post.getVote()*(-1),holder.itemView.getContext()));

            }
            if(post.getVote()>=0){
                holder.votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),holder.itemView.getContext()));

            }



            SonataUser user = post.getUser();

            if(user.getHasPp()){
                glide.load(user.getPPAdapter())
                        .placeholder(new ColorDrawable(ContextCompat.getColor(holder.profilephoto.getContext(), R.color.placeholder_gray)))
                        .into(holder.profilephoto);
            }
            else{
                glide.load(holder.itemView.getContext().getResources().getDrawable(R.drawable.emptypp)).into(holder.profilephoto);

            }

            holder.name.setText(user.getName());
            holder.username.setText("@"+user.getUsername());



            if(post.getDesc().trim().length()>0){
                if(!holder.postdesc.getPostId().equals(post.getObjectId())){
                    holder.postdesc.setPostId(post.getObjectId());
                    holder.postdesc.setMaxLines(3);
                }
                holder.postdesc.setLinkText(post.getDesc().trim());
                holder.postdesc.setVisibility(View.VISIBLE);

            }

            if(getItemViewType(holder.getAdapterPosition())==COMMENT_TYPE_IMAGE_FIRST){

                //holder.imageprogress.setVisibility(View.VISIBLE);
                //holder.reloadlayout.setVisibility(View.INVISIBLE);


                if(((float)post.getRatioH()/(float)post.getRatioW())>1.4f){
                    holder.ratiolayout.setAspectRatio(10,14);
                }
                else{
                    if(((float)post.getRatioH()/(float)post.getRatioW())<0.4f){
                        holder.ratiolayout.setAspectRatio(10,4);
                    }
                    else{
                        holder.ratiolayout.setAspectRatio(post.getRatioW(),post.getRatioH());
                    }
                }


                String url=post.getMainMedia().getUrl();

                String thumburl=post.getThumbMedia().getUrl();
                if(post.getNsfw()){
                    holder.nsfwIcon.setVisibility(View.VISIBLE);
                    glide.load(url).fitCenter().fitCenter().thumbnail(glide.load(thumburl)).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //holder.imageprogress.setVisibility(View.INVISIBLE);
                            //holder.reloadlayout.setVisibility(View.VISIBLE);
                            Log.e("glideError",e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //holder.imageprogress.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    }).into(holder.postimage);

                }
                else{
                    holder.nsfwIcon.setVisibility(View.INVISIBLE);
                    if(post.getRatioH()>1280||post.getRatioW()>1280){
                        int ih = 1280;
                        int iw = 1280;
                        if(post.getRatioH()>post.getRatioW()){
                            ih = 1280;
                            iw = 1280 * (post.getRatioW()/post.getRatioH());
                        }
                        else{
                            iw = 1280;
                            ih = 1280 * (post.getRatioH()/post.getRatioW());
                        }
                        glide.load(url).fitCenter().fitCenter().override(iw,ih).thumbnail(glide.load(thumburl)).addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //holder.imageprogress.setVisibility(View.INVISIBLE);
                                //holder.reloadlayout.setVisibility(View.VISIBLE);
                                Log.e("glideError",e.getMessage());
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //holder.imageprogress.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        }).into(holder.postimage);
                    }
                    else{
                        //holder.nsfwIcon.setVisibility(View.INVISIBLE);
                        glide.load(url).fitCenter().fitCenter().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).thumbnail(glide.load(thumburl)).addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //holder.imageprogress.setVisibility(View.INVISIBLE);
                                //holder.reloadlayout.setVisibility(View.VISIBLE);
                                Log.e("glideError",e.getMessage());
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //holder.imageprogress.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        }).into(holder.postimage);
                    }
                }



            }

        }


    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        else{
            return 0;
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView upvoteimage,downvoteimage,nsfwIcon;
        ImageView profilephoto,postimage;
        TextView name,username,postdate,votecount,howmanyreplies,readMore;
        SocialTextView postdesc;
        ProgressBar imageprogress;
        AspectRatioLayout ratiolayout;
        RoundKornerRelativeLayout reloadlayout;
        RelativeLayout options,reloadripple,upvote,downvote,reply ;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nsfwIcon = itemView.findViewById(R.id.postImageNsfwIcon);

            howmanyreplies=itemView.findViewById(R.id.commentreplyfirst);
            reply=itemView.findViewById(R.id.commentreplyripple);
            postimage = itemView.findViewById(R.id.postimageview);
            imageprogress = itemView.findViewById(R.id.postimageprogressbar);
            ratiolayout = itemView.findViewById(R.id.postratiolayout);
            reloadripple = itemView.findViewById(R.id.postfailimageripple);
            reloadlayout=itemView.findViewById(R.id.postimagefail);
            votecount = itemView.findViewById(R.id.commentvotecounttext);
            options = itemView.findViewById(R.id.commentoptions);
            upvote = itemView.findViewById(R.id.commentupvoteripple);
            downvote = itemView.findViewById(R.id.commentdownvoteripple);
            upvoteimage = itemView.findViewById(R.id.commentupvoteimage);
            downvoteimage = itemView.findViewById(R.id.commentdownvoteimage);
            profilephoto = itemView.findViewById(R.id.commentppimage);
            name = itemView.findViewById(R.id.commentnametext);
            username = itemView.findViewById(R.id.commentusernametext);
            postdesc = itemView.findViewById(R.id.commentdesc);
            postdate = itemView.findViewById(R.id.commentdate);
            readMore = itemView.findViewById(R.id.readMoreText);

            if(readMore!=null){
                readMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(postdesc==null) return;

                        if(postdesc.getLineCount()>postdesc.getMaxLines()){
                            postdesc.setMaxLines(Integer.MAX_VALUE);
                            postdesc.setEllipsize(null);
                            if(readMore!=null){
                                readMore.setVisibility(View.GONE);
                            }

                        }
                        else if(postdesc.getLineCount()<=postdesc.getMaxLines()){
                            postdesc.setMaxLines(3);
                            postdesc.setEllipsize(null);
                            if(postdesc.getLineCount()>3){
                                if(readMore!=null){
                                    readMore.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
            }


            if(profilephoto!=null){
                profilephoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onGoToProfileClick(getAdapterPosition());
                    }
                });
            }

            if(name!=null){
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onGoToProfileClick(getAdapterPosition());
                    }
                });
            }

            if(username!=null){
                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onGoToProfileClick(getAdapterPosition());
                    }
                });
            }

            if(upvote!=null){
                upvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onCommentUpvoteClick(getAdapterPosition(),upvoteimage,downvoteimage,votecount);
                    }
                });
            }

            if(downvote!=null){
                downvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onCommentDownvoteClick(getAdapterPosition(),upvoteimage,downvoteimage,votecount);
                    }
                });
            }

            if(reply!=null){
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onCommentReply(getAdapterPosition());
                    }
                });
            }

            if(options!=null){
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onCommentOptionsClick(getAdapterPosition());
                    }
                });
            }

            if(postdesc!=null){
                postdesc.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
                    @Override
                    public void onLinkClicked(int i, String s) {
                        recyclerViewClick.onCommentSocialClick(getAdapterPosition(),i,s);

                    }
                });
                postdesc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GenelUtil.ToastLong(itemView.getContext(),postdesc.getLineCount()+"");
                        if(postdesc.getLineCount()>postdesc.getMaxLines()){
                            postdesc.setMaxLines(Integer.MAX_VALUE);
                            postdesc.setEllipsize(null);
                            if(readMore!=null){
                                readMore.setVisibility(View.GONE);
                            }

                        }
                        else if(postdesc.getLineCount()<=postdesc.getMaxLines()){
                            postdesc.setMaxLines(3);
                            postdesc.setEllipsize(null);
                            if(postdesc.getLineCount()>3){
                                if(readMore!=null){
                                    readMore.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
                postdesc.setLineCountListener(new SocialTextView.LineCountListener() {
                    @Override
                    public void onLineCount(int lineCount) {
                        Log.e("Position",getAdapterPosition()+"");
                        Log.e("line count",lineCount+"");
                        Log.e("max line count",postdesc.getMaxLines()+"");
                        if(lineCount > postdesc.getMaxLines()){
                            if(readMore != null){
                                Log.e("readMoreVisible","true");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    public void run() {
                                        // do something
                                        readMore.setVisibility(View.VISIBLE);
                                    }
                                });

                            }
                        }
                        else{
                            if(readMore!=null){
                                Log.e("readMoreVisible","false");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    public void run() {
                                        // do something
                                        readMore.setVisibility(View.GONE);
                                    }
                                });

                            }
                        }
                    }
                });
            }

            if(postimage!=null){
                postimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onImageClick(getAdapterPosition(),postimage,0);
                    }
                });
            }

            if(reloadripple!=null){
                reloadripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onReloadImageClick(getAdapterPosition()
                                ,reloadlayout,imageprogress,postimage);
                    }
                });
            }


        }

    }













}
