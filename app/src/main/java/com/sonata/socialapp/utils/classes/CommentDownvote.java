package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
@ParseClassName("CommentDownvote")
public class CommentDownvote extends ParseObject {

    public String getCommentID(){
        return getString("commentid");
    }
}
