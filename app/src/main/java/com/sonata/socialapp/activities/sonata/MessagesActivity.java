package com.sonata.socialapp.activities.sonata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.Util;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.adapters.AllMessagesAdapter;
import com.sonata.socialapp.utils.classes.Chat;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.BlockedAdapterClick;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MessagesActivity extends AppCompatActivity implements BlockedAdapterClick {

    RelativeLayout back,newMessage;

    List<ListObject> list;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    AllMessagesAdapter adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView.OnScrollListener onScrollListener;

    boolean loading,hasmore;
    Date date;
    List<String> chatIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Util.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        if(getIntent()!=null){
            if(getIntent().getStringExtra("to")!=null){
                String to = getIntent().getStringExtra("to");
                if(ParseUser.getCurrentUser().getObjectId().equals(to)){
                    setOnCreate();
                }
                else{
                    List<Object> an = Util.isUserSaved(this,to);
                    boolean isExist = (boolean) an.get(0);
                    if(isExist){
                        String session = (String) an.get(2);
                        ParseUser.becomeInBackground(session, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {

                                if(e==null){
                                    String text = String.format(getResources().getString(R.string.accsw), "@"+ParseUser.getCurrentUser().getUsername());
                                    Util.ToastLong(MessagesActivity.this,text);

                                    setOnCreate();
                                }
                                else{
                                    if(e.getCode() == ParseException.INVALID_SESSION_TOKEN){
                                        Util.removeUserFromCache(to, MessagesActivity.this);
                                    }
                                    Util.ToastLong(MessagesActivity.this,getString(R.string.invalidsessiontoken));
                                    startActivity(new Intent(MessagesActivity.this, StartActivity.class));
                                    finish();
                                }
                            }
                        });
                    }
                    else{
                        startActivity(new Intent(this, StartActivity.class));
                        finish();
                    }
                }
            }
            else{
                setOnCreate();
            }
        }
        else{
            setOnCreate();
        }


    }

    private void setOnCreate(){
        chatIdList = new ArrayList<>();
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
        loading = true;
        swipeRefreshLayout = findViewById(R.id.folreqSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!Util.isAlive(MessagesActivity.this)) return;
                refresh(true,false);
            }
        });

        onScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0&&linearLayoutManager.findLastVisibleItemPosition()>(list.size()-3)&&!loading&&hasmore){
                    loading=true;
                    getMessages(false,false);
                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
        getMessages(false,false);
    }

    private void getMessages(boolean refresh,boolean useExisting){
        HashMap<String,Object> params = new HashMap<>();
        if(date!=null){
            params.put("date",date);
        }
        if(useExisting){
            params.put("ids",chatIdList);
        }
        ParseCloud.callFunctionInBackground("getChats", params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap object, ParseException e) {
                if(e==null){
                    List<HashMap> tempList = (List<HashMap>) object.get("result");
                    boolean hasmore = object.get("hasmore") != null && (boolean) object.get("hasmore");
                    Date date = object.get("date") != null ? (Date) object.get("date") : Calendar.getInstance().getTime();
                    initList(tempList,hasmore,date,refresh,useExisting);
                }
                else{
                    if(e.getCode() == ParseException.CONNECTION_FAILED){
                        getMessages(refresh,useExisting);
                    }
                }
            }
        });
    }

    private void initList(List<HashMap> tempList,boolean hasmore, Date date,boolean refresh,boolean useExisting){
        if(useExisting){
            Observable.fromCallable(() -> {
                //Collections.reverse(tempList);
                try{

                    if(list.size()>0){
                        if(list.get(list.size()-1).getType().equals("load")){
                            list.remove(list.size()-1);
                        }
                    }
                    for(int i = 0; i < tempList.size(); i++){
                        Chat chat = (Chat) tempList.get(i).get("chat");
                        if(chatIdList.indexOf(chat.getObjectId())<0){
                            ListObject post = new ListObject();
                            post.setType("user");

                            SonataUser p2 = (SonataUser) tempList.get(i).get("user");
                            post.setUser(p2);


                            post.setChat(chat);
                            chatIdList.add(chat.getObjectId());

                            list.add(post);
                        }

                    }

                    ListObject post = new ListObject();
                    post.setType("load");
                    list.add(post);

                    return true;
                }catch (Exception e){
                    Log.e("rxjava",e.toString());
                    return false;
                }

            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        if(!Util.isAlive(MessagesActivity.this)) return;
                        if(!result) return;
                        adapter.notifyDataSetChanged();
                        loading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        loading = false;
                    });
        }
        else{
            Observable.fromCallable(() -> {
                //Collections.reverse(tempList);
                try{
                    this.hasmore = hasmore;
                    this.date = date;
                    if(refresh){
                        list.clear();
                    }
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
                        post.setChat(chat);
                        chatIdList.add(chat.getObjectId());

                        list.add(post);
                    }
                    if(hasmore){
                        ListObject post = new ListObject();
                        post.setType("load");
                        list.add(post);
                    }
                    if(list.size() == 0){
                        ListObject post = new ListObject();
                        post.setType("boş");
                        list.add(post);
                    }
                    return true;
                }catch (Exception e){
                    Log.e("rxjava",e.toString());
                    return false;
                }

            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        if(!Util.isAlive(MessagesActivity.this)) return;
                        if(!result) return;
                        adapter.notifyDataSetChanged();
                        loading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        loading = false;
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.whereAmI = "allMessages";
        /*try{
            Collections.sort(list, new ListObject.SortByDate());
            if(adapter!=null) adapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }*/

        //refresh(false,true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApp.whereAmI = "";
    }

    private void refresh(boolean refresh,boolean useExisting){
        if(!Util.isAlive(this)) return;
        if(loading) return;
        loading = true;
        if(refresh){
            date = null;
            hasmore = true;
        }
        getMessages(refresh,useExisting);

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
