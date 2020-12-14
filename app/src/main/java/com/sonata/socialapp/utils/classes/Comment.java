package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public List<String> getExcludeList(){
        return getList("excludelist") != null ? getList("excludelist") : new ArrayList<>();
    }

    public boolean getUpvote2(){
        return getBoolean("upvote2");
    }

    public boolean getUpvote(){
        return getBoolean("upvote");
    }

    public void setUpvote(boolean bool){
        put("upvote",bool);
    }

    public boolean getDownvote2(){
        return getBoolean("downvote2");
    }

    public void setDownvote(boolean bool){
        put("downvote",bool);
    }

    public boolean getDownvote(){
        return getBoolean("downvote");
    }

    public long getVote2(){ return getLong("vote2"); }

    public void setVote(long a){
        put("vote",a);
    }

    public long getVote(){
        return getLong("vote");
    }






    public long getReplyCount2(){
        return getLong("replycount2");
    }


    public boolean getSaved2(){
        return getBoolean("saved2");
    }
    public boolean getNsfw(){
        return getBoolean("nsfw");
    }
    public void setSaved(boolean bool){
        put("saved",bool);
    }

    public boolean getSaved(){
        return getBoolean("saved");
    }






    public void setUser(ParseUser user){
        put("user",user);
    }

    public void setPost(Post post){
        put("post",post);
    }



    public String getUrl() {
        return getString("url");
    }

    public void setUrl(String url) {
        put("url",url);
    }

    public int getRatioW() {
        return (int)get("ratiow")>0?(int)get("ratiow"):1;
    }


    public int getRatioH() {
        return (int)get("ratioh")>0?(int)get("ratioh"):1;
    }




    public void setReplyCount(long a){
        put("replycount",a);
    }

    public long getReplyCount(){
        return getLong("replycount");
    }



    public void setType(String desc){
        put("type",desc);
    }

    public String getType(){
        return getString("type")!=null?getString("type"):"load";
    }


    public SonataUser getUser(){
        return (SonataUser) get("user")!=null?(SonataUser) get("user"):new SonataUser();
    }

    public Post getPost(){
        return (Post)get("post")!=null?(Post)get("post"):new Post();
    }

    public String getDesc(){
        return  getString("description")!=null?getString("description"):"";
    }

    public ParseFile getMainMedia() {
        return getParseFile("mainmedia");
    }


    public ParseFile getThumbMedia() {
        return getParseFile("thumbmedia");
    }





}
