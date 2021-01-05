package com.sonata.socialapp.utils.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;
import com.takwolf.android.aspectratio.AspectRatioLayout;
import com.tylersuehr.socialtextview.SocialTextView;


import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {




    boolean mute = true;
    List<ListObject> list;
    RequestManager glide;


    RecyclerViewClick recyclerViewClick;
    public void setContext(List<ListObject> list
            ,RequestManager glide,RecyclerViewClick recyclerViewClick){
        this.recyclerViewClick=recyclerViewClick;
        this.list=list;
        this.glide=glide;

    }

    private static final int POST_TYPE_TEXT = 1;
    private static final int POST_TYPE_IMAGE = 2;
    private static final int POST_TYPE_VIDEO = 3;
    private static final int POST_TYPE_LINK = 4;
    private static final int POST_TYPE_EMPTY = 5;
    private static final int POST_TYPE_LOAD = 6;
    private static final int POST_TYPE_AD = 7;
    private static final int POST_TYPE_TOP = 8;

    public static final int TYPE_HASHTAG = 1;
    public static final int TYPE_LINK = 8;
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
                return POST_TYPE_IMAGE;
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
        if(holder.adView!=null){
            if(holder.adView.getIconView()!=null){
                glide.clear(holder.adView.getIconView());
                ((ImageView)holder.adView.getIconView()).setImageDrawable(null);
                ((ImageView)holder.adView.getIconView()).setImageBitmap(null);
            }
        }



    }





    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==POST_TYPE_TEXT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_text_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_image_layout,viewGroup,false);
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
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int positiona) {
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
                notifyDataSetChanged();

            }
            else{
                Post post = list.get(holder.getAdapterPosition()).getPost();
                SonataUser user = post.getUser();

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









            }

        }
        else if(getItemViewType(holder.getAdapterPosition())==POST_TYPE_AD){

                holder.adView.setMediaView(holder.adView.findViewById(R.id.ad_media));
                holder.adView.setHeadlineView(holder.adView.findViewById(R.id.ad_headline));
                holder.adView.setBodyView(holder.adView.findViewById(R.id.ad_body));
                holder.adView.setCallToActionView(holder.adView.findViewById(R.id.ad_call_to_action));
                holder.adView.setIconView(holder.adView.findViewById(R.id.ad_icon));
                holder.adView.setPriceView(holder.adView.findViewById(R.id.ad_price));
                holder.adView.setStarRatingView(holder.adView.findViewById(R.id.ad_stars));
                holder.adView.setStoreView(holder.adView.findViewById(R.id.ad_store));
                holder.adView.setAdvertiserView(holder.adView.findViewById(R.id.ad_advertiser));

                UnifiedNativeAd nativeAd = list.get(holder.getAdapterPosition()).getAd();
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
                    glide.load(holder.itemView.getContext().getResources().getDrawable(R.drawable.adicon))
                            .into(((ImageView) holder.adView.getIconView()));
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

        ImageView profilephoto,postimage,linkimage,likeimage,nsfwIcon;
        TextView name,username,linktitle,linkdesc,linkurl,likenumber,commentnumber,postdate;
        ProgressBar imageprogress;
        RelativeLayout options,like,comment,reloadripple;
        RelativeLayout linkripple;
        AspectRatioLayout ratiolayout,linkimagelayout;
        SocialTextView postdesc;
        RoundKornerRelativeLayout reloadlayout,pplayout;
        UnifiedNativeAdView adView;

        private long mLastClickTime = 0;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nsfwIcon = itemView.findViewById(R.id.postImageNsfwIcon);
            adView = itemView.findViewById(R.id.ad_view);
            pplayout = itemView.findViewById(R.id.pplayout);
            linkimagelayout=itemView.findViewById(R.id.linkimagelayout);
            profilephoto = itemView.findViewById(R.id.postprofilephoto);
            name = itemView.findViewById(R.id.postnametext);
            username = itemView.findViewById(R.id.postusernametext);
            options = itemView.findViewById(R.id.postopripple);
            postimage = itemView.findViewById(R.id.postimageview);
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


            if(options!=null){
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onOptionsClick(getAdapterPosition(),commentnumber);
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
