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
import com.parse.ParseObject;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.VideoUtils.JZMediaExo;
import com.sonata.socialapp.utils.VideoUtils.VideoPlayer;
import com.sonata.socialapp.utils.classes.Comment;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.CommentAdapterClick;
import com.takwolf.android.aspectratio.AspectRatioLayout;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import jp.wasabeef.glide.transformations.BlurTransformation;


public class ComAdapter extends RecyclerView.Adapter<ComAdapter.ViewHolder> {




    List<ParseObject> list;
    RequestManager glide;

    boolean finish = false;
    public void setFinish(boolean finish){
        this.finish=finish;
    }

    CommentAdapterClick recyclerViewClick;
    public void setContext(List<ParseObject> list
            ,RequestManager glide,CommentAdapterClick recyclerViewClick){
        this.recyclerViewClick=recyclerViewClick;
        this.list=list;
        this.glide=glide;

    }

    private static final int POST_TYPE_TEXT = 1;
    private static final int POST_TYPE_IMAGE = 2;
    private static final int POST_TYPE_IMAGE1 = 2589;
    private static final int POST_TYPE_IMAGE2 = 24758;
    private static final int POST_TYPE_IMAGE3 = 21256;
    private static final int POST_TYPE_VIDEO = 22;
    private static final int COMMENT_TYPE_TEXT = 3;
    private static final int COMMENT_TYPE_IMAGE = 7;
    private static final int POST_TYPE_LINK = 4;
    private static final int POST_TYPE_EMPTY = 5;
    private static final int POST_TYPE_LOAD = 6;
    private static final int REPLY_TYPE_TEXT = 8;
    private static final int REPLY_TYPE_IMAGE = 9;


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int i) {
        if(i==0){
            switch (Objects.requireNonNull(list.get(i).getString("type"))) {
                case "text":
                    return POST_TYPE_TEXT;
                case "image":
                    if(list.get(i).getLong("imagecount")==1){
                        return POST_TYPE_IMAGE;
                    }
                    else if(list.get(i).getLong("imagecount")==2){
                        return POST_TYPE_IMAGE1;
                    }
                    else if(list.get(i).getLong("imagecount")==3){
                        return POST_TYPE_IMAGE2;
                    }
                    else if(list.get(i).getLong("imagecount")==4){
                        return POST_TYPE_IMAGE3;
                    }
                    else if(list.get(i).getLong("imagecount")==0){
                        return POST_TYPE_IMAGE;
                    }
                case "video":
                    return POST_TYPE_VIDEO;
                case "link":
                    return POST_TYPE_LINK;
                case "boş":
                    return POST_TYPE_EMPTY;
                default:
                    return POST_TYPE_LOAD;
            }
        }
        else{
            if(list.get(i).getString("isreply")!=null){
                if(list.get(i).getString("isreply").equals("false")){
                    switch (Objects.requireNonNull(list.get(i).getString("type"))) {
                        case "text":
                            return COMMENT_TYPE_TEXT;
                        case "image":
                            return COMMENT_TYPE_IMAGE;
                        case "boş":
                            return POST_TYPE_EMPTY;
                        default:
                            return POST_TYPE_LOAD;
                    }
                }
                else{
                    switch (Objects.requireNonNull(list.get(i).getString("type"))) {
                        case "text":
                            return REPLY_TYPE_TEXT;
                        case "boş":
                            return POST_TYPE_EMPTY;
                        case "image":
                            return REPLY_TYPE_IMAGE;
                        default:
                            return POST_TYPE_LOAD;
                    }
                }
            }
            else{
                if ("boş".equals(Objects.requireNonNull(list.get(i).getString("type")))) {
                    return POST_TYPE_EMPTY;
                }
                return POST_TYPE_LOAD;
            }


        }

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.postprofilephoto != null) {
            if(holder.postprofilephoto.getVisibility()== View.VISIBLE){
                glide.clear(holder.postprofilephoto);
                holder.postprofilephoto.setImageDrawable(null);
                holder.postprofilephoto.setImageBitmap(null);
            }

        }
        if (holder.commentprofilephoto != null) {
            if(holder.commentprofilephoto.getVisibility()== View.VISIBLE){
                glide.clear(holder.commentprofilephoto);
                holder.commentprofilephoto.setImageDrawable(null);
                holder.commentprofilephoto.setImageBitmap(null);
            }

        }
        if (holder.postimage != null) {
            if(holder.postimage.getVisibility()== View.VISIBLE){
                glide.clear(holder.postimage);
                holder.postimage.setImageDrawable(null);
                holder.postimage.setImageBitmap(null);
            }
        }
        if (holder.postimage1 != null) {
            glide.clear(holder.postimage1);
            holder.postimage1.setImageDrawable(null);
            holder.postimage1.setImageBitmap(null);

        }
        if (holder.postimage2 != null) {
            glide.clear(holder.postimage2);
            holder.postimage2.setImageDrawable(null);
            holder.postimage2.setImageBitmap(null);

        }
        if (holder.postimage3 != null) {
            glide.clear(holder.postimage3);
            holder.postimage3.setImageDrawable(null);
            holder.postimage3.setImageBitmap(null);

        }



        if (holder.postlinkimage != null) {
            if(holder.postlinkimage.getVisibility()== View.VISIBLE){
                glide.clear(holder.postlinkimage);
                holder.postlinkimage.setImageDrawable(null);
                holder.postlinkimage.setImageBitmap(null);
            }
        }



        if(holder.videoPlayer!=null){
            if (holder.videoPlayer.posterImageView != null) {
                glide.clear(holder.videoPlayer.posterImageView);
                holder.videoPlayer.posterImageView.setImageDrawable(null);
                holder.videoPlayer.posterImageView.setImageBitmap(null);

            }
        }



    }
    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        VideoPlayer jzvd = holder.videoPlayer;
        if (jzvd != null && Jzvd.CURRENT_JZVD != null && jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
            if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                Jzvd.releaseAllVideos();
            }
        }
    }




    @NonNull
    @Override
    public ComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==POST_TYPE_TEXT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_text_layout_comment,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_image_layout_comment,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE1){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_image_layout_1_comment,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE2){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_image_layout_2_comment,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE3){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_image_layout_3_comment,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_VIDEO){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_video_layout_comment,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_LINK){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_link_layout_comment,viewGroup,false);
            return new ViewHolder(view);
        }

        else if(i==POST_TYPE_LOAD){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_load,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==COMMENT_TYPE_IMAGE){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_layout_image,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==COMMENT_TYPE_TEXT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_layout,viewGroup,false);
            return new ViewHolder(view);
        }

        else if(i==REPLY_TYPE_IMAGE){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_layout_image_reply_first_notif,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==REPLY_TYPE_TEXT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_layout_reply_first_notif,viewGroup,false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view,viewGroup,false);
            return new ViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ComAdapter.ViewHolder holder, int positiona) {
        if(getItemViewType(holder.getAdapterPosition())!=POST_TYPE_LOAD&&getItemViewType(holder.getAdapterPosition())!=POST_TYPE_EMPTY){

            if(holder.getAdapterPosition()==0){
                Post post = (Post)list.get(holder.getAdapterPosition());
                SonataUser user = post.getUser();

                holder.postpostdate.setText(DateUtils.getRelativeTimeSpanString(post.getCreatedAt().getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
                holder.postpostdesc.setLinkText("");
                holder.postpostdesc.setVisibility(View.GONE);

                if(post.getCommentnumber()==1){
                    holder.posthowmanyreplies.setText(post.getCommentnumber()+" "+holder.itemView.getContext().getString(R.string.comments2tekil));
                }
                else{
                    holder.posthowmanyreplies.setText(post.getCommentnumber()+" "+holder.itemView.getContext().getString(R.string.comments2cogul));
                }


                if(post.getBoolean("commentable")){
                    holder.postcommentnumber.setText(GenelUtil.ConvertNumber((int)post.getCommentnumber(),holder.itemView.getContext()));
                }
                else{
                    holder.postcommentnumber.setText(holder.itemView.getContext().getString(R.string.disabledcomment));
                }

                if(!post.getLiked()){
                    holder.postlikeimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_like));
                }
                else{
                    holder.postlikeimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_like_red));
                }

                if(post.getLikenumber()>=0){
                    holder.postlikenumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),holder.itemView.getContext()));
                }
                else{
                    post.setLikenumber(0);
                    holder.postlikenumber.setText("0");
                }


                if(user.getHasPp()){
                    glide.load(user.getPPAdapter())
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(new ColorDrawable(ContextCompat.getColor(holder.postprofilephoto.getContext(), R.color.placeholder_gray)))
                            .into(holder.postprofilephoto);
                }
                else{
                    glide.load(holder.postprofilephoto.getContext().getResources().getDrawable(R.drawable.emptypp)).into(holder.postprofilephoto);

                }



                holder.postname.setText(user.getName());
                holder.postusername.setText("@"+user.getUsername());







                holder.textLayout.setVisibility(View.GONE);
                String description = post.getDescription().trim();
                if(!description.equals("")){
                    if(!holder.postpostdesc.getPostId().equals(post.getObjectId())){
                        holder.postpostdesc.setPostId(post.getObjectId());
                        holder.postpostdesc.setMaxLines(3);
                    }
                    holder.postpostdesc.setLinkText(description);
                    holder.postpostdesc.setVisibility(View.VISIBLE);
                    holder.textLayout.setVisibility(View.VISIBLE);
                }

                if(getItemViewType(holder.getAdapterPosition())==POST_TYPE_LINK){
                    holder.postlinkdesc.setText("");
                    holder.postlinkurl.setText("");
                    holder.postlinktitle.setText("");
                    String url=post.getUrl();
                    String linkimageurl=post.getLinkimageurl().trim();
                    glide.load(linkimageurl).fitCenter().apply(new RequestOptions().override(180,150)).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.postlinkimagelayout.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(holder.postlinkimage);


                    String linktitle=post.getLinktitle().trim();
                    holder.postlinktitle.setText(linktitle);
                    String linkdesc = post.getLinkdesc();
                    if(linkdesc.trim().length()<1){
                        holder.postlinkdesc.setVisibility(View.GONE);
                        holder.postlinktitle.setMaxLines(4);
                    }
                    else{
                        holder.postlinkdesc.setText(linkdesc);
                    }

                    holder.postlinkurl.setText(url);




                }
                else if(getItemViewType(holder.getAdapterPosition())==POST_TYPE_IMAGE){

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
                        glide.load(url).fitCenter().thumbnail(glide.load(thumburl)).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).addListener(new RequestListener<Drawable>() {
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
                            glide.load(url).fitCenter().override(iw,ih).thumbnail(glide.load(thumburl)).addListener(new RequestListener<Drawable>() {
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
                            glide.load(url).fitCenter().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).thumbnail(glide.load(thumburl)).addListener(new RequestListener<Drawable>() {
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

                else if(getItemViewType(holder.getAdapterPosition())==POST_TYPE_IMAGE1){

                    String url1=post.getMainMedia1().getUrl();
                    String url=post.getMainMedia().getUrl();
                    String thumburl=post.getThumbMedia().getUrl();
                    String thumburl1=post.getThumbMedia1().getUrl();

                    glide.load(url).fitCenter()
                            .thumbnail(glide.load(thumburl))
                            .into(holder.postimage);
                    glide.load(url1).fitCenter()
                            .thumbnail(glide.load(thumburl1))
                            .into(holder.postimage1);

                }
                else if(getItemViewType(holder.getAdapterPosition())==POST_TYPE_IMAGE2){
                    String url=post.getMainMedia().getUrl();
                    String url1=post.getMainMedia1().getUrl();
                    String url2=post.getMainMedia2().getUrl();

                    String thumburl=post.getThumbMedia().getUrl();
                    String thumburl1=post.getThumbMedia1().getUrl();
                    String thumburl2=post.getThumbMedia2().getUrl();

                    glide.load(url).fitCenter()
                            .thumbnail(glide.load(thumburl))
                            .into(holder.postimage);
                    glide.load(url1).fitCenter()
                            .thumbnail(glide.load(thumburl1))
                            .into(holder.postimage1);
                    glide.load(url2).fitCenter()
                            .thumbnail(glide.load(thumburl2))
                            .into(holder.postimage2);

                }
                else if(getItemViewType(holder.getAdapterPosition())==POST_TYPE_IMAGE3){
                    String url=post.getMainMedia().getUrl();
                    String url1=post.getMainMedia1().getUrl();
                    String url2=post.getMainMedia2().getUrl();
                    String url3=post.getMainMedia3().getUrl();

                    String thumburl=post.getThumbMedia().getUrl();
                    String thumburl1=post.getThumbMedia1().getUrl();
                    String thumburl2=post.getThumbMedia2().getUrl();
                    String thumburl3=post.getThumbMedia3().getUrl();

                    glide.load(url).fitCenter()
                            .thumbnail(glide.load(thumburl))
                            .into(holder.postimage);
                    glide.load(url1).fitCenter()
                            .thumbnail(glide.load(thumburl1))
                            .into(holder.postimage1);
                    glide.load(url2).fitCenter()
                            .thumbnail(glide.load(thumburl2))
                            .into(holder.postimage2);

                    glide.load(url3).fitCenter()
                            .thumbnail(glide.load(thumburl3))
                            .into(holder.postimage3);

                }
                else if(getItemViewType(holder.getAdapterPosition())==POST_TYPE_VIDEO){




                    if(((float)post.getRatioH()/(float)post.getRatioW())>(float)4f/3f){
                        holder.ratiolayout.setAspectRatio(3,4);
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



                    JZDataSource jzDataSource = new JZDataSource(MyApp.getProxy(holder.videoPlayer.getContext()).getProxyUrl(url));
                    jzDataSource.looping=true;

                    glide.load(thumburl).fitCenter().into(holder.videoPlayer.posterImageView);
                    holder.videoPlayer.setUp(jzDataSource,Jzvd.SCREEN_NORMAL, JZMediaExo.class);



                }

            }
            else{
                //Comment
                Comment post =(Comment) list.get(holder.getAdapterPosition());


                holder.commentpostdate.setText(DateUtils.getRelativeTimeSpanString(post.getCreatedAt().getTime()-5000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));





                holder.commentupvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_upvote));
                holder.commentdownvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_downvote));
                holder.commentvotecount.setTextColor(Color.parseColor("#999999"));
                if(post.getUpvote()){
                    holder.commentupvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_upvote_blue));
                    holder.commentdownvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_downvote));

                    holder.commentvotecount.setTextColor(Color.parseColor("#2d72bc"));


                }
                if(post.getDownvote()){
                    holder.commentdownvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_downvote_red));
                    holder.commentupvoteimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_upvote));

                    holder.commentvotecount.setTextColor(Color.parseColor("#a64942"));
                }
                if(post.getVote()<0){
                    holder.commentvotecount.setText("-"+GenelUtil.ConvertNumber((int)post.getVote()*(-1),holder.itemView.getContext()));

                }
                if(post.getVote()>=0){
                    holder.commentvotecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),holder.itemView.getContext()));

                }








                if(post.getString("isreply").equals("false")){
                    if(post.getReplyCount()>0){
                        holder.commentshowreplies.setVisibility(View.VISIBLE);
                        holder.commentreplytext.setText(holder.itemView.getContext().getString(R.string.showreplies)+" ("+post.getReplyCount()+")");



                    }
                    if(post.getReplyCount()<=0){
                        holder.commentshowreplies.setVisibility(View.GONE);
                    }
                }

                SonataUser user = post.getUser();

                if(user.getHasPp()){
                    glide.load(user.getPPAdapter())
                            .placeholder(new ColorDrawable(ContextCompat.getColor(holder.commentprofilephoto.getContext(), R.color.placeholder_gray)))
                            .into(holder.commentprofilephoto);
                }
                else{
                    glide.load(holder.itemView.getContext().getResources().getDrawable(R.drawable.emptypp)).into(holder.commentprofilephoto);

                }

                holder.commentname.setText(user.getName());
                holder.commentusername.setText("@"+user.getUsername());



                holder.commentpostdesc.setVisibility(View.GONE);
                holder.readMore.setVisibility(View.GONE);
                if(post.getDesc().trim().length()>0){
                    if(!holder.commentpostdesc.getPostId().equals(post.getObjectId())){
                        holder.commentpostdesc.setPostId(post.getObjectId());
                        holder.commentpostdesc.setMaxLines(3);
                    }
                    holder.commentpostdesc.setLinkText(post.getDesc().trim());
                    holder.commentpostdesc.setVisibility(View.VISIBLE);
                }

                if(getItemViewType(holder.getAdapterPosition())==COMMENT_TYPE_IMAGE){

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
                        glide.load(url).thumbnail(glide.load(thumburl)).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).addListener(new RequestListener<Drawable>() {
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
                            glide.load(url).override(iw,ih).thumbnail(glide.load(thumburl)).addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    //holder.imageprogress.setVisibility(View.INVISIBLE);
                                    //holder.reloadlayout.setVisibility(View.VISIBLE);
                                    Log.e("glideError",e.getMessage());
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    holder.imageprogress.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            }).into(holder.postimage);
                        }
                        else{
                            //holder.nsfwIcon.setVisibility(View.INVISIBLE);
                            glide.load(url).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).thumbnail(glide.load(thumburl)).addListener(new RequestListener<Drawable>() {
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

        ImageView commentupvoteimage,commentdownvoteimage;
        ImageView commentprofilephoto,postimage,postimage1,postimage2,postimage3;
        TextView commentname,commentusername,commentpostdate,commentvotecount,commentreplytext;
        SocialTextView commentpostdesc;
        ProgressBar imageprogress;
        AspectRatioLayout ratiolayout;
        RelativeLayout reloadripple;
        RoundKornerRelativeLayout reloadlayout;
        RelativeLayout commentoptions,commentupvote,commentdownvote,commentreply,commentshowreplies;


        TextView readMore;
        RelativeLayout textLayout;

        //PostHolder
        ImageView postprofilephoto,postlinkimage,postlikeimage,nsfwIcon;
        TextView postname,postusername,postlinktitle,postlinkdesc,postlinkurl,postlikenumber,postcommentnumber,postpostdate,posthowmanyreplies;
        RelativeLayout postoptions,postlike,postcomment;
        AspectRatioLayout postlinkimagelayout;
        com.tylersuehr.socialtextview.SocialTextView postpostdesc;
        RoundKornerRelativeLayout postpplayout;
        RelativeLayout postlinkripple;
        VideoPlayer videoPlayer;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posthowmanyreplies=itemView.findViewById(R.id.commentreplyfirst);
            postpplayout = itemView.findViewById(R.id.pplayout);
            postlinkimagelayout=itemView.findViewById(R.id.linkimagelayout);
            postprofilephoto = itemView.findViewById(R.id.postprofilephoto);
            postname = itemView.findViewById(R.id.postnametext);
            postusername = itemView.findViewById(R.id.postusernametext);
            postoptions = itemView.findViewById(R.id.postopripple);
            postpostdesc = itemView.findViewById(R.id.postdesc);
            postlike = itemView.findViewById(R.id.postlikeripple);
            postcomment = itemView.findViewById(R.id.postcommentripple);
            postlinktitle = itemView.findViewById(R.id.linktitle);
            postlinkdesc = itemView.findViewById(R.id.linkdesc);
            postlinkurl = itemView.findViewById(R.id.linkurl);
            postlinkimage = itemView.findViewById(R.id.linkimage);
            postlikenumber = itemView.findViewById(R.id.postlikenumber);
            postcommentnumber = itemView.findViewById(R.id.postcommentnumber);
            postpostdate = itemView.findViewById(R.id.postdate);
            postlikeimage = itemView.findViewById(R.id.postlikeicon);
            postlinkripple=itemView.findViewById(R.id.postlinkripple);
            videoPlayer = itemView.findViewById(R.id.masterExoPlayer);


            nsfwIcon = itemView.findViewById(R.id.postImageNsfwIcon);


            commentreplytext=itemView.findViewById(R.id.commentshowreplytext);
            commentshowreplies=itemView.findViewById(R.id.commentshowrepliesripple);
            commentreply=itemView.findViewById(R.id.commentreplyripple);

            postimage = itemView.findViewById(R.id.postimageview);
            postimage1 = itemView.findViewById(R.id.postimageview1);
            postimage2 = itemView.findViewById(R.id.postimageview2);
            postimage3 = itemView.findViewById(R.id.postimageview3);

            imageprogress = itemView.findViewById(R.id.postimageprogressbar);
            ratiolayout = itemView.findViewById(R.id.postratiolayout);
            reloadripple = itemView.findViewById(R.id.postfailimageripple);
            reloadlayout=itemView.findViewById(R.id.postimagefail);
            commentvotecount = itemView.findViewById(R.id.commentvotecounttext);
            commentoptions = itemView.findViewById(R.id.commentoptions);
            commentupvote = itemView.findViewById(R.id.commentupvoteripple);
            commentdownvote = itemView.findViewById(R.id.commentdownvoteripple);
            commentupvoteimage = itemView.findViewById(R.id.commentupvoteimage);
            commentdownvoteimage = itemView.findViewById(R.id.commentdownvoteimage);
            commentprofilephoto = itemView.findViewById(R.id.commentppimage);
            commentname = itemView.findViewById(R.id.commentnametext);
            commentusername = itemView.findViewById(R.id.commentusernametext);
            commentpostdesc = itemView.findViewById(R.id.commentdesc);
            commentpostdate = itemView.findViewById(R.id.commentdate);

            readMore = itemView.findViewById(R.id.readMoreText);
            textLayout = itemView.findViewById(R.id.textLayout);


            if(readMore!=null){
                readMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(commentpostdesc != null){
                            if(commentpostdesc.getLineCount()>commentpostdesc.getMaxLines()){
                                commentpostdesc.setMaxLines(Integer.MAX_VALUE);
                                commentpostdesc.setEllipsize(null);
                                if(readMore!=null){
                                    readMore.setVisibility(View.GONE);
                                }

                            }
                            else if(commentpostdesc.getLineCount()<=commentpostdesc.getMaxLines()){
                                commentpostdesc.setMaxLines(3);
                                commentpostdesc.setEllipsize(null);
                                if(commentpostdesc.getLineCount()>3){
                                    if(readMore!=null){
                                        readMore.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        else{
                            if(postpostdesc != null){
                                if(postpostdesc.getLineCount()>postpostdesc.getMaxLines()){
                                    postpostdesc.setMaxLines(Integer.MAX_VALUE);
                                    postpostdesc.setEllipsize(null);
                                    if(readMore!=null){
                                        readMore.setVisibility(View.GONE);
                                    }

                                }
                                else if(postpostdesc.getLineCount()<=postpostdesc.getMaxLines()){
                                    postpostdesc.setMaxLines(3);
                                    postpostdesc.setEllipsize(null);
                                    if(postpostdesc.getLineCount()>3){
                                        if(readMore!=null){
                                            readMore.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        }


                    }
                });
            }

            if(commentprofilephoto!=null){
                commentprofilephoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onGoToProfileClick(getAdapterPosition());
                    }
                });
            }

            if(commentname!=null){
                commentname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onGoToProfileClick(getAdapterPosition());
                    }
                });
            }

            if(commentusername!=null){
                commentusername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onGoToProfileClick(getAdapterPosition());
                    }
                });
            }

            if(commentshowreplies!=null){
                commentshowreplies.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onCommentShowReplies(getAdapterPosition());
                    }
                });
            }

            if(commentupvote!=null){
                commentupvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onCommentUpvoteClick(getAdapterPosition(),commentupvoteimage,commentdownvoteimage,commentvotecount);
                    }
                });
            }

            if(commentdownvote!=null){
                commentdownvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onCommentDownvoteClick(getAdapterPosition(),commentupvoteimage,commentdownvoteimage,commentvotecount);
                    }
                });
            }

            if(commentreply!=null){
                commentreply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onCommentReply(getAdapterPosition());
                    }
                });
            }

            if(commentoptions!=null){
                commentoptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onCommentOptionsClick(getAdapterPosition());
                    }
                });
            }

            if(commentpostdesc!=null){
                commentpostdesc.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
                    @Override
                    public void onLinkClicked(int i, String s) {
                        recyclerViewClick.onCommentSocialClick(getAdapterPosition(),i,s);

                    }
                });
                commentpostdesc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GenelUtil.ToastLong(itemView.getContext(),postdesc.getLineCount()+"");
                        if(commentpostdesc.getLineCount()>commentpostdesc.getMaxLines()){
                            commentpostdesc.setMaxLines(Integer.MAX_VALUE);
                            commentpostdesc.setEllipsize(null);
                            if(readMore!=null){
                                readMore.setVisibility(View.GONE);
                            }

                        }
                        else if(commentpostdesc.getLineCount()<=commentpostdesc.getMaxLines()){
                            commentpostdesc.setMaxLines(3);
                            commentpostdesc.setEllipsize(null);
                            if(commentpostdesc.getLineCount()>3){
                                if(readMore!=null){
                                    readMore.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
                commentpostdesc.setLineCountListener(new SocialTextView.LineCountListener() {
                    @Override
                    public void onLineCount(int lineCount) {
                        Log.e("Position",getAdapterPosition()+"");
                        Log.e("line count",lineCount+"");
                        Log.e("max line count",commentpostdesc.getMaxLines()+"");
                        if(lineCount > commentpostdesc.getMaxLines()){
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


            if(postoptions!=null){
                postoptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onPostOptionsClick(getAdapterPosition(),postcommentnumber);
                    }
                });
            }

            if(postpostdesc!=null){
                postpostdesc.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
                    @Override
                    public void onLinkClicked(int i, String s) {
                        recyclerViewClick.onPostSocialClick(getAdapterPosition(),i,s);
                    }
                });
                postpostdesc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GenelUtil.ToastLong(itemView.getContext(),postdesc.getLineCount()+"");
                        if(postpostdesc.getLineCount()>postpostdesc.getMaxLines()){
                            postpostdesc.setMaxLines(Integer.MAX_VALUE);
                            postpostdesc.setEllipsize(null);
                            if(readMore!=null){
                                readMore.setVisibility(View.GONE);
                            }

                        }
                        else if(postpostdesc.getLineCount()<=postpostdesc.getMaxLines()){
                            postpostdesc.setMaxLines(3);
                            postpostdesc.setEllipsize(null);
                            if(postpostdesc.getLineCount()>3){
                                if(readMore!=null){
                                    readMore.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
                postpostdesc.setLineCountListener(new SocialTextView.LineCountListener() {
                    @Override
                    public void onLineCount(int lineCount) {
                        if(lineCount > postpostdesc.getMaxLines()){
                            if(readMore != null){
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

            if(postlike!=null){
                postlike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onPostLikeClick(getAdapterPosition(),postlikeimage,postlikenumber);
                    }
                });
            }

            if(postcomment!=null){
                postcomment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onPostOpenComments(getAdapterPosition());
                    }
                });
            }

            if(postprofilephoto!=null){
                postprofilephoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onGoToProfileClick(getAdapterPosition());
                    }
                });
            }

            if(postname!=null){
                postname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onGoToProfileClick(getAdapterPosition());
                    }
                });
            }

            if(postusername!=null){
                postusername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onGoToProfileClick(getAdapterPosition());
                    }
                });
            }

            if(postlinkripple!=null){
                postlinkripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onPostLinkClick(getAdapterPosition());
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
            if(postimage1!=null){
                postimage1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onImageClick(getAdapterPosition(),postimage1,1);
                    }
                });
            }

            if(postimage2!=null){
                postimage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onImageClick(getAdapterPosition(),postimage2,2);
                    }
                });
            }
            if(postimage3!=null){
                postimage3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onImageClick(getAdapterPosition(),postimage3,3);
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
