package com.sonata.socialapp.utils.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
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
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.GuestProfileActivity;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;

import java.util.HashMap;
import java.util.List;


public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.ViewHolder> {






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
    public FollowRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(list.get(i).getType().equals("user")){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.follow_request_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        if(list.get(i).getType().equals("top")){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_top_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        if(list.get(i).getType().equals("load")){
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
    public void onBindViewHolder(@NonNull FollowRequestAdapter.ViewHolder holder, int position) {
        if(!list.get(holder.getAdapterPosition()).getType().equals("top")&&!list.get(holder.getAdapterPosition()).getType().equals("load")&&!list.get(holder.getAdapterPosition()).getType().equals("boş")){

            SonataUser user = list.get(holder.getAdapterPosition()).getUser();
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

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(GenelUtil.clickable(700)){
                        if(!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                            holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), GuestProfileActivity.class).putExtra("user",user));

                        }
                    }

                }
            };

            holder.profilephoto.setOnClickListener(onClickListener);
            holder.name.setOnClickListener(onClickListener);
            holder.username.setOnClickListener(onClickListener);

            if(user.getToMeFollowRequest()){
                holder.buttonText.setText(holder.itemView.getContext().getString(R.string.accept));
                holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue));
                holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background));
            }
            else{
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



            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - holder.a < 1000){
                        return;
                    }
                    holder.a = SystemClock.elapsedRealtime();
                    if(holder.buttonText.getText().toString().equals(holder.buttonText.getContext().getString(R.string.accept))){
                        //isteği geri çek
                        holder.rejectLayout.setVisibility(View.GONE);

                        holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.loading));

                        HashMap<String,String> params = new HashMap<>();
                        params.put("id",user.getObjectId());
                        ParseCloud.callFunctionInBackground("acceptFollowRequest", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(e==null){



                                }
                                else{

                                    holder.rejectLayout.setVisibility(View.VISIBLE);
                                    holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.accept));
                                    GenelUtil.ToastLong(holder.buttonText.getContext(),holder.buttonText.getContext().getString(R.string.error));
                                }
                            }
                        });

                    }
                    if(holder.buttonText.getText().toString().equals(holder.buttonText.getContext().getString(R.string.follow))){
                        if(user.getPrivate()){
                            //Takip İsteği gönder
                            holder.buttonText.setText(holder.itemView.getContext().getString(R.string.loading));
                            HashMap<String,String> params = new HashMap<>();
                            params.put("userID",user.getObjectId());
                            ParseCloud.callFunctionInBackground("sendFollowRequest", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object object, ParseException e) {

                                    if(e==null){
                                        holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background_dolu));
                                        user.setFollowRequest(true);
                                        holder.buttonText.setText(holder.itemView.getContext().getString(R.string.requestsent));
                                        holder.buttonText.setTextColor(Color.WHITE);
                                    }
                                    else{
                                        holder.buttonText.setText(holder.itemView.getContext().getString(R.string.follow));
                                        GenelUtil.ToastLong(holder.itemView.getContext(),holder.itemView.getContext().getString(R.string.error));
                                    }


                                }
                            });
                        }
                        else{
                            //takip et
                            holder.buttonText.setText(holder.itemView.getContext().getString(R.string.loading));
                            HashMap<String,String> params = new HashMap<>();
                            params.put("userID",user.getObjectId());
                            ParseCloud.callFunctionInBackground("follow", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object object, ParseException e) {
                                    if(e==null){
                                        user.setFollow(true);
                                        holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));

                                        holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background_dolu));
                                        holder.buttonText.setText(holder.itemView.getContext().getString(R.string.unfollow));
                                    }
                                    else{
                                        holder.buttonText.setText(holder.itemView.getContext().getString(R.string.follow));
                                        GenelUtil.ToastLong(holder.itemView.getContext(),holder.itemView.getContext().getString(R.string.error));
                                    }


                                }
                            });

                        }
                    }
                    if(holder.buttonText.getText().toString().equals(holder.buttonText.getContext().getString(R.string.unfollow))){
                        if(user.getPrivate()){
                            //takipten çık ve profili gizle
                            holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.loading));
                            HashMap<String,String> params = new HashMap<>();
                            params.put("userID",user.getObjectId());
                            ParseCloud.callFunctionInBackground("unfollow", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object object, ParseException e) {
                                    if(e==null){
                                        user.setFollow(false);
                                        holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.follow));
                                        holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue));
                                        holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background));


                                    }
                                    else{
                                        holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.unfollow));
                                        GenelUtil.ToastLong(holder.itemView.getContext(),holder.itemView.getContext().getString(R.string.error));
                                    }


                                }
                            });
                        }
                        else{
                            //takipten çık
                            holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.loading));
                            HashMap<String,String> params = new HashMap<>();
                            params.put("userID",user.getObjectId());
                            ParseCloud.callFunctionInBackground("unfollow", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object object, ParseException e) {
                                    if(e==null){
                                        user.setFollow(false);
                                        holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.follow));
                                        holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue));
                                        holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background));

                                    }
                                    else{
                                        holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.unfollow));
                                        GenelUtil.ToastLong(holder.itemView.getContext(),holder.itemView.getContext().getString(R.string.error));
                                    }


                                }
                            });
                        }
                    }
                    if(holder.buttonText.getText().toString().equals(holder.button.getContext().getString(R.string.unblock))){
                        //isteği geri çek

                        holder.buttonText.setText(holder.button.getContext().getString(R.string.loading));

                        HashMap<String,String> params = new HashMap<>();
                        params.put("userID",user.getObjectId());
                        ParseCloud.callFunctionInBackground("unblock", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(e==null){

                                    user.setBlock(false);
                                    holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.follow));
                                    holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue));
                                    holder.buttonLay.setBackground(holder.buttonLay.getContext().getResources().getDrawable(R.drawable.button_background));

                                }
                                else{
                                    holder.buttonText.setText(holder.button.getContext().getString(R.string.accept));
                                    GenelUtil.ToastLong(holder.button.getContext(),holder.button.getContext().getString(R.string.error));
                                }
                            }
                        });

                    }
                    if(holder.buttonText.getText().toString().equals(holder.buttonText.getContext().getString(R.string.requestsent))){
                        //isteği geri çek
                        holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("userID",user.getObjectId());
                        ParseCloud.callFunctionInBackground("removeFollowRequest", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(e==null){
                                    user.setFollowRequest(false);
                                    holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.follow));
                                    holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue));
                                    holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background));
                                }
                                else{
                                    holder.buttonText.setText(holder.buttonText.getContext().getString(R.string.requestsent));
                                    GenelUtil.ToastLong(holder.itemView.getContext(),holder.itemView.getContext().getString(R.string.error));
                                }


                            }
                        });

                    }
                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.buttonText.setText(holder.itemView.getContext().getString(R.string.loading));
                    holder.reject.setVisibility(View.INVISIBLE);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    HashMap<String,String> params = new HashMap<>();
                    params.put("id",user.getObjectId());
                    ParseCloud.callFunctionInBackground("rejectFollowRequest", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                user.setToMeFollowRequest(false);
                                holder.buttonLay.setVisibility(View.INVISIBLE);
                                holder.rejectLayout.setVisibility(View.INVISIBLE);
                                //holder.setVisibility(false);

                            }
                            else{
                                GenelUtil.ToastLong(holder.reject.getContext(),holder.reject.getContext().getString(R.string.error));
                                holder.reject.setVisibility(View.VISIBLE);
                                holder.buttonText.setText(holder.itemView.getContext().getString(R.string.accept));
                                holder.buttonText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue));
                                holder.buttonLay.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.button_background));
                                holder.progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            });

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
        long a = 0;


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

        }
    }










}
