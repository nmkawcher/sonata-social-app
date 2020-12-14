package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {
    public void setOwner(ParseUser user){
        put("owner",user);
    }

    public void setPost(Post post){
        put("post",post);
    }

    public void setOPostID(String id){
        put("postid",id);
    }

    public String getPostID(){
        return getString("postid");
    }
}
