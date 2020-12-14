package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
@ParseClassName("CommentUpvote")
public class CommentUpvote extends ParseObject {

    public String getCommentID(){
        return getString("commentid");
    }

}
