package com.sonata.socialapp.utils.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.classes.Notif;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.NotifRecyclerView;

import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {






    private List<Notif> list;
    private RequestManager glide;
    private NotifRecyclerView click;

    public void setContext(List<Notif> list
            , RequestManager glide, NotifRecyclerView click){
        this.list=list;
        this.glide=glide;
        this.click=click;

    }

    private static final int TYPE_NOTIF = 2;
    private static final int TYPE_EMPTY = 5;
    private static final int TYPE_LOAD = 6;

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int i) {
        switch (Objects.requireNonNull(list.get(i).getType())) {
            case "followedyou":
                return TYPE_NOTIF;
            case "likedpost":
                return TYPE_NOTIF;
            case "mentionpost":
                return TYPE_NOTIF;
            case "commentpost":
                return TYPE_NOTIF;
            case "replycomment":
                return TYPE_NOTIF;
            case "mentioncomment":
                return TYPE_NOTIF;
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
            glide.clear(holder.profilephoto);
            holder.profilephoto.setImageDrawable(null);
            holder.profilephoto.setImageBitmap(null);
        }
        if (holder.notifImage != null) {
            glide.clear(holder.notifImage);
            holder.notifImage.setImageDrawable(null);
            holder.notifImage.setImageBitmap(null);
        }
    }



    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==TYPE_NOTIF){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notif_layout_like,viewGroup,false);
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
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        if(getItemViewType(holder.getAdapterPosition())!=TYPE_LOAD&&getItemViewType(holder.getAdapterPosition())!=TYPE_EMPTY){

            SonataUser user = list.get(holder.getAdapterPosition()).getOwner();
            Notif notif = list.get(holder.getAdapterPosition());
            holder.notifdesc.setVisibility(View.GONE);
            holder.imageLayout.setVisibility(View.GONE);
            holder.nsfwicon.setVisibility(View.INVISIBLE);
            if(user.getHasPp()){
                glide.load(user.getPPAdapter())
                        .placeholder(new ColorDrawable(ContextCompat.getColor(holder.profilephoto.getContext(), R.color.placeholder_gray)))
                        .into(holder.profilephoto);
            }
            else{
                glide.load(holder.profilephoto.getContext().getResources().getDrawable(R.drawable.emptypp,null)).into(holder.profilephoto);
            }

            if(notif.getType().equals("followedyou")){
                holder.notifname.setText(user.getName()
                        +" (@"+user.getUsername()+") "
                        +holder.itemView.getContext().getString(R.string.followedyou));
            }
            else if(notif.getType().equals("likedpost")){
                if(notif.getCount()<=0){
                    holder.notifname.setText(user.getName()
                            +" (@"+user.getUsername()+") "
                            +holder.itemView.getContext().getString(R.string.likedyourpost));
                }
                else{
                    String text = String.format(holder.itemView.getContext().getString(R.string.newlikenotiftext), user.getName() +" (@"+user.getUsername()+") ",notif.getCount()+"");
                    holder.notifname.setText(text);
                }

            }
            else if(notif.getType().equals("mentionpost")){
                holder.notifname.setText(user.getName()
                        +" (@"+user.getUsername()+") "
                        +holder.itemView.getContext().getString(R.string.mentionpost));
            }
            else if(notif.getType().equals("commentpost")){
                holder.notifname.setText(user.getName()
                        +" (@"+user.getUsername()+") "
                        +holder.itemView.getContext().getString(R.string.commentyourpost));
            }
            else if(notif.getType().equals("replycomment")){
                holder.notifname.setText(user.getName()
                        +" (@"+user.getUsername()+") "
                        +holder.itemView.getContext().getString(R.string.replycomment));
            }
            else if(notif.getType().equals("mentioncomment")){
                holder.notifname.setText(user.getName()
                        +" (@"+user.getUsername()+") "
                        +holder.itemView.getContext().getString(R.string.mentioncomment));
            }

            if(notif.getDesc()!=null){
                if(notif.getDesc().length()>0){
                    holder.notifdesc.setVisibility(View.VISIBLE);
                    holder.notifdesc.setText(notif.getDesc());
                }

            }

            if(notif.getMedia()!=null){
                holder.imageLayout.setVisibility(View.VISIBLE);
                if(notif.getNsfw()){
                    holder.nsfwicon.setVisibility(View.VISIBLE);
                    glide.load(notif.getMedia().getUrl())
                            .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                            .placeholder(new ColorDrawable(ContextCompat.getColor(holder.profilephoto.getContext(), R.color.placeholder_gray)))
                            .into(holder.notifImage);
                }
                else{
                    glide.load(notif.getMedia().getUrl())
                            .placeholder(new ColorDrawable(ContextCompat.getColor(holder.profilephoto.getContext(), R.color.placeholder_gray)))
                            .into(holder.notifImage);
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

        ImageView profilephoto,notifImage,nsfwicon;
        TextView notifname,notifdesc;
        RelativeLayout ripple;
        FrameLayout imageLayout;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nsfwicon = itemView.findViewById(R.id.postImageNsfwIcon);
            imageLayout = itemView.findViewById(R.id.imagelayout);
            notifImage = itemView.findViewById(R.id.notifImage);
            notifname = itemView.findViewById(R.id.followreqname);
            notifdesc = itemView.findViewById(R.id.followreqdesc);
            profilephoto = itemView.findViewById(R.id.followreqlayoutpp);
            ripple = itemView.findViewById(R.id.rippleClick);

            if(profilephoto!=null){
                profilephoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.goToProfile(getAdapterPosition());
                    }
                });
            }
            if(notifname!=null){
                notifname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.notifClick(getAdapterPosition());
                    }
                });
            }
            if(notifdesc!=null){
                notifdesc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.notifClick(getAdapterPosition());
                    }
                });
            }
            if(ripple!=null){
                ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.notifClick(getAdapterPosition());
                    }
                });
            }

        }
    }










}
