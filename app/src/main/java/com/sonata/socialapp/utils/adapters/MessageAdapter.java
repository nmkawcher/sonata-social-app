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
import com.developers.imagezipper.ImageZipper;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.Util;
import com.sonata.socialapp.utils.classes.Message;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.tylersuehr.socialtextview.SocialTextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


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

    private final int VIEW_TYPE_LOAD = 112;
    private final int VIEW_TYPE_OWNER = 113;
    private final int VIEW_TYPE_OTHER = 114;

    private final int VIEW_TYPE_OWNER_MEDIA = 115;
    private final int VIEW_TYPE_OTHER_MEDIA = 116;

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getType().equals("load")){
            return VIEW_TYPE_LOAD;
        }
        else{
            if(list.get(position).getMedia() != null || list.get(position).getIsUri()){
                if(list.get(position).getOwner().equals(owner)){
                    return VIEW_TYPE_OWNER_MEDIA;
                }
                else{
                    return VIEW_TYPE_OTHER_MEDIA;
                }
            }
            else{
                if(list.get(position).getOwner().equals(owner)){
                    return VIEW_TYPE_OWNER;
                }
                else{
                    return VIEW_TYPE_OTHER;
                }
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
        else if(i==VIEW_TYPE_OWNER){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direct_message_owner_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==VIEW_TYPE_OWNER_MEDIA){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direct_message_owner_media_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else if(i==VIEW_TYPE_OTHER_MEDIA){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direct_message_other_media_layout,viewGroup,false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direct_message_other_layout,viewGroup,false);
            return new ViewHolder(view);
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
                    notifyItemChanged(list.indexOf(message));
                }
                else{
                    Log.e("MessageSaveError",e.getMessage());
                    setRetryClick(message,progresstext,mainlayout,position,text);
                }
            }
        });
    }

    private void saveMessage(Message message, TextView progresstext,RelativeLayout mainlayout,int position,SocialTextView text,ImageView media){
        progresstext.setTextColor(Color.parseColor("#999999"));
        progresstext.setVisibility(View.VISIBLE);
        progresstext.setText(progresstext.getContext().getString(R.string.sending));
        final File[] imageZipperFile = {null};
        Observable.fromCallable(() -> {
            try {
                File dst = new File(media.getContext().getCacheDir()+File.separator+"copiedimage"+(System.currentTimeMillis()%10));
                Util.copy(message.getUri(),dst,media.getContext(),null);
                imageZipperFile[0] = new ImageZipper(media.getContext())
                        .setQuality(70)
                        .compressToFile(dst);
                Log.e("filePath: ",imageZipperFile[0].getAbsolutePath());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("GetLangErr",e.getMessage());
                return false;
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if(result){
                        ParseFile file = new ParseFile(imageZipperFile[0]);
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    message.setMedia(file);
                                    message.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e==null){
                                                notifyItemChanged(list.indexOf(message));
                                            }
                                            else{
                                                Log.e("MessageSaveError",e.getMessage());
                                                //GenelUtil.ToastLong(media.getContext(),e.getMessage());
                                                setRetryClick(message,progresstext,mainlayout,position,text,media);
                                            }
                                        }
                                    });
                                }
                                else{
                                    setRetryClick(message,progresstext,mainlayout,position,text,media);
                                }
                            }
                        });
                    }
                    else{
                        setRetryClick(message,progresstext,mainlayout,position,text,media);
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
    private void setRetryClick(Message message, TextView progresstext,RelativeLayout mainlayout,int position,SocialTextView text,ImageView media){
        progresstext.setTextColor(Color.parseColor("#ff0000"));
        progresstext.setVisibility(View.VISIBLE);
        progresstext.setText(progresstext.getContext().getString(R.string.messagesenderror));
        mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getObjectId() == null){
                    saveMessage(message,progresstext,mainlayout,position,text,media);
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
            if(holder.progresstext!=null){
                holder.progresstext.setVisibility(View.GONE);
            }
            if(holder.mainLayout!=null){
                holder.mainLayout.setOnClickListener(null);
            }
            if(getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_OWNER){
                //Ben gönderdim
                if(message.getObjectId() == null){
                    holder.progresstext.setVisibility(View.VISIBLE);
                    saveMessage(message,holder.progresstext,holder.mainLayout,holder.getAdapterPosition(),holder.text);
                }
                else{

                    holder.mainLayout.setOnClickListener(null);
                }
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setLinkText(message.getMessage());
            }
            else if(getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_OWNER_MEDIA){
                //Ben gönderdim

                if(message.getObjectId() == null){
                    glide.load(message.getUri()).into(holder.media);
                    holder.progresstext.setVisibility(View.VISIBLE);
                    saveMessage(message,holder.progresstext,holder.mainLayout,holder.getAdapterPosition(),holder.text,holder.media);
                }
                else{

                    holder.mainLayout.setOnClickListener(null);
                    glide.load(message.getMedia().getUrl()).into(holder.media);
                }
                if(message.getMessage().equals("_message_media_only_")){
                    holder.text.setVisibility(View.GONE);
                }
                else{
                    holder.text.setLinkText(message.getMessage());
                    holder.text.setVisibility(View.VISIBLE);
                }
            }
            else if(getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_OTHER){
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
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setLinkText(message.getMessage());

            }
            else if(getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_OTHER_MEDIA){
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
                glide.load(message.getMedia().getUrl()).into(holder.media);
                if(message.getMessage().equals("_message_media_only_")){
                    holder.text.setVisibility(View.GONE);
                }
                else{
                    holder.text.setLinkText(message.getMessage());
                    holder.text.setVisibility(View.VISIBLE);
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

        ImageView profilephoto,media;
        TextView progresstext;
        RelativeLayout mainLayout;
        SocialTextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.anan);
            progresstext = itemView.findViewById(R.id.progressText);
            profilephoto = itemView.findViewById(R.id.mesaggeProfilePhoto);
            text = itemView.findViewById(R.id.messageText);
            media = itemView.findViewById(R.id.message_media);


            if(text != null){
                text.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
                    @Override
                    public void onLinkClicked(int linkType, String matchedText) {
                        messageClick.onTextClick(getAdapterPosition(),linkType,matchedText);
                    }
                });
                text.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        messageClick.onTextLongClick(getAdapterPosition());
                        return false;
                    }
                });
            }
            if(profilephoto != null){
                profilephoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageClick.onProfileClick(getAdapterPosition());
                    }
                });
            }

            if(media != null){
                media.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageClick.onImageClick(getAdapterPosition(),media);
                    }
                });
            }
        }
    }


    public interface MessageClick {
        void onTextClick(int position,int clickType,String text);
        void onTextLongClick(int position);
        void onProfileClick(int position);
        void onImageClick(int position,ImageView media);
    }








}
