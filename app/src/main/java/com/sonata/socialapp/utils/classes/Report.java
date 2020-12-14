package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Report")
public class Report extends ParseObject {

    public void setOwner(ParseUser user){
        put("owner",user);
    }
    public void setPost(Post post){
        put("post",post);
    }

}
