package com.sonata.socialapp.utils.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.ParseFile;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.VideoUtils.JZMediaExo;
import com.sonata.socialapp.utils.VideoUtils.VideoPlayer;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;
import com.takwolf.android.aspectratio.AspectRatioLayout;
import com.tylersuehr.socialtextview.SocialTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import jp.wasabeef.glide.transformations.BlurTransformation;


public class ProfilAdapter extends RecyclerView.Adapter<ProfilAdapter.ViewHolder> {




    List<ListObject> list;
    RequestManager glide;

    boolean finish = false;
    public void setFinish(boolean finish){
        this.finish=finish;
    }
    SonataUser user;

    RecyclerViewClick recyclerViewClick;
    public void setContext(List<ListObject> list
            ,RequestManager glide,RecyclerViewClick recyclerViewClick,SonataUser user){
        this.recyclerViewClick=recyclerViewClick;
        this.list=list;
        this.glide=glide;
        this.user = user;


    }


    private static final int POST_TYPE_TEXT = 1;
    private static final int POST_TYPE_IMAGE = 2;
    private static final int POST_TYPE_IMAGE1 = 2589;
    private static final int POST_TYPE_IMAGE2 = 24758;
    private static final int POST_TYPE_IMAGE3 = 21256;
    private static final int POST_TYPE_VIDEO = 22;
    private static final int POST_TYPE_LINK = 4;
    private static final int POST_TYPE_EMPTY = 5;
    private static final int POST_TYPE_LOAD = 6;
    private static final int POST_TYPE_AD = 7;
    private static final int POST_TYPE_TOP = 8;

    public static final int TYPE_HASHTAG = 1;
    public static final int TYPE_LINK = 16;
    public static final int TYPE_MENTION = 2;


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int i) {
        switch (list.get(i).getType()) {
            case "text":
                return POST_TYPE_TEXT;
            case "image":
                if(list.get(i).getPost().getImageCount()==1){
                    return POST_TYPE_IMAGE;
                }
                else if(list.get(i).getPost().getImageCount()==2){
                    return POST_TYPE_IMAGE1;
                }
                else if(list.get(i).getPost().getImageCount()==3){
                    return POST_TYPE_IMAGE2;
                }
                else if(list.get(i).getPost().getImageCount()==4){
                    return POST_TYPE_IMAGE3;
                }
                else if(list.get(i).getPost().getImageCount()==0){
                    return POST_TYPE_IMAGE;
                }
            case "video":
                return POST_TYPE_VIDEO;
            case "link":
                return POST_TYPE_LINK;
            case "reklam":
                return POST_TYPE_AD;
            case "load":
                return POST_TYPE_LOAD;
            default:
                return POST_TYPE_EMPTY;
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.postimage != null) {
                glide.clear(holder.postimage);
                holder.postimage.setImageDrawable(null);
                holder.postimage.setImageBitmap(null);

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
        if(finish){
            if(holder.adView!=null){
                holder.adView.destroy();
            }
        }
        if (holder.linkimage != null) {
                glide.clear(holder.linkimage);
                holder.linkimage.setImageDrawable(null);
                holder.linkimage.setImageBitmap(null);

        }





        if (holder.profilephoto != null) {
                glide.clear(holder.profilephoto);
                holder.profilephoto.setImageDrawable(null);
                holder.profilephoto.setImageBitmap(null);


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
    public ProfilAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==POST_TYPE_TEXT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_text_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_image_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE1){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_image_layout_1,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE2){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_image_layout_2,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE3){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_image_layout_3,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_VIDEO){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_video_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_LINK){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_link_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_AD){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.admobreklam,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_LOAD){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_load,viewGroup,false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view_real,viewGroup,false);
            return new ViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ProfilAdapter.ViewHolder holder, int positiona) {
        if(getItemViewType(holder.getAdapterPosition())!=POST_TYPE_TOP&&getItemViewType(holder.getAdapterPosition())!=POST_TYPE_LOAD&&getItemViewType(holder.getAdapterPosition())!=POST_TYPE_AD&&getItemViewType(holder.getAdapterPosition())!=POST_TYPE_EMPTY){

            if(list.get(holder.getAdapterPosition()).getPost().getIsDeleted()){
                if (holder.postimage != null) {
                    if(holder.postimage.getVisibility()== View.VISIBLE){
                        glide.clear(holder.postimage);
                        holder.postimage.setImageDrawable(null);
                        holder.postimage.setImageBitmap(null);
                    }
                }

                if (holder.linkimage != null) {
                    if(holder.linkimage.getVisibility()== View.VISIBLE){
                        glide.clear(holder.linkimage);
                        holder.linkimage.setImageDrawable(null);
                        holder.linkimage.setImageBitmap(null);
                    }
                }






                if (holder.profilephoto != null) {
                    if(holder.profilephoto.getVisibility()== View.VISIBLE){
                        glide.clear(holder.profilephoto);
                        holder.profilephoto.setImageDrawable(null);
                        holder.profilephoto.setImageBitmap(null);
                    }

                }
                list.remove(holder.getAdapterPosition());

                //notifyItemRemoved(holder.getAdapterPosition());

            }
            else{
                Post post = list.get(holder.getAdapterPosition()).getPost();


                holder.postdate.setText(DateUtils.getRelativeTimeSpanString(post.getCreatedAt().getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
                holder.postdesc.setLinkText("");
                holder.postdesc.setVisibility(View.GONE);




                if(post.getBoolean("commentable")){
                    holder.commentnumber.setText(GenelUtil.ConvertNumber((int)post.getCommentnumber(),holder.itemView.getContext()));
                }
                else{
                    holder.commentnumber.setText(holder.itemView.getContext().getString(R.string.disabledcomment));
                }

                if(!post.getLiked()){
                    holder.likeimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_like));
                }
                else{
                    holder.likeimage.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_like_red));
                }

                if(post.getLikenumber()>=0){
                    holder.likenumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),holder.itemView.getContext()));
                }
                else{
                    post.setLikenumber(0);
                    holder.likenumber.setText("0");
                }


                if(user.getHasPp()){
                    glide.load(user.getPPAdapter())
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(new ColorDrawable(ContextCompat.getColor(holder.profilephoto.getContext(), R.color.placeholder_gray)))
                            .into(holder.profilephoto);
                }
                else{
                    glide.load(holder.profilephoto.getContext().getResources().getDrawable(R.drawable.emptypp)).into(holder.profilephoto);

                }



                holder.name.setText(user.getName());
                holder.username.setText("@"+user.getUsername());





                holder.textLayout.setVisibility(View.GONE);
                String description = post.getDescription().trim();
                if(!description.equals("")){
                    if(!holder.postdesc.getPostId().equals(post.getObjectId())){
                        holder.postdesc.setPostId(post.getObjectId());
                        holder.postdesc.setMaxLines(3);
                    }
                    holder.postdesc.setLinkText(description);
                    holder.postdesc.setVisibility(View.VISIBLE);
                    holder.textLayout.setVisibility(View.VISIBLE);
                }

                int viewType = getItemViewType(holder.getAdapterPosition());
                if(viewType==POST_TYPE_IMAGE){

                    List<HashMap> mediaList = post.getMediaList();
                    HashMap media = mediaList.get(0);
                    try {

                        int width = (int) media.get("width");
                        int height = (int) media.get("height");
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                        if(((float)height/(float)width)>1.4f){
                            holder.ratiolayout.setAspectRatio(10,14);
                        }
                        else{
                            if(((float)height/(float)width)<0.4f){
                                holder.ratiolayout.setAspectRatio(10,4);
                            }
                            else{
                                holder.ratiolayout.setAspectRatio(width,height);
                            }
                        }

                        String url=mainMedia.getUrl();

                        String thumburl=thumbnail.getUrl();

                        holder.nsfwIcon.setVisibility(View.INVISIBLE);
                        if(height>1280||width>1280){
                            int ih = 1280;
                            int iw = 1280;
                            if(height>width){
                                //ih = 1280;
                                iw = 1280 * (int) (width/height);
                            }
                            else{
                                //iw = 1280;
                                ih = 1280 * (int) (height/width);
                            }
                            glide.load(url).fitCenter().override(iw,ih).thumbnail(glide.load(thumburl)).addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    //holder.imageprogress.setVisibility(View.INVISIBLE);
                                    //holder.reloadlayout.setVisibility(View.VISIBLE);
                                    //Log.e("glideError",e.getMessage());
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else if(viewType==POST_TYPE_IMAGE1){

                    List<HashMap> mediaList = post.getMediaList();


                    try {
                        HashMap media = mediaList.get(0);
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                        String url = mainMedia.getUrl();
                        String thumburl = thumbnail.getUrl();

                        glide.load(url).fitCenter()
                                .thumbnail(glide.load(thumburl))
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(holder.postimage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }




                    try {
                        HashMap media = mediaList.get(1);
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                        String url = mainMedia.getUrl();
                        String thumburl = thumbnail.getUrl();

                        glide.load(url).fitCenter()
                                .thumbnail(glide.load(thumburl))
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(holder.postimage1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else if(viewType==POST_TYPE_IMAGE2){

                    List<HashMap> mediaList = post.getMediaList();

                    try {
                        HashMap media = mediaList.get(0);
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                        String url = mainMedia.getUrl();
                        String thumburl = thumbnail.getUrl();

                        glide.load(url).fitCenter()
                                .thumbnail(glide.load(thumburl))
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(holder.postimage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        HashMap media = mediaList.get(1);
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                        String url = mainMedia.getUrl();
                        String thumburl = thumbnail.getUrl();

                        glide.load(url).fitCenter()
                                .thumbnail(glide.load(thumburl))
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(holder.postimage1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        HashMap media = mediaList.get(2);
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                        String url = mainMedia.getUrl();
                        String thumburl = thumbnail.getUrl();

                        glide.load(url).fitCenter()
                                .thumbnail(glide.load(thumburl))
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(holder.postimage2);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(viewType==POST_TYPE_IMAGE3){
                    List<HashMap> mediaList = post.getMediaList();

                    try {
                        HashMap media = mediaList.get(0);
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                        String url = mainMedia.getUrl();
                        String thumburl = thumbnail.getUrl();

                        glide.load(url).fitCenter()
                                .thumbnail(glide.load(thumburl))
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(holder.postimage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        HashMap media = mediaList.get(1);
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                        String url = mainMedia.getUrl();
                        String thumburl = thumbnail.getUrl();

                        glide.load(url).fitCenter()
                                .thumbnail(glide.load(thumburl))
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(holder.postimage1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        HashMap media = mediaList.get(2);
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                        String url = mainMedia.getUrl();
                        String thumburl = thumbnail.getUrl();

                        glide.load(url).fitCenter()
                                .thumbnail(glide.load(thumburl))
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(holder.postimage2);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        HashMap media = mediaList.get(3);
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                        String url = mainMedia.getUrl();
                        String thumburl = thumbnail.getUrl();

                        glide.load(url).fitCenter()
                                .thumbnail(glide.load(thumburl))
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(holder.postimage3);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(viewType==POST_TYPE_VIDEO){

                    List<HashMap> mediaList = post.getMediaList();
                    HashMap media = mediaList.get(0);

                    try {
                        int width = (int) media.get("width");
                        int height = (int) media.get("height");
                        ParseFile mainMedia = (ParseFile) media.get("media");
                        ParseFile thumbnail = (ParseFile) media.get("thumbnail");
                        ParseFile thumbnailsmall = (ParseFile) media.get("thumbnail2");

                        if(((float)height/(float)width)>(float)4f/3f){
                            holder.ratiolayout.setAspectRatio(3,4);
                        }
                        else{
                            if(((float)height/(float)width)<0.4f){
                                holder.ratiolayout.setAspectRatio(10,4);
                            }
                            else{
                                holder.ratiolayout.setAspectRatio(width,height);
                            }
                        }

                        String url=mainMedia.getUrl();
                        String thumburl=thumbnail.getUrl();
                        String thumburl2=thumbnailsmall.getUrl();



                        JZDataSource jzDataSource = new JZDataSource(MyApp.getProxy(holder.videoPlayer.getContext()).getProxyUrl(url));
                        jzDataSource.looping=true;

                        glide.load(thumburl).thumbnail(glide.load(thumburl2)).addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(holder.videoPlayer.posterImageView);
                        holder.videoPlayer.setUp(jzDataSource,Jzvd.SCREEN_NORMAL, JZMediaExo.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }










                }


            }

        }

        else if(getItemViewType(holder.getAdapterPosition())==POST_TYPE_AD){
            if(finish){
                holder.adView.destroy();
            }
            else{
                holder.adView.setMediaView(holder.adView.findViewById(R.id.ad_media));
                holder.adView.setHeadlineView(holder.adView.findViewById(R.id.ad_headline));
                holder.adView.setBodyView(holder.adView.findViewById(R.id.ad_body));
                holder.adView.setCallToActionView(holder.adView.findViewById(R.id.ad_call_to_action));
                holder.adView.setIconView(holder.adView.findViewById(R.id.ad_icon));
                holder.adView.setPriceView(holder.adView.findViewById(R.id.ad_price));
                holder.adView.setStarRatingView(holder.adView.findViewById(R.id.ad_stars));
                holder.adView.setStoreView(holder.adView.findViewById(R.id.ad_store));
                holder.adView.setAdvertiserView(holder.adView.findViewById(R.id.ad_advertiser));

                NativeAd nativeAd = list.get(holder.getAdapterPosition()).getAd();
                ((TextView) holder.adView.getHeadlineView()).setText(nativeAd.getHeadline());
                ((TextView) holder.adView.getBodyView()).setText(nativeAd.getBody());
                ((Button) holder.adView.getCallToActionView()).setText(nativeAd.getCallToAction());

                // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
                // check before trying to display them.
                NativeAd.Image icon = nativeAd.getIcon();
                holder.adView.getMediaView().setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
                    @Override
                    public void onChildViewAdded(View view, View view1) {
                        if (view1 instanceof ImageView) {
                            ImageView imageView = (ImageView) view1;
                            imageView.setAdjustViewBounds(true);
                        }
                    }

                    @Override
                    public void onChildViewRemoved(View view, View view1) {

                    }
                });
                if (icon == null) {
                    ((ImageView) holder.adView.getIconView()).setBackgroundColor(Color.parseColor("#999999"));
                    holder.adView.getIconView().setVisibility(View.VISIBLE);
                } else {
                    ((ImageView) holder.adView.getIconView()).setImageDrawable(icon.getDrawable());
                    holder.adView.getIconView().setVisibility(View.VISIBLE);
                }

                if (nativeAd.getPrice() == null) {
                    holder.adView.getPriceView().setVisibility(View.GONE);
                } else {
                    holder.adView.getPriceView().setVisibility(View.VISIBLE);
                    ((TextView) holder.adView.getPriceView()).setText(nativeAd.getPrice());
                }

                if (nativeAd.getStore() == null) {
                    holder.adView.getStoreView().setVisibility(View.GONE);
                } else {
                    holder.adView.getStoreView().setVisibility(View.VISIBLE);
                    ((TextView) holder.adView.getStoreView()).setText(nativeAd.getStore());
                }

                if (nativeAd.getStarRating() == null) {
                    holder.adView.getStarRatingView().setVisibility(View.GONE);
                } else {
                    ((RatingBar) holder.adView.getStarRatingView())
                            .setRating(nativeAd.getStarRating().floatValue());
                    holder.adView.getStarRatingView().setVisibility(View.VISIBLE);
                }

                if (nativeAd.getAdvertiser() == null) {
                    holder.adView.getAdvertiserView().setVisibility(View.INVISIBLE);
                } else {
                    ((TextView) holder.adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                    holder.adView.getAdvertiserView().setVisibility(View.VISIBLE);
                }

                // Assign native ad object to the native view.
                holder.adView.setNativeAd(nativeAd);
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

        ImageView profilephoto,postimage,postimage1,postimage2,postimage3,linkimage,likeimage,nsfwIcon;
        TextView name,username,linktitle,linkdesc,linkurl,likenumber,commentnumber,postdate,readMore;
        ProgressBar imageprogress;
        RelativeLayout options,like,comment,reloadripple;
        RelativeLayout linkripple,textLayout;
        AspectRatioLayout ratiolayout,linkimagelayout;
        SocialTextView postdesc;
        RoundKornerRelativeLayout reloadlayout,pplayout;
        NativeAdView adView;

        private long mLastClickTime = 0;
        VideoPlayer videoPlayer;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoPlayer = itemView.findViewById(R.id.masterExoPlayer);

            nsfwIcon = itemView.findViewById(R.id.postImageNsfwIcon);
            adView = itemView.findViewById(R.id.ad_view);
            pplayout = itemView.findViewById(R.id.pplayout);
            linkimagelayout=itemView.findViewById(R.id.linkimagelayout);
            profilephoto = itemView.findViewById(R.id.postprofilephoto);
            name = itemView.findViewById(R.id.postnametext);
            username = itemView.findViewById(R.id.postusernametext);
            options = itemView.findViewById(R.id.postopripple);

            postimage = itemView.findViewById(R.id.postimageview);
            postimage1 = itemView.findViewById(R.id.postimageview1);
            postimage2 = itemView.findViewById(R.id.postimageview2);
            postimage3 = itemView.findViewById(R.id.postimageview3);

            imageprogress = itemView.findViewById(R.id.postimageprogressbar);
            ratiolayout = itemView.findViewById(R.id.postratiolayout);
            reloadripple = itemView.findViewById(R.id.postfailimageripple);
            reloadlayout=itemView.findViewById(R.id.postimagefail);
            postdesc = itemView.findViewById(R.id.postdesc);
            like = itemView.findViewById(R.id.postlikeripple);
            comment = itemView.findViewById(R.id.postcommentripple);
            linktitle = itemView.findViewById(R.id.linktitle);
            linkdesc = itemView.findViewById(R.id.linkdesc);
            linkurl = itemView.findViewById(R.id.linkurl);
            linkimage = itemView.findViewById(R.id.linkimage);
            likenumber = itemView.findViewById(R.id.postlikenumber);
            commentnumber = itemView.findViewById(R.id.postcommentnumber);
            postdate = itemView.findViewById(R.id.postdate);
            likeimage = itemView.findViewById(R.id.postlikeicon);
            linkripple=itemView.findViewById(R.id.postlinkripple);

            readMore = itemView.findViewById(R.id.readMoreText);
            textLayout = itemView.findViewById(R.id.textLayout);

            if(options!=null){
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onOptionsClick(getAdapterPosition(),commentnumber);
                    }
                });
            }

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

            if(postdesc!=null){

                postdesc.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
                    @Override
                    public void onLinkClicked(int i, String s) {
                        recyclerViewClick.onSocialClick(getAdapterPosition(),i,s);
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


            if(like!=null){
                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onLikeClick(getAdapterPosition(),likeimage,likenumber);
                    }
                });
            }

            if(comment!=null){
                comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onOpenComments(getAdapterPosition());
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

            if(linkripple!=null){
                linkripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 250){
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        //recyclerViewClick.onLinkClick(getAdapterPosition());
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
                        //recyclerViewClick.onReloadImageClick(getAdapterPosition(),reloadlayout,imageprogress,postimage);
                    }
                });
            }





        }

    }














}
