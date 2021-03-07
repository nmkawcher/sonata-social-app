package com.sonata.socialapp.utils.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.classes.Message;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.List;



public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {






    private List<Message> list;
    private RequestManager glide;
    private SonataUser user;
    private String owner;
    private MessageClick messageClick;
    public void setContext(List<Message> list
            ,RequestManager glide
            ,SonataUser user
            ,String owner
            ,MessageClick messageClick){
        this.list=list;
        this.glide=glide;
        this.user=user;
        this.owner=owner;
        this.messageClick = messageClick;
    }




    @Override
    public long getItemId(int position) {
        return position;
    }

    private int VIEW_TYPE_LOAD = 112;
    private int VIEW_TYPE_OWNER = 113;
    private int VIEW_TYPE_OTHER = 114;

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getType().equals("load")){
            return VIEW_TYPE_LOAD;
        }
        else{
            if(list.get(position).getOwner().equals(owner)){
                return VIEW_TYPE_OWNER;
            }
            else{
                return VIEW_TYPE_OTHER;
            }
        }
        //return position;
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
        if(i==VIEW_TYPE_LOAD){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_load,viewGroup,false);
            return new ViewHolder(view);
        }
        else{
            if(i==VIEW_TYPE_OWNER){
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direct_message_owner_layout,viewGroup,false);
                return new ViewHolder(view);
            }
            else{
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direct_message_other_layout,viewGroup,false);
                return new ViewHolder(view);
            }
        }
    }

    private void saveMessage(Message message, TextView progresstext,RelativeLayout mainlayout,int position,SocialTextView text){
        progresstext.setTextColor(Color.parseColor("#999999"));
        progresstext.setVisibility(View.VISIBLE);
        progresstext.setText(progresstext.getContext().getString(R.string.sending));
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    progresstext.setVisibility(View.GONE);
                    mainlayout.setOnClickListener(null);
                    //notifyItemChanged(position);
                    text.setLinkText(message.getMessage());
                }
                else{
                    Log.e("MessageSaveError",e.getMessage());
                    setRetryClick(message,progresstext,mainlayout,position,text);
                }
            }
        });
    }
    private void setRetryClick(Message message, TextView progresstext,RelativeLayout mainlayout,int position,SocialTextView text){
        progresstext.setTextColor(Color.parseColor("#ff0000"));
        progresstext.setVisibility(View.VISIBLE);
        progresstext.setText(progresstext.getContext().getString(R.string.messagesenderror));
        mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getObjectId() == null){
                    saveMessage(message,progresstext,mainlayout,position,text);
                }
                else{
                    progresstext.setVisibility(View.GONE);
                }
            }
        });
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        if(getItemViewType(holder.getAdapterPosition())!=VIEW_TYPE_LOAD){
            Message message = list.get(holder.getAdapterPosition());
            holder.text.setLinkText(message.getMessage());

            if(message.getOwner().equals(owner)){
                //Ben gönderdim
                if(message.getObjectId() == null){
                    saveMessage(message,holder.progresstext,holder.mainLayout,holder.getAdapterPosition(),holder.text);
                }
                else{
                    holder.progresstext.setVisibility(View.GONE);
                    holder.mainLayout.setOnClickListener(null);
                }
            }
            else{
                //bana gönderildi
                if(holder.getAdapterPosition()>0){
                    if(list.get(holder.getAdapterPosition()-1).getOwner().equals(message.getOwner())){
                        holder.profilephoto.setVisibility(View.INVISIBLE);
                    }
                    else{
                        holder.profilephoto.setVisibility(View.VISIBLE);
                        glide.load(user.getPPAdapter()).into(holder.profilephoto);
                    }
                }
                else{
                    holder.profilephoto.setVisibility(View.VISIBLE);
                    if(user.getHasPp()){
                        glide.load(user.getPPAdapter()).into(holder.profilephoto);
                    }
                    else{
                        glide.load(R.drawable.emptypp).into(holder.profilephoto);
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
        TextView progresstext;
        RelativeLayout mainLayout;
        SocialTextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.anan);
            progresstext = itemView.findViewById(R.id.progressText);
            profilephoto = itemView.findViewById(R.id.mesaggeProfilePhoto);
            text = itemView.findViewById(R.id.messageText);


            if(text != null){
                text.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
                    @Override
                    public void onLinkClicked(int linkType, String matchedText) {
                        messageClick.onTextClick(getAdapterPosition(),linkType,matchedText);
                    }
                });
            }

        }
    }


    public static interface MessageClick {
        void onTextClick(int position,int clickType,String text);
    }








}
