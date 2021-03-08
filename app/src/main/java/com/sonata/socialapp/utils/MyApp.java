package com.sonata.socialapp.utils;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sonata.socialapp.utils.classes.Chat;
import com.sonata.socialapp.utils.classes.Comment;
import com.sonata.socialapp.utils.classes.Group;
import com.sonata.socialapp.utils.classes.GroupPost;
import com.sonata.socialapp.utils.classes.Message;
import com.sonata.socialapp.utils.classes.Notif;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.zxy.tiny.Tiny;




public class MyApp extends Application {

    public static boolean ses = false;
    public static SimpleCache simpleCache;
    Context context;

    public static String whereAmI= "";

    public static final int TYPE_HASHTAG = 1;
    public static final int TYPE_LINK = 8;
    public static final int TYPE_MENTION = 2;


    @Override
    public void onCreate() {
        super.onCreate();
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        else{
            LeakCanary.install(this);
        }*/

        context = this;
        LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024);
        ExoDatabaseProvider databaseProvider = new ExoDatabaseProvider(this);

        if (simpleCache == null) {
            simpleCache = new SimpleCache(getCacheDir(), leastRecentlyUsedCacheEvictor, databaseProvider);
        }

        if(GenelUtil.getNightModeApp(this)){
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }

        Tiny.getInstance().init(this);



        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(GroupPost.class);
        ParseObject.registerSubclass(Chat.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Notif.class);
        ParseUser.registerSubclass(SonataUser.class);


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("fDVnuSxHVTycjWW2J3ZG9WHukxewXxZq")
                .enableLocalDataStore()
                .server("http://167.86.118.172:1337/parse/")
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
