package com.sonata.socialapp.utils.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sonata.socialapp.R;
import com.sonata.socialapp.socialview.MentionArrayAdapter;
import com.sonata.socialapp.socialview.Mentionable;

import java.io.File;

public class MentionAdapter extends MentionArrayAdapter {
    public MentionAdapter(@NonNull Context context) {
        super(context);
    }

    public MentionAdapter(@NonNull Context context, int defaultAvatar) {
        super(context, defaultAvatar);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mention_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Mentionable item = (Mentionable) getItem(position);
        if (item != null) {
            holder.usernameView.setText("@"+item.getUsername());

            final CharSequence displayname = item.getDisplayname();
            holder.displaynameView.setText(displayname);
            holder.displaynameView.setVisibility(View.VISIBLE);



            final Object avatar = item.getAvatar();
            final RequestManager request;
            if (avatar instanceof String) {
                if(((String) avatar).equals("empty")){
                    Glide.with(holder.avatarView).load(R.drawable.emptypp).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.loadingView.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.loadingView.setVisibility(View.GONE);
                            return false;
                        }
                    }).apply(RequestOptions.circleCropTransform()).into(holder.avatarView);
                }
                else if(((String) avatar).equals("lemazkevhnuaecbswwhbuljgbkorbx")){

                }
                else{
                    Glide.with(holder.avatarView).load(((String) avatar)).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.loadingView.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.loadingView.setVisibility(View.GONE);
                            return false;
                        }
                    }).apply(RequestOptions.circleCropTransform()).into(holder.avatarView);
                }
            }

        }
        return convertView;
    }

    private static class ViewHolder {
        private final ImageView avatarView;
        private final ProgressBar loadingView;
        private final TextView usernameView;
        private final TextView displaynameView;

        ViewHolder(View itemView) {
            avatarView = itemView.findViewById(R.id.socialview_mention_avatar);
            loadingView = itemView.findViewById(R.id.socialview_mention_loading);
            usernameView = itemView.findViewById(R.id.socialview_mention_username);
            displaynameView = itemView.findViewById(R.id.socialview_mention_displayname);
        }
    }
}
