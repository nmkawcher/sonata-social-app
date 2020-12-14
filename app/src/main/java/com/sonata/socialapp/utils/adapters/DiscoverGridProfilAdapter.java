package com.sonata.socialapp.utils.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.GridClick;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class DiscoverGridProfilAdapter extends RecyclerView.Adapter<DiscoverGridProfilAdapter.ViewHolder> {




    private List<ListObject> list;
    private RequestManager glide;


    private GridClick recyclerViewClick;
    public void setContext(List<ListObject> list
            ,RequestManager glide,GridClick recyclerViewClick){
        this.recyclerViewClick=recyclerViewClick;
        this.list=list;
        this.glide=glide;



    }


    private static final int POST_TYPE_IMAGE = 2;
    private static final int POST_TYPE_IMAGE_START = 87;
    private static final int POST_TYPE_IMAGE_END = 97;
    private static final int POST_TYPE_VIDEO = 22;
    private static final int POST_TYPE_VIDEO_START = 12;
    private static final int POST_TYPE_VIDEO_END = 312;
    private static final int POST_TYPE_EMPTY = 5;
    private static final int POST_TYPE_LOAD = 6;
    private static final int POST_TYPE_PRIVATE = 7;

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

            case "image":
                if(i%3==0){
                    return POST_TYPE_IMAGE_START;
                }
                else if(i%3==1){
                    return POST_TYPE_IMAGE;
                }
                else{
                    return POST_TYPE_IMAGE_END;
                }

            case "video":
                if(i%3==0){
                    return POST_TYPE_VIDEO_START;
                }
                else if(i%3==1){
                    return POST_TYPE_VIDEO;
                }
                else{
                    return POST_TYPE_VIDEO_END;
                }
            case "load":
                return POST_TYPE_LOAD;
            case "private":
                return POST_TYPE_PRIVATE;
            default:
                return POST_TYPE_EMPTY;
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.image != null) {
                glide.clear(holder.image);
                holder.image.setImageDrawable(null);
                holder.image.setImageBitmap(null);

        }
    }





    @NonNull
    @Override
    public DiscoverGridProfilAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i==POST_TYPE_IMAGE||i==POST_TYPE_VIDEO){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_post_layout_middle,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE_START||i==POST_TYPE_VIDEO_START){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_post_layout_start,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_IMAGE_END||i==POST_TYPE_VIDEO_END){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_post_layout_end,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_LOAD){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_load,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==POST_TYPE_PRIVATE){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.private_profile,viewGroup,false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view_real,viewGroup,false);
            return new ViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverGridProfilAdapter.ViewHolder holder, int positiona) {
        if(getItemViewType(holder.getAdapterPosition())!=POST_TYPE_PRIVATE
                &&getItemViewType(holder.getAdapterPosition())!=POST_TYPE_LOAD
                &&getItemViewType(holder.getAdapterPosition())!=POST_TYPE_EMPTY){

            Post post = list.get(holder.getAdapterPosition()).getPost();
            int type = getItemViewType(holder.getAdapterPosition());
            if(type==POST_TYPE_IMAGE||type==POST_TYPE_IMAGE_START||type==POST_TYPE_IMAGE_END){
                holder.videoIcon.setVisibility(View.INVISIBLE);
                if(post.getImageCount()>1){
                    holder.multiImageNumberLayout.setVisibility(View.VISIBLE);
                }
                else{
                    holder.multiImageNumberLayout.setVisibility(View.INVISIBLE);
                }
                if(post.getNsfw()){
                    holder.nsfwIcon.setVisibility(View.VISIBLE);
                    glide.load(post.getMainMedia().getUrl())
                            .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                            .into(holder.image);
                }
                else{
                    holder.nsfwIcon.setVisibility(View.INVISIBLE);
                    glide.load(post.getMainMedia().getUrl())
                            .thumbnail(glide.load(post.getThumbMedia().getUrl()))
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
                            .into(holder.image);
                }


            }
            else if(type==POST_TYPE_VIDEO||type==POST_TYPE_VIDEO_START||type==POST_TYPE_VIDEO_END){
                glide.load(post.getThumbMedia().getUrl())
                        .thumbnail(glide.load(post.getThumbMedia2().getUrl()))
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
                        .into(holder.image);
                holder.nsfwIcon.setVisibility(View.INVISIBLE);
                holder.videoIcon.setVisibility(View.VISIBLE);
                holder.multiImageNumberLayout.setVisibility(View.INVISIBLE);
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

        ImageView image,nsfwIcon,videoIcon;
        RelativeLayout ripple;
        RoundKornerRelativeLayout multiImageNumberLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            multiImageNumberLayout = itemView.findViewById(R.id.multiImageNumberLay);
            image = itemView.findViewById(R.id.image);
            ripple = itemView.findViewById(R.id.grid_ripple);
            nsfwIcon = itemView.findViewById(R.id.postImageNsfwIcon);
            videoIcon = itemView.findViewById(R.id.videoIcon);



            if(ripple!=null){
                ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerViewClick.onClick(getAdapterPosition());
                    }
                });
            }







        }

    }














}
