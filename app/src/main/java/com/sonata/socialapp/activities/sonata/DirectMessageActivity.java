package com.sonata.socialapp.activities.sonata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.OkHttp3SocketClientFactory;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.adapters.MessageAdapter;
import com.sonata.socialapp.utils.classes.Chat;
import com.sonata.socialapp.utils.classes.Message;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.theartofdev.edmodo.cropper.CropImage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;

public class DirectMessageActivity extends AppCompatActivity implements MessageAdapter.MessageClick {

    TextView topUsername;
    SonataUser user;
    Chat chat;
    RelativeLayout back, bottomCommentLayout;
    EditText editText;
    ImageButton send;
    RecyclerView recyclerView;
    MessageAdapter adapter;
    List<Message> list;
    LinearLayoutManager linearLayoutManager;
    ParseQuery<Message> parseQuery;
    ParseLiveQueryClient parseLiveQueryClient;
    ProgressBar progressBar;
    String to = "";

    Date date;
    boolean hasmore,loading;

    RecyclerView.OnScrollListener onScrollListener;


    int imagerequestcode = 1111;
    ProgressDialog alertDialog;
    RoundKornerRelativeLayout imageCancel,addImageLayout;
    ImageView commentimage;
    RelativeLayout imagelayout;
    RelativeLayout addImageRipple;
    private long lastClick=0;
    Uri uri;
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
        to = user.getObjectId();
        if(user == null) return;
        topUsername.setText("@"+user.getUsername());
        topUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileClick(0);
            }
        });
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
        //chat = getIntent() != null ? getIntent().getParcelableExtra("chat") : null;
        bottomCommentLayout.setVisibility(View.INVISIBLE);

        alertDialog = new ProgressDialog(this);
        alertDialog.setCancelable(false);

        addImageLayout=findViewById(R.id.addImagebuttonlayout);
        imageCancel = findViewById(R.id.commentimagecancel);

        commentimage = findViewById(R.id.commentimageview);
        imagelayout = findViewById(R.id.commentimagelayout);
        addImageRipple=findViewById(R.id.commentaddimageripple);
        imageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClick < 1000){
                    return;
                }
                lastClick = SystemClock.elapsedRealtime();

                uri=null;
                commentimage.setImageDrawable(null);
                commentimage.setImageBitmap(null);
                Glide.with(commentimage).clear(commentimage);
                addImageLayout.setVisibility(View.VISIBLE);
                imagelayout.setVisibility(View.GONE);


            }
        });

        addImageRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClick < 1000){
                    return;
                }
                lastClick = SystemClock.elapsedRealtime();
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                Permissions.check(DirectMessageActivity.this,permissions,null, null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,getString(R.string.video)),imagerequestcode);
                    }
                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions){

                    }
                });

            }
        });
        getChatAndMessages(to);

    }


    private void setUpRecyclerView(String key){
        if(!GenelUtil.isAlive(this)) return;
        list=new ArrayList<>();
        linearLayoutManager=new LinearLayoutManager(DirectMessageActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new MessageAdapter();
        adapter.setContext(list
                , Glide.with(DirectMessageActivity.this)
                ,user
                ,ParseUser.getCurrentUser().getObjectId()
                ,this);
        adapter.setHasStableIds(false);
        recyclerView.setAdapter(adapter);
        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(list==null)return;
                if(dy<0&&linearLayoutManager.findLastVisibleItemPosition()==list.size()-1&&!loading&&hasmore){
                    if(chat==null)return;
                    loading=true;
                    getMessages(date,chat);
                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
        //adapter.notifyDataSetChanged();
    }

    private void getMessages(Date date,Chat chat){
        if(!GenelUtil.isAlive(this)) return;
        loading = true;
        HashMap<String,Object> params = new HashMap<>();
        params.put("date",date);
        params.put("chat",chat.getObjectId());
        ParseCloud.callFunctionInBackground("getMessages", params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap object, ParseException e) {
                Log.e("done","cloud code done");
                if(!GenelUtil.isAlive(DirectMessageActivity.this)) return;
                if(e==null){

                    setUpNewMessages((List<Message>) object.get("messages"),(boolean)object.get("hasmore"),(Date)object.get("date"));


                }
                else{
                    if(e.getCode()==ParseException.CONNECTION_FAILED){
                        getMessages(date,chat);
                    }
                    else{
                        Log.e("Error Message",e.getMessage());
                        GenelUtil.ToastLong(DirectMessageActivity.this,getString(R.string.error));
                        finish();
                    }
                }
            }
        });
    }

    private void getChatAndMessages(String id){
        if(!GenelUtil.isAlive(this)) return;
        loading = true;
        HashMap<String,Object> params = new HashMap<>();
        params.put("to",id);
        ParseCloud.callFunctionInBackground("getChatAndMessages", params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap object, ParseException e) {
                Log.e("done","cloud code done");
                if(!GenelUtil.isAlive(DirectMessageActivity.this)) return;
                if(e==null){
                    chat = object.get("chat") != null ? (Chat) object.get("chat") : null;
                    setUpAfterFetch((List<Message>) object.get("messages"), object.get("hasmore") != null && (boolean) object.get("hasmore"),object.get("date") != null?(Date)object.get("date"): Calendar.getInstance().getTime());


                }
                else{
                    if(e.getCode()==ParseException.CONNECTION_FAILED){
                        getChatAndMessages(id);
                    }
                    else{
                        Log.e("Error Message",e.getMessage());
                        GenelUtil.ToastLong(DirectMessageActivity.this,getString(R.string.error));
                        finish();
                    }
                }
            }
        });
    }

    private void setUpNewMessages(List<Message> tempList, boolean hasmore, Date date) {
        if(!GenelUtil.isAlive(this)) return;
        this.hasmore = hasmore;
        this.date = date;



        //Collections.reverse(tempList);
        Observable.fromCallable(() -> {
            //Collections.reverse(tempList);
            try{
                for(int i = 0; i < tempList.size(); i++){
                    Message message = tempList.get(i);
                    //Log.e("object class", message.getClassName());
                }
                return true;
            }catch (Exception e){
                Log.e("rxjava",e.toString());
                return false;
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if(!GenelUtil.isAlive(DirectMessageActivity.this)) return;
                    if(list.size()>0 && list.get(list.size()-1).getType().equals("load")){
                        list.remove(list.size()-1);
                        //adapter.notifyDataSetChanged();
                    }

                    int preSize = list.size();
                    if(hasmore){
                        Message m = new Message();
                        m.setType("load");
                        tempList.add(m);
                    }
                    list.addAll(tempList);

                    //Log.e("object class", list.get(0).getClassName());
                    adapter.notifyDataSetChanged();
                    //recyclerView.scrollToPosition(tempList.size());
                    loading=false;

                });



    }

    private void setUpAfterFetch(List<Message> tempList, boolean hasmore, Date date) {
        if(!GenelUtil.isAlive(this)) return;
        this.hasmore = hasmore;
        this.date = date;
        if(chat != null) {
            setUpRecyclerView(chat.getKey());



            //Collections.reverse(tempList);
            Observable.fromCallable(() -> {
                //Collections.reverse(tempList);
                try{
                    for(int i = 0; i < tempList.size(); i++){
                        Message message = tempList.get(i);
                    }
                    return true;
                }catch (Exception e){
                    Log.e("rxjava",e.toString());
                    return false;
                }

            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        if(!GenelUtil.isAlive(DirectMessageActivity.this)) return;
                        int preSize = list.size();
                        if(hasmore){
                            Message m = new Message();
                            m.setType("load");
                            tempList.add(m);
                        }
                        list.addAll(tempList);

                        bottomCommentLayout.setVisibility(View.VISIBLE);

                        adapter.notifyItemRangeInserted(preSize, list.size()-preSize);
                        chat.setRead(true);
                        setSendClick();
                        progressBar.setVisibility(View.INVISIBLE);
                        connectLiveServer(chat);
                        HashMap<String,Object> params = new HashMap<>();
                        params.put("chat",chat.getObjectId());
                        ParseCloud.callFunctionInBackground("setChatAsRead",params);
                        loading=false;
                    });

        }else{
            setUpRecyclerView("");
            bottomCommentLayout.setVisibility(View.VISIBLE);
            setSendClick();
            progressBar.setVisibility(View.INVISIBLE);
            loading=false;
            //connectLiveServer(chat);
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.whereAmI = "directMessage"+to;
        //if(chat != null && parseLiveQueryClient != null) parseLiveQueryClient.reconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApp.whereAmI = "";
        //if(parseLiveQueryClient != null) parseLiveQueryClient.disconnect();
    }

    private void setSendClick(){
        if(!GenelUtil.isAlive(this)) return;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().trim().length()>0){
                    if(chat != null){
                        Message message = new Message();
                        if(imagelayout.getVisibility() == View.VISIBLE){
                            message.setIsUri(true);
                            message.setUri(uri);
                        }
                        message.setChat(chat.getObjectId());
                        message.setOwner(ParseUser.getCurrentUser().getObjectId());
                        message.setMessage(editText.getText().toString().trim());
                        list.add(0,message);
                        //Log.e("chat id",chat.getObjectId());
                        adapter.notifyItemInserted(0);
                        recyclerView.scrollToPosition(0);
                        imageCancel.performClick();


                    }
                    else{
                        sendFirstMessage();
                    }
                    editText.setText("");
                }
                else{
                    if(chat != null){
                        Message message = new Message();
                        if(imagelayout.getVisibility() == View.VISIBLE){
                            message.setIsUri(true);
                            message.setUri(uri);
                            message.setChat(chat.getObjectId());
                            message.setOwner(ParseUser.getCurrentUser().getObjectId());
                            message.setMessage("_message_media_only_");
                            list.add(0,message);
                            //Log.e("chat id",chat.getObjectId());
                            adapter.notifyItemInserted(0);
                            recyclerView.scrollToPosition(0);
                            imageCancel.performClick();
                        }
                    }
                    else{
                        sendFirstMessage();
                    }
                    editText.setText("");
                }
            }
        });
    }

    private void sendFirstMessage(){
        if(!GenelUtil.isAlive(this)) return;
        ProgressDialog progressDialog = new ProgressDialog(DirectMessageActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.sendmessage));
        progressDialog.show();

        HashMap<String,Object> params = new HashMap<>();
        params.put("to",user.getObjectId());

        String text = editText.getText().toString().trim();

        params.put("text",text);

        ParseCloud.callFunctionInBackground("sendFirstMessage", params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap object, ParseException e) {
                if(!GenelUtil.isAlive(DirectMessageActivity.this)) return;
                if(e==null){

                    chat = object.get("chat") != null ? (Chat) object.get("chat") : null;
                    Message message = (Message) object.get("message");

                    list.add(0,message);
                    adapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);
                    connectLiveServer(chat);
                    progressDialog.dismiss();
                }
                else{
                    GenelUtil.ToastLong(DirectMessageActivity.this,getString(R.string.error));
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void connectLiveServer(Chat chat){

        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://ws-server.sonatasocialapp.com/ws-server/parse/"));
            parseQuery = ParseQuery.getQuery(Message.class);
            parseQuery.whereEqualTo("chat",chat.getObjectId());
            parseQuery.whereEqualTo("owner",to);

            SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
                @Override
                public void onEvent(ParseQuery<Message> query, Message object) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Log.e("LiveQuery","id "+object.getObjectId());
                            if(!object.getOwner().equals(GenelUtil.getCurrentUser().getObjectId())){
                                list.add(0,object);
                                //Log.e("chat id",chat.getObjectId());
                                adapter.notifyItemInserted(0);
                                if(list.size()>0) adapter.notifyItemChanged(1);
                                recyclerView.scrollToPosition(0);
                            }
                        }
                    });
                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

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
    protected void onDestroy() {
        super.onDestroy();
        if(parseLiveQueryClient != null && parseQuery != null) parseLiveQueryClient.unsubscribe(parseQuery);
    }


    @Override
    public void onTextClick(int position, int clickType, String text) {
        if(!isDialogShown){
            GenelUtil.handleLinkClicks(this,text,clickType);
        }
    }

    public static boolean isDialogShown = false;
    @Override
    public void onTextLongClick(int position) {
        clickCheck = System.currentTimeMillis();
        GenelUtil.messageLongClick(list.get(position),this);
    }

    long clickCheck = 0;
    @Override
    public void onProfileClick(int position) {
        if(user!=null){
            startActivity(new Intent(this,GuestProfileActivity.class).putExtra("user",user));
        }
    }

    @Override
    public void onImageClick(int position,ImageView media) {
        List<String> ulist = new ArrayList<>();
        ulist.add("0");

        List<HashMap> ulist2 = new ArrayList<>();
        HashMap json = new HashMap();
        try {
            json.put("media",list.get(position).getMedia());
            json.put("thumbnail",list.get(position).getMedia());
            json.put("width",2000);
            json.put("height",2000);
            ulist2.add(json);

            GenelUtil.showImage(ulist,ulist2
                    ,media,0,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==imagerequestcode&&data.getData()!=null){
                uri=data.getData();
                Glide.with(this).load(uri).into(commentimage);
                addImageLayout.setVisibility(View.GONE);
                imagelayout.setVisibility(View.VISIBLE);

            }


        }
    }

}
