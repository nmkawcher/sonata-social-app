package com.sonata.socialapp.utils.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.Util;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.BlockedAdapterClick;

import java.util.List;
import java.util.Objects;


public class BlockedPersonAdapter extends RecyclerView.Adapter<BlockedPersonAdapter.ViewHolder> {






    private List<ListObject> list;
    private RequestManager glide;
    private BlockedAdapterClick click;

    public void setContext(List<ListObject> list
            , RequestManager glide, BlockedAdapterClick click){
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
    public BlockedPersonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==TYPE_USER){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.block_account_layout,viewGroup,false);
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
    public void onBindViewHolder(@NonNull BlockedPersonAdapter.ViewHolder holder, int position) {
        if(getItemViewType(holder.getAdapterPosition())!=TYPE_LOAD&&getItemViewType(holder.getAdapterPosition())!=TYPE_EMPTY){

            SonataUser user = list.get(holder.getAdapterPosition()).getUser();
            if(user.getObjectId().equals(Util.getCurrentUser().getObjectId())){
                holder.buttonLay.setVisibility(View.INVISIBLE);
            }
            else{
                holder.buttonLay.setVisibility(View.VISIBLE);
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
        TextView name,username,buttonText;
        RelativeLayout button;
        RoundKornerRelativeLayout buttonLay;
        long a = 0;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.blockButton);
            buttonText = itemView.findViewById(R.id.followButtonText);
            buttonLay = itemView.findViewById(R.id.folreqacceptlayout);
            profilephoto = itemView.findViewById(R.id.followreqlayoutpp);
            name = itemView.findViewById(R.id.followreqname);
            username = itemView.findViewById(R.id.followrequsername);

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

            if(button!=null&&buttonText!=null&&buttonLay!=null){
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.buttonClick(getAdapterPosition(),buttonText,buttonLay);
                    }
                });
            }

        }
    }









}
