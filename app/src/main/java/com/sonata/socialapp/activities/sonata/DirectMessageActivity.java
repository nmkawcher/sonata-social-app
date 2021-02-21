package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.Chat;
import com.sonata.socialapp.utils.classes.SonataUser;

public class DirectMessageActivity extends AppCompatActivity {

    TextView topUsername;
    SonataUser user;
    Chat chat;
    RelativeLayout back,send, bottomCommentLayout;
    EditText editText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);

        topUsername = findViewById(R.id.profileusernametext);
        user = getIntent().getParcelableExtra("user");
        if(user == null) return;
        topUsername.setText("@"+user.getUsername());

        send = findViewById(R.id.sendbuttonlayout);
        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bottomCommentLayout = findViewById(R.id.makecommentlayout);
        chat = getIntent() != null ? getIntent().getParcelableExtra("chat") : null;
        if(chat != null) {
            bottomCommentLayout.setVisibility(View.VISIBLE);
        }

        editText = findViewById(R.id.commentedittext);


    }

    private void getChatAndMessages(String id){

    }
}
