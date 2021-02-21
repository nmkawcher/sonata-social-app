package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.adapters.MessageAdapter;
import com.sonata.socialapp.utils.classes.Chat;
import com.sonata.socialapp.utils.classes.Message;
import com.sonata.socialapp.utils.classes.SonataUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import tgio.rncryptor.RNCryptorNative;

public class DirectMessageActivity extends AppCompatActivity {

    TextView topUsername;
    SonataUser user;
    Chat chat;
    RelativeLayout back, bottomCommentLayout;
    EditText editText;
    RNCryptorNative rncryptor;
    ImageButton send;
    RecyclerView recyclerView;
    MessageAdapter adapter;
    List<Message> list;
    LinearLayoutManager linearLayoutManager;

    ProgressBar progressBar;

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

        send = findViewById(R.id.sendbutton);
        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.commentrecyclerview);

        bottomCommentLayout = findViewById(R.id.makecommentlayout);
        progressBar = findViewById(R.id.progressBar);
        editText = findViewById(R.id.commentedittext);
        rncryptor = new RNCryptorNative();
        chat = getIntent() != null ? getIntent().getParcelableExtra("chat") : null;
        if(chat != null) {
            progressBar.setVisibility(View.INVISIBLE);
            bottomCommentLayout.setVisibility(View.VISIBLE);
            setUpRecyclerView(chat.getKey());
            setSendClick();
        }
        else{
            bottomCommentLayout.setVisibility(View.INVISIBLE);
        }
        getChatAndMessages(user.getObjectId());


    }

    private void setUpRecyclerView(String key){
        list=new ArrayList<>();
        linearLayoutManager=new LinearLayoutManager(DirectMessageActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new MessageAdapter();
        adapter.setContext(list
                , Glide.with(DirectMessageActivity.this)
                ,user
                ,ParseUser.getCurrentUser().getObjectId()
                ,key);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    private void getChatAndMessages(String id){
        HashMap<String,Object> params = new HashMap<>();
        params.put("to",id);
        ParseCloud.callFunctionInBackground("getChatAndMessages", params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap object, ParseException e) {
                if(e==null){
                    chat = object.get("chat") != null ? (Chat) object.get("chat") : null;
                    if(chat != null) {
                        setUpRecyclerView(chat.getKey());
                        int preSize = list.size();
                        List<Message> tempList = (List<Message>) object.get("messages");
                        for(int i = 0; i < tempList.size(); i++){
                            Message message = tempList.get(i);
                            message.setMessage(rncryptor.decrypt(message.getEncryptedMessage(), chat.getKey()));
                        }
                        //Collections.reverse(tempList);
                        list.addAll(tempList);
                        bottomCommentLayout.setVisibility(View.VISIBLE);

                        adapter.notifyItemRangeInserted(preSize, list.size()-preSize);
                        setSendClick();
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{
                        setUpRecyclerView("");
                        bottomCommentLayout.setVisibility(View.VISIBLE);
                        setSendClick();
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                }
                else{
                    if(e.getCode()==ParseException.CONNECTION_FAILED){
                        getChatAndMessages(id);
                    }
                    else{
                        GenelUtil.ToastLong(DirectMessageActivity.this,getString(R.string.error));
                        finish();
                    }
                }
            }
        });
    }

    private void setSendClick(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chat != null){
                    Message message = new Message();
                    message.setChat(chat);
                    message.setOwner(ParseUser.getCurrentUser().getObjectId());
                    message.setMessage(editText.getText().toString().trim());
                    message.setEncryptedMessage(new String(rncryptor.encrypt(editText.getText().toString().trim()
                            , chat.getKey())));
                    list.add(message);
                    adapter.notifyItemInserted(list.size()-1);
                    recyclerView.scrollToPosition(list.size()-1);
                }
                else{
                    sendFirstMessage();
                }
                editText.setText("");
            }
        });
    }

    private void sendFirstMessage(){

        ProgressDialog progressDialog = new ProgressDialog(DirectMessageActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.sendmessage));
        progressDialog.show();

        String key = new String(rncryptor.encrypt(editText.getText().toString().trim()
                , user.getObjectId()+ ParseUser.getCurrentUser().getObjectId()));
        adapter.setkey(key);

        HashMap<String,Object> params = new HashMap<>();
        params.put("to",user.getObjectId());
        params.put("key",key);

        String text = new String(rncryptor.encrypt(editText.getText().toString().trim()
                , key));

        params.put("text",text);

        ParseCloud.callFunctionInBackground("sendFirstMessage", params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap object, ParseException e) {
                if(e==null){

                    chat = object.get("chat") != null ? (Chat) object.get("chat") : null;
                    Message message = (Message) object.get("message");
                    message.setMessage(rncryptor.decrypt(message.getEncryptedMessage(), chat.getKey()));
                    list.add(message);
                    adapter.notifyItemInserted(list.size()-1);
                    recyclerView.scrollToPosition(list.size()-1);
                    progressDialog.dismiss();
                }
                else{
                    GenelUtil.ToastLong(DirectMessageActivity.this,getString(R.string.error));
                    progressDialog.dismiss();
                }
            }
        });
    }
}
