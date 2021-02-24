package com.sonata.socialapp.utils.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateUtils;
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
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.Chat;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.BlockedAdapterClick;

import java.util.List;
import java.util.Objects;


public class AllMessagesAdapter extends RecyclerView.Adapter<AllMessagesAdapter.ViewHolder> {






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
    public AllMessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==TYPE_USER){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_message_layout,viewGroup,false);
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
    public void onBindViewHolder(@NonNull AllMessagesAdapter.ViewHolder holder, int position) {
        if(getItemViewType(holder.getAdapterPosition())!=TYPE_LOAD&&getItemViewType(holder.getAdapterPosition())!=TYPE_EMPTY){
            ListObject listObject = list.get(holder.getAdapterPosition());
            SonataUser user = listObject.getUser();
            Chat chat = listObject.getChat();
            if(user.getHasPp()){
                glide.load(user.getPPAdapter())
                        .placeholder(new ColorDrawable(ContextCompat.getColor(holder.profilephoto.getContext(), R.color.placeholder_gray)))
                        .into(holder.profilephoto);
            }
            else{
                glide.load(holder.profilephoto.getContext().getResources().getDrawable(R.drawable.emptypp,null)).into(holder.profilephoto);
            }

            boolean anan = chat.getLastPoster().equals(GenelUtil.getCurrentUser().getObjectId());
            if(anan){
                holder.name.setTextColor(Color.parseColor("#999999"));
                holder.message.setTextColor(Color.parseColor("#999999"));
                holder.indicator.setVisibility(View.INVISIBLE);
            }
            else{
                if(chat.isRead()){
                    holder.name.setTextColor(Color.parseColor("#999999"));
                    holder.message.setTextColor(Color.parseColor("#999999"));
                    holder.indicator.setVisibility(View.INVISIBLE);
                }
                else{
                    //yeni okuyacak
                    holder.name.setTextColor(GenelUtil.getNightMode() ? Color.parseColor("#ffffff"):Color.parseColor("#000000"));
                    holder.message.setTextColor(GenelUtil.getNightMode() ? Color.parseColor("#ffffff"):Color.parseColor("#000000"));
                    holder.indicator.setVisibility(View.VISIBLE);
                }
            }
            holder.name.setText(user.getName());
            if(anan){
                holder.message.setText(holder.itemView.getContext().getString(R.string.you)+" "+chat.getMessage());
            }
            else{
                holder.message.setText(chat.getMessage());
            }

            holder.date.setText(DateUtils.getRelativeTimeSpanString(chat.getLastEdit().getTime(), System.currentTimeMillis(), DateUtils.FORMAT_24HOUR));



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
        TextView name,message,date;
        RelativeLayout clickLay,indicator;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilephoto = itemView.findViewById(R.id.followreqlayoutpp);
            name = itemView.findViewById(R.id.followreqname);
            message = itemView.findViewById(R.id.followrequsername);
            clickLay = itemView.findViewById(R.id.clickLayout);
            indicator = itemView.findViewById(R.id.blueIndiator);
            date = itemView.findViewById(R.id.date);

            if(clickLay!=null){
                clickLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.goToProfileClick(getAdapterPosition());
                    }
                });
            }

        }
    }

}
