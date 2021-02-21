package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.adapters.AllMessagesAdapter;
import com.sonata.socialapp.utils.adapters.SearchAccountAdapter;
import com.sonata.socialapp.utils.classes.Chat;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.BlockedAdapterClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tgio.rncryptor.RNCryptorNative;

public class MessagesActivity extends AppCompatActivity implements BlockedAdapterClick {

    RelativeLayout back,newMessage;

    List<ListObject> list;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    AllMessagesAdapter adapter;
    RNCryptorNative rncryptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        newMessage = findViewById(R.id.newMessageButton);
        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessagesActivity.this, NewMessageActivity.class));
            }
        });

        recyclerView = findViewById(R.id.folreqrecyclerview);
        list=new ArrayList<>();

        linearLayoutManager=new LinearLayoutManager(MessagesActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new AllMessagesAdapter();
        adapter.setContext(list, Glide.with(MessagesActivity.this),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        ListObject object2 = new ListObject();
        object2.setType("load");
        list.add(object2);
        adapter.notifyDataSetChanged();
        rncryptor = new RNCryptorNative();
        getMessages();

    }

    private void getMessages(){
        ParseCloud.callFunctionInBackground("getChats", new HashMap<>(), new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap object, ParseException e) {
                if(e==null){
                    List<HashMap> tempList = (List<HashMap>) object.get("result");
                    initList(tempList);
                }
                else{
                    if(e.getCode() == ParseException.CONNECTION_FAILED){
                        getMessages();
                    }
                }
            }
        });
    }

    private void initList(List<HashMap> tempList){
        if(list.size()>0){
            if(list.get(list.size()-1).getType().equals("load")){
                list.remove(list.size()-1);
            }
        }
        for(int i = 0; i < tempList.size(); i++){
            ListObject post = new ListObject();
            post.setType("user");

            SonataUser p2 = (SonataUser) tempList.get(i).get("user");
            post.setUser(p2);

            Chat chat = (Chat) tempList.get(i).get("chat");
            //chat.setMessage();
            post.setChat(chat);
            post.setDecryptedMessage(rncryptor.decrypt(chat.getEncryptedMessage(), chat.getKey()));

            list.add(post);
        }
        if(list.size() == 0){
            ListObject post = new ListObject();
            post.setType("boş");
            list.add(post);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.whereAmI = "allMessages";
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApp.whereAmI = "";
    }

    @Override
    public void onBackPressed() {
        if(getIntent()!=null) {
            if(getIntent().getBooleanExtra("notif",false)){
                if(this.isTaskRoot()){
                    startActivity(new Intent(this,MainActivity.class));
                    finish();
                }
                else{
                    super.onBackPressed();
                }
            }
            else{
                super.onBackPressed();
            }
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public void goToProfileClick(int position) {
        SonataUser user = list.get(position).getUser();
        Chat chat = list.get(position).getChat();
        startActivity(new Intent(this,DirectMessageActivity.class)
                .putExtra("user",user)
                .putExtra("chat",chat));
    }

    @Override
    public void buttonClick(int position, TextView buttonText, RoundKornerRelativeLayout buttonLay) {

    }
}
