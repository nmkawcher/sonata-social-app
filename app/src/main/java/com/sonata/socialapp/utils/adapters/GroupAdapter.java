package com.sonata.socialapp.utils.adapters;

import android.graphics.Color;
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

import com.bumptech.glide.Glide;
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
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.VideoUtils.JZMediaExo;
import com.sonata.socialapp.utils.VideoUtils.VideoPlayer;
import com.sonata.socialapp.utils.classes.Group;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.GroupAdapterClick;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;
import com.takwolf.android.aspectratio.AspectRatioLayout;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.List;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import jp.wasabeef.glide.transformations.BlurTransformation;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {




    private List<ListObject> list;
    private RequestManager glide;


    private GroupAdapterClick recyclerViewClick;
    public void setContext(List<ListObject> list
            , RequestManager glide, GroupAdapterClick recyclerViewClick){
        this.recyclerViewClick=recyclerViewClick;
        this.list=list;
        this.glide=glide;

    }

    private static final int POST_TYPE_GROUP = 1;
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
            case "group":
                return POST_TYPE_GROUP;
            case "load":
                return POST_TYPE_LOAD;
            default:
                return POST_TYPE_EMPTY;
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.groupimage != null) {
                glide.clear(holder.groupimage);
                holder.groupimage.setImageDrawable(null);
                holder.groupimage.setImageBitmap(null);

        }



    }




    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==POST_TYPE_GROUP){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_layout_recyclerview,viewGroup,false);
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
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int positiona) {
        if(getItemViewType(holder.getAdapterPosition())!=POST_TYPE_LOAD
                &&getItemViewType(holder.getAdapterPosition())!=POST_TYPE_EMPTY){

            Group group = list.get(holder.getAdapterPosition()).getGroup();

            glide.load(group.getImageUrl()).into(holder.groupimage);
            holder.name.setText(group.getName());
            holder.desc.setText(group.getDescription());
            if(!group.getPrivate()){
                holder.privateLayout.setVisibility(View.INVISIBLE);
            }
            else{
                holder.privateLayout.setVisibility(View.VISIBLE);
            }
            holder.membercount.setText(GenelUtil.ConvertNumber((int)group.getMemberCount(),holder.itemView.getContext()));

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

        ImageView groupimage;
        TextView name,desc,membercount;
        RelativeLayout ripple,privateLayout;


        private long mLastClickTime = 0;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.groupname);
            desc = itemView.findViewById(R.id.groupdesc);
            groupimage = itemView.findViewById(R.id.group_image);
            membercount = itemView.findViewById(R.id.group_member_number);
            ripple = itemView.findViewById(R.id.groupripple);
            privateLayout = itemView.findViewById(R.id.group_private_layout);

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
