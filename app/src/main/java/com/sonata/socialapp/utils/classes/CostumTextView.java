package com.sonata.socialapp.utils.classes;

import android.content.Context;
import android.util.AttributeSet;

import com.tylersuehr.socialtextview.SocialTextView;

import java.util.regex.Pattern;

public class CostumTextView extends SocialTextView {
    private static Pattern patternMention;


    public CostumTextView(Context c) {
        super(c);
    }

    public CostumTextView(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    public CostumTextView(Context c, AttributeSet attrs, int def) {
        super(c, attrs, def);
    }

    private static Pattern getMentionPattern() {
        if (patternMention == null) {
            patternMention = Pattern.compile("/\\B@[a-z0-9]+/gi");
        }

        return patternMention;
    }

}
