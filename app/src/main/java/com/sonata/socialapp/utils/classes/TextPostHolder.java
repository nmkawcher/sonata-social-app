package com.sonata.socialapp.utils.classes;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.sonata.socialapp.R;
import com.tylersuehr.socialtextview.SocialTextView;

public class TextPostHolder extends RecyclerView.ViewHolder {

    ImageView profilephoto,likeimage;
    TextView name,username,likenumber,commentnumber,postdate;
    RelativeLayout options,like,comment;
    SocialTextView postdesc;
    RoundKornerRelativeLayout pplayout;

    public TextPostHolder(@NonNull View itemView) {
        super(itemView);

        pplayout = itemView.findViewById(R.id.pplayout);
        profilephoto = itemView.findViewById(R.id.postprofilephoto);
        name = itemView.findViewById(R.id.postnametext);
        username = itemView.findViewById(R.id.postusernametext);
        options = itemView.findViewById(R.id.postopripple);

        postdesc = itemView.findViewById(R.id.postdesc);
        like = itemView.findViewById(R.id.postlikeripple);
        comment = itemView.findViewById(R.id.postcommentripple);

        likenumber = itemView.findViewById(R.id.postlikenumber);
        commentnumber = itemView.findViewById(R.id.postcommentnumber);
        postdate = itemView.findViewById(R.id.postdate);
        likeimage = itemView.findViewById(R.id.postlikeicon);

    }


}
