package com.sonata.socialapp.utils.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.GuestProfileActivity;
import com.sonata.socialapp.utils.Util;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;

import java.util.List;


public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {






    private List<ListObject> list;
    private RequestManager glide;

    public void setContext(List<ListObject> list
            ,RequestManager glide){
        this.list=list;
        this.glide=glide;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
    public SearchUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(list.get(i).getType().equals("user")){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.person_layout_grid,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(list.get(i).getType().equals("load")){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_load,viewGroup,false);
            return new ViewHolder(view);
        }

        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view_real,viewGroup,false);
            return new ViewHolder(view);

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SearchUserAdapter.ViewHolder holder, int position) {
        if(!list.get(position).getType().equals("boş")&&!list.get(position).getType().equals("load")){

            SonataUser user = list.get(position).getUser();
            if(user.getHasPp()){
                glide.load(user.getPPbig())
                        .thumbnail(
                                glide.load(user.getPPAdapter()).apply(RequestOptions.circleCropTransform()))
                        .placeholder(new ColorDrawable(ContextCompat.getColor(holder.profilephoto.getContext(), R.color.placeholder_gray)))
                        .into(holder.profilephoto);
            }
            else{
                glide.load(holder.profilephoto.getContext().getResources().getDrawable(R.drawable.emptypp,null)).into(holder.profilephoto);
            }

            holder.name.setText(user.getName());
            holder.username.setText("@"+user.getUsername());

            View.OnClickListener onClickListener = view -> {
                if(Util.clickable(700)){
                    if(!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), GuestProfileActivity.class).putExtra("user",user));

                    }
                }

            };

            /*holder.profilephoto.setOnClickListener(onClickListener);
            holder.name.setOnClickListener(onClickListener);
            holder.username.setOnClickListener(onClickListener);
            holder.mainLay.setOnClickListener(onClickListener);*/
            holder.ripple.setOnClickListener(onClickListener);



        }
        else if(list.get(position).getType().equals("load")){
            if(list.size()>holder.getAdapterPosition()+1){
                holder.setVisibility(false);
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
        TextView name,username;
        RoundKornerRelativeLayout mainLay;
        RelativeLayout ripple;




        public void setVisibility(boolean isVisible){
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            if (isVisible){
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            }else{
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ripple = itemView.findViewById(R.id.gridPersonRipple);
            mainLay = itemView.findViewById(R.id.mainLay);
            profilephoto = itemView.findViewById(R.id.followreqlayoutpp);
            name = itemView.findViewById(R.id.followreqname);
            username = itemView.findViewById(R.id.followrequsername);

        }
    }










}
