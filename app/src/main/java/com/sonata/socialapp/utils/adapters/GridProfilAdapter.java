package com.sonata.socialapp.utils.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.parse.ParseFile;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class GridProfilAdapter extends RecyclerView.Adapter<GridProfilAdapter.ViewHolder> {




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


    private static final int POST_TYPE_IMAGE = 2;
    private static final int POST_TYPE_IMAGE_START = 87;
    private static final int POST_TYPE_IMAGE_END = 97;
    private static final int POST_TYPE_VIDEO = 22;
    private static final int POST_TYPE_VIDEO_START = 12;
    private static final int POST_TYPE_VIDEO_END = 312;
    private static final int POST_TYPE_EMPTY = 5;
    private static final int POST_TYPE_LOAD = 6;

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
    public GridProfilAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

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
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view_real,viewGroup,false);
            return new ViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull GridProfilAdapter.ViewHolder holder, int positi) {
        if(getItemViewType(holder.getAdapterPosition())!=POST_TYPE_LOAD&&getItemViewType(holder.getAdapterPosition())!=POST_TYPE_EMPTY){
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

                holder.nsfwIcon.setVisibility(View.INVISIBLE);
                try {
                    List<HashMap> mediaList = post.getMediaList();
                    HashMap media = mediaList.get(0);
                    ParseFile mainMedia = (ParseFile) media.get("media");
                    ParseFile thumbnail = (ParseFile) media.get("thumbnail");

                    String url = mainMedia.getUrl();
                    String thumburl = thumbnail.getUrl();

                    glide.load(url)
                            .thumbnail(glide.load(thumburl))
                            .into(holder.image);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            else if(type==POST_TYPE_VIDEO||type==POST_TYPE_VIDEO_START||type==POST_TYPE_VIDEO_END){
                try {
                    List<HashMap> mediaList = post.getMediaList();
                    HashMap media = mediaList.get(0);
                    ParseFile thumbnail = (ParseFile) media.get("thumbnail");
                    ParseFile thumbnail2 = (ParseFile) media.get("thumbnail2");


                    String thumburl = thumbnail.getUrl();
                    String thumburl2 = thumbnail2.getUrl();

                    glide.load(thumburl)
                            .thumbnail(glide.load(thumburl2))
                            .into(holder.image);

                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                        recyclerViewClick.onOpenComments(getAdapterPosition());
                    }
                });
            }







        }

    }














}
