package com.sonata.socialapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.CommentActivity;
import com.sonata.socialapp.activities.sonata.FollowRequestActivity;
import com.sonata.socialapp.activities.sonata.GuestProfileActivity;
import com.sonata.socialapp.activities.sonata.MessagesActivity;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.adapters.NotificationAdapter;
import com.sonata.socialapp.utils.classes.Notif;
import com.sonata.socialapp.utils.interfaces.NotifRecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;


public class NotifFragment extends Fragment implements NotifRecyclerView {


    List<Notif> list;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    NotificationAdapter adapter;
    private boolean loading=true;
    Date date;
    RecyclerView.OnScrollListener onScrollListener;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;


    private TextView followReqText;
    private RelativeLayout followReqLayout;
    private RelativeLayout followreqs,messages;

    @Override
    public void onDestroy() {
        super.onDestroy();
        followreqs=null;
        followReqLayout=null;
        followReqText=null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(GenelUtil.getCurrentUser().getPrivate()){
            followReqLayout.setVisibility(View.VISIBLE);
            if(followReqText!=null&&followreqs!=null&&GenelUtil.getCurrentUser()!=null){
                followReqText.setText(getString(R.string.followreqs)+" ("+ GenelUtil.getCurrentUser().getFollowReqCount()+")");
                followreqs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), FollowRequestActivity.class));
                    }
                });
            }

        }
        else{
            if(followReqLayout!=null){
                followReqLayout.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notif, container, false);



        followReqLayout = view.findViewById(R.id.followreqlayout);
        followReqText=view.findViewById(R.id.followrequesttext);
        followreqs = view.findViewById(R.id.followreqripple);
        if(GenelUtil.getCurrentUser().getPrivate()){

            followReqText.setText(getString(R.string.followreqs)+" ("+ GenelUtil.getCurrentUser().getFollowReqCount()+")");
            followreqs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), FollowRequestActivity.class));
                }
            });
        }
        else{
            followReqLayout.setVisibility(View.GONE);
        }
        messages = view.findViewById(R.id.messageButton);
        addBadgeToMessages((int) GenelUtil.getCurrentUser().getMessageCount());
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MessagesActivity.class));
                if(badgeView!=null){
                    badgeView.hide(true);
                    badgeView=null;

                    ParseCloud.callFunctionInBackground("notifResetMessages",new HashMap<>());
                }
            }
        });

        recyclerView = view.findViewById(R.id.notifrecyclerview);
        swipeRefreshLayout = view.findViewById(R.id.nSwipeRefresh);

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0&&linearLayoutManager.findLastVisibleItemPosition()>(list.size()-7)&&!loading&&!postson){
                    loading=true;
                    getReqs(date,false);
                }
            }
        };

        list=new ArrayList<>();
        Notif object2 = new Notif();
        object2.setType("load");
        list.add(object2);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new NotificationAdapter();
        adapter.setContext(list, Glide.with(getActivity()),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!loading){
                    loading=true;

                    date=null;

                    getReqs(null,true);

                }
            }
        };
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);



        recyclerView.addOnScrollListener(onScrollListener);

        if(!this.isDetached()){
            getReqs(null,false);
        }

        return view;
    }

    private void refreshSetting(){
        list.clear();
    }

    private void getReqs(Date date,boolean isRefresh){
        if(!this.isDetached()){
            HashMap<String, Object> params = new HashMap<String, Object>();
            if(date!=null){
                params.put("date",date);
            }
            ParseCloud.callFunctionInBackground("getNotifs", params, new FunctionCallback<HashMap>() {
                @Override
                public void done(HashMap  objects, ParseException e) {
                    Log.e("done","done");
                    if(!NotifFragment.this.isDetached()){
                        if(e==null){

                            if(objects!= null){
                                if(isRefresh){
                                    refreshSetting();
                                }
                                initList((List<Notif>)objects.get("notifs"),(boolean)objects.get("hasmore"),(Date)objects.get("date"));
                            }
                            else{
                                initList(new ArrayList<>(),false,new Date());
                            }


                        }
                        else{
                            initList(new ArrayList<>(),false,new Date());


                        }
                    }
                }
            });
        }
    }

    QBadgeView badgeView = null;
    public void addBadgeToMessages(int i){
        if(messages!=null && i > 0){
            if (badgeView == null){
                badgeView = new QBadgeView(getContext());
            }

            badgeView.setBadgeNumber(i)
                    .setBadgeGravity(Gravity.TOP|Gravity.END)
                    //.setGravityOffset(-15, -15, true)
                    .bindTarget(messages);
        }
    }

    public void notifyAdapter(){
        if(GenelUtil.getCurrentUser().getPrivate()){
            followReqLayout.setVisibility(View.VISIBLE);
            if(followReqText!=null&&followreqs!=null&&GenelUtil.getCurrentUser()!=null){
                followReqText.setText(getString(R.string.followreqs)+" ("+ GenelUtil.getCurrentUser().getFollowReqCount()+")");
                followreqs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), FollowRequestActivity.class));
                    }
                });
            }

        }
        else{
            if(followReqLayout!=null){
                followReqLayout.setVisibility(View.GONE);
            }
        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
    private boolean getActive(){
        return getActivity()!=null && GenelUtil.isAlive(getActivity());
    }


    public void Refresh(){
        if(getActive()){
            if(GenelUtil.getCurrentUser().getPrivate()){
                followReqLayout.setVisibility(View.VISIBLE);
                if(followReqText!=null&&followreqs!=null&&GenelUtil.getCurrentUser()!=null){
                    followReqText.setText(getString(R.string.followreqs)+" ("+ GenelUtil.getCurrentUser().getFollowReqCount()+")");
                    followreqs.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), FollowRequestActivity.class));
                        }
                    });
                }

            }
            else{
                if(followReqLayout!=null){
                    followReqLayout.setVisibility(View.GONE);
                }
            }
            loading=true;

            date=null;

            getReqs(null,true);
            if(swipeRefreshLayout!=null){
                swipeRefreshLayout.setRefreshing(true);
            }

        }
    }


    private void initList(List<Notif> objects,boolean hasMore,Date date) {
        if(getActive()){
            postson = !hasMore;
            this.date = date;
            if(objects.size()==0){
                loading =false;
                if(list!=null){
                    if(list.size()==0){
                        Notif post = new Notif();
                        post.setType("boş");
                        list.add(post);
                    }
                    else{
                        if(list.get(list.size()-1).getType().equals("load")){
                            list.remove(list.get(list.size()-1));
                        }
                        if(list.size()==0){
                            Notif post = new Notif();
                            post.setType("boş");
                            list.add(post);
                        }

                    }
                }

                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();



            }
            else{
                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        list.remove(list.get(list.size()-1));
                    }
                }

                if(objects.size()<40){
                    list.addAll(objects);

                    adapter.notifyDataSetChanged();

                    loading =false;
                    swipeRefreshLayout.setRefreshing(false);


                }
                else{
                    list.addAll(objects);



                    Notif load = new Notif();
                    load.setType("load");
                    list.add(load);

                    adapter.notifyDataSetChanged();

                    loading =false;
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        }

    }



    @Override
    public void goToProfile(int position) {
        if(GenelUtil.clickable(400)){
            Notif notif = list.get(position);
            if(!notif.getOwner().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                startActivity(new Intent(getContext(), GuestProfileActivity.class).putExtra("user",notif.getOwner()));
            }
        }
    }

    @Override
    public void notifClick(int position) {
        if(GenelUtil.clickable(400)){
            Notif notif = list.get(position);
            switch (notif.getType()) {
                case "followedyou":
                    if(!notif.getOwner().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                        startActivity(new Intent(getContext(), GuestProfileActivity.class).putExtra("user",notif.getOwner()));
                    }
                    break;
                case "likedpost":
                    startActivity(new Intent(getActivity(), CommentActivity.class).putExtra("id",notif.getPostId()));
                    break;
                case "mentionpost":
                    startActivity(new Intent(getActivity(), CommentActivity.class).putExtra("id",notif.getPostId()));
                    break;
                case "commentpost":
                    startActivity(new Intent(getActivity(), CommentActivity.class).putExtra("commentid",notif.getCommentId()));
                    break;
                case "replycomment":
                    startActivity(new Intent(getActivity(), CommentActivity.class).putExtra("commentid",notif.getCommentId()));
                    break;
                case "mentioncomment":
                    startActivity(new Intent(getActivity(), CommentActivity.class).putExtra("commentid",notif.getCommentId()));
                    break;
                default:
                    break;
            }

        }
    }
}
