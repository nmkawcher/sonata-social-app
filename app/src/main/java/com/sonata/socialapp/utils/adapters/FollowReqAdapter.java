package com.sonata.socialapp.utils.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.FollowRequestClick;

import java.util.List;
import java.util.Objects;


public class FollowReqAdapter extends RecyclerView.Adapter<FollowReqAdapter.ViewHolder> {






    private List<ListObject> list;
    private RequestManager glide;
    private FollowRequestClick click;

    public void setContext(List<ListObject> list
            , RequestManager glide, FollowRequestClick click){
        this.list=list;
        this.glide=glide;
        this.click=click;

    }

    private static final int TYPE_USER = 2;
    private static final int TYPE_EMPTY = 5;
    private static final int TYPE_LOAD = 6;

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int i) {
        switch (Objects.requireNonNull(list.get(i).getType())) {
            case "user":
                return TYPE_USER;
            case "boş":
                return TYPE_EMPTY;
            default:
                return TYPE_LOAD;
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

    }



    @NonNull
    @Override
    public FollowReqAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==TYPE_USER){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.follow_request_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        if(i==TYPE_EMPTY){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view_real,viewGroup,false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_load,viewGroup,false);
            return new ViewHolder(view);

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FollowReqAdapter.ViewHolder holder, int position) {
        if(getItemViewType(holder.getAdapterPosition())!=TYPE_LOAD&&getItemViewType(holder.getAdapterPosition())!=TYPE_EMPTY){

            SonataUser user = list.get(holder.getAdapterPosition()).getUser();
            if(user.getObjectId().equals(GenelUtil.getCurrentUser().getObjectId())){
                holder.buttonLay.setVisibility(View.INVISIBLE);
            }
            else{
                holder.buttonLay.setVisibility(View.VISIBLE);
            }
            if(user.getToMeFollowRequest()){
                holder.reject.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.rejectLayout.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.buttonLay.getLayoutParams();
                params.setMarginEnd((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        55,
                        holder.buttonLay.getContext().getResources().getDisplayMetrics()));
                holder.buttonLay.setLayoutParams(params);
                holder.buttonText.setText(holder.itemView.getContext().getString(R.string.accept));
                holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue));
                holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background));
            }
            else{
                holder.rejectLayout.setVisibility(View.INVISIBLE);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.buttonLay.getLayoutParams();
                params.setMarginEnd((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        20,
                        holder.buttonLay.getContext().getResources().getDisplayMetrics()));
                holder.buttonLay.setLayoutParams(params);
                if(user.getBlock()){
                    holder.buttonText.setText(holder.itemView.getContext().getString(R.string.unblock));
                    holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
                    holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background_engel));
                }
                else{
                    if(user.getFollow()){
                        holder.buttonText.setText(holder.itemView.getContext().getString(R.string.unfollow));
                        holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
                        holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background_dolu));
                    }
                    else{
                        if(user.getFollowRequest()){
                            holder.buttonText.setText(holder.itemView.getContext().getString(R.string.requestsent));
                            holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
                            holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background_dolu));
                        }
                        else{
                            holder.buttonText.setText(holder.itemView.getContext().getString(R.string.follow));
                            holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue));
                            holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background));
                        }

                    }
                }
            }

            if(user.getHasPp()){
                glide.load(user.getPPAdapter())
                        .placeholder(new ColorDrawable(ContextCompat.getColor(holder.profilephoto.getContext(), R.color.placeholder_gray)))
                        .into(holder.profilephoto);
            }
            else{
                glide.load(holder.profilephoto.getContext().getResources().getDrawable(R.drawable.emptypp,null)).into(holder.profilephoto);
            }

            holder.name.setText(user.getName());
            holder.username.setText("@"+user.getUsername());




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

        ImageView profilephoto;
        TextView name,username;

        RelativeLayout button,reject;
        TextView buttonText;
        RoundKornerRelativeLayout buttonLay;
        ProgressBar progressBar;
        RoundKornerRelativeLayout rejectLayout;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rejectLayout = itemView.findViewById(R.id.followreqrejectlayout);
            progressBar = itemView.findViewById(R.id.followreqrejectprogress);
            profilephoto = itemView.findViewById(R.id.followreqlayoutpp);
            name = itemView.findViewById(R.id.followreqname);
            username = itemView.findViewById(R.id.followrequsername);
            reject = itemView.findViewById(R.id.followreqrejetcripple);
            button = itemView.findViewById(R.id.blockButton);
            buttonText = itemView.findViewById(R.id.followButtonText);
            buttonLay = itemView.findViewById(R.id.folreqacceptlayout);

            if(profilephoto!=null){
                profilephoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.goToProfileClick(getAdapterPosition());
                    }
                });
            }
            if(name!=null){
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.goToProfileClick(getAdapterPosition());
                    }
                });
            }
            if(username!=null){
                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.goToProfileClick(getAdapterPosition());
                    }
                });
            }

            if(button!=null&&buttonText!=null&&buttonLay!=null&&rejectLayout!=null){
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.buttonClick(getAdapterPosition(),buttonText,buttonLay,rejectLayout);
                    }
                });
            }

            if(reject!=null&&buttonText!=null&&buttonLay!=null&&rejectLayout!=null&&progressBar!=null){
                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.rejectClick(getAdapterPosition(),buttonText,buttonLay,rejectLayout,progressBar,reject);
                    }
                });
            }

        }
    }










}
