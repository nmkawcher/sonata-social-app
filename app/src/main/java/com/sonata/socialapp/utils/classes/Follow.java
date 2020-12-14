package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Follow")
public class Follow extends ParseObject {

    public void setOwner(ParseUser user) {
        put("owner",user);
    }

    public void setWho(ParseUser user) {
        put("who",user);
    }

}
