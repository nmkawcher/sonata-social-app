package com.sonata.socialapp.utils;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.danikula.videocache.HttpProxyCacheServer;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sonata.socialapp.utils.classes.Block;
import com.sonata.socialapp.utils.classes.Chat;
import com.sonata.socialapp.utils.classes.Comment;
import com.sonata.socialapp.utils.classes.CommentDownvote;
import com.sonata.socialapp.utils.classes.CommentUpvote;
import com.sonata.socialapp.utils.classes.Follow;
import com.sonata.socialapp.utils.classes.Group;
import com.sonata.socialapp.utils.classes.GroupPost;
import com.sonata.socialapp.utils.classes.Like;
import com.sonata.socialapp.utils.classes.Message;
import com.sonata.socialapp.utils.classes.Notif;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.Report;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.squareup.leakcanary.LeakCanary;
import com.zxy.tiny.Tiny;




public class MyApp extends Application {

    public static boolean ses = false;
    @Override
    public void onCreate() {
        super.onCreate();
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        else{
            LeakCanary.install(this);
        }*/



        Tiny.getInstance().init(this);



        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Like.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(Report.class);
        ParseObject.registerSubclass(CommentUpvote.class);
        ParseObject.registerSubclass(CommentDownvote.class);
        ParseObject.registerSubclass(Block.class);
        ParseObject.registerSubclass(GroupPost.class);
        ParseObject.registerSubclass(Chat.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Notif.class);
        ParseObject.registerSubclass(Follow.class);
        ParseUser.registerSubclass(SonataUser.class);


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("fDVnuSxHVTycjWW2J3ZG9WHukxewXxZq")
                .enableLocalDataStore()
                .server("http://23.94.219.164:1337/parse/")
                .build());


    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApp app = (MyApp) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }


}
