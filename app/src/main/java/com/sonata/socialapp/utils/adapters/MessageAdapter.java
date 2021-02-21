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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.GuestProfileActivity;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.Message;
import com.sonata.socialapp.utils.classes.Notif;
import com.sonata.socialapp.utils.classes.SonataUser;

import java.util.HashMap;
import java.util.List;

import tgio.rncryptor.RNCryptorNative;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {






    private List<Message> list;
    private RequestManager glide;
    private SonataUser user;
    private String owner;
    private String key;
    RNCryptorNative rncryptor = new RNCryptorNative();
    public void setContext(List<Message> list
            ,RequestManager glide
            ,SonataUser user
            ,String owner
            ,String key){
        this.list=list;
        this.glide=glide;
        this.user=user;
        this.owner=owner;
        this.key=key;
    }

    public void setkey(String key){
        this.key = key;
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
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(list.get(i).getOwner().equals(owner)){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direct_message_owner_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direct_message_other_layout,viewGroup,false);
            return new ViewHolder(view);
        }
    }

    private void saveMessage(Message message, TextView progresstext,RelativeLayout mainlayout,int position){
        progresstext.setTextColor(Color.parseColor("#999999"));
        progresstext.setVisibility(View.VISIBLE);
        progresstext.setText(progresstext.getContext().getString(R.string.sending));
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    message.setMessage(rncryptor.decrypt(message.getEncryptedMessage(), key));
                    notifyItemChanged(list.indexOf(message));
                }
                else{
                    setRetryClick(message,progresstext,mainlayout,position);
                }
            }
        });
    }
    private void setRetryClick(Message message, TextView progresstext,RelativeLayout mainlayout,int position){
        progresstext.setTextColor(Color.parseColor("#ff0000"));
        progresstext.setVisibility(View.VISIBLE);
        progresstext.setText(progresstext.getContext().getString(R.string.messagesenderror));
        mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getObjectId() == null){
                    saveMessage(message,progresstext,mainlayout,position);
                }
            }
        });
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = list.get(holder.getAdapterPosition());
        holder.text.setText(message.getMessage());

        if(message.getOwner().equals(owner)){
            //Ben gönderdim
            if(message.getObjectId() == null){
                saveMessage(message,holder.progresstext,holder.mainLayout,holder.getAdapterPosition());
            }
            else{
                holder.progresstext.setVisibility(View.GONE);
                holder.mainLayout.setOnClickListener(null);
            }

        }
        else{
            //bana gönderildi
            glide.load(user.getPPAdapter()).into(holder.profilephoto);
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
        TextView text,progresstext;
        RelativeLayout mainLayout;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.anan);
            progresstext = itemView.findViewById(R.id.progressText);
            profilephoto = itemView.findViewById(R.id.mesaggeProfilePhoto);
            text = itemView.findViewById(R.id.messageText);

        }
    }









}
