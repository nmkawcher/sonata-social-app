package com.sonata.socialapp.utils.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.Util;
import com.sonata.socialapp.utils.classes.UploadObject;
import com.sonata.socialapp.utils.interfaces.UploadPostClick;

import java.util.List;
import java.util.Objects;


public class UploadPostAdapter extends RecyclerView.Adapter<UploadPostAdapter.ViewHolder> {






    private List<UploadObject> list;
    private RequestManager glide;
    private UploadPostClick click;

    public void setContext(List<UploadObject> list
            , RequestManager glide, UploadPostClick click){
        this.list=list;
        this.glide=glide;
        this.click=click;

    }

    private static final int TYPE_POST = 2;
    private static final int TYPE_POST_VIDEO = 7;
    private static final int TYPE_ADD = 5;

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int i) {
        switch (Objects.requireNonNull(list.get(i).getType())) {
            case "image":
                return TYPE_POST;
            case "video":
                return TYPE_POST_VIDEO;
            default:
                return TYPE_ADD;
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.image != null) {
            if(holder.image.getVisibility()== View.VISIBLE){
                glide.clear(holder.image);
                holder.image.setImageDrawable(null);
                holder.image.setImageBitmap(null);
            }

        }

    }



    @NonNull
    @Override
    public UploadPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==TYPE_POST){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.upload_post_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==TYPE_POST_VIDEO){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.upload_post_video_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.upload_add_layout,viewGroup,false);
            return new ViewHolder(view);

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UploadPostAdapter.ViewHolder holder, int positiona) {
        if(getItemViewType(holder.getAdapterPosition())!=TYPE_ADD){

            CircularProgressDrawable progressDrawable = new CircularProgressDrawable(holder.itemView.getContext());
            progressDrawable.setCenterRadius(50f);
            progressDrawable.setStrokeWidth(9f);
            progressDrawable.setColorSchemeColors(holder.itemView.getResources().getColor(R.color.colorRed));
            progressDrawable.start();
            glide.load(list.get(holder.getAdapterPosition()).getUri()).placeholder(progressDrawable).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Util.ToastLong(holder.itemView.getContext(),R.string.error);
                    holder.cancel.performClick();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                    return false;
                }
            }).into(holder.image);

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

        ImageView image,videoicon;
        RelativeLayout add,cancel,edit;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edit = itemView.findViewById(R.id.upload_post_edit);
            image = itemView.findViewById(R.id.upload_post_image);
            videoicon = itemView.findViewById(R.id.videoIcon);
            add = itemView.findViewById(R.id.upload_post_add_ripple);
            cancel = itemView.findViewById(R.id.upload_post_cancel);

            if(add!=null){
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.onAdd();
                    }
                });
            }
            if(edit!=null){
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.onEdit(getAdapterPosition());
                    }
                });
            }
            if(cancel!=null){
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.onCancel(getAdapterPosition());
                    }
                });
            }


        }
    }









}
