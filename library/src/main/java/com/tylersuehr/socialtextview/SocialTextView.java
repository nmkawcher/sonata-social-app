package com.tylersuehr.socialtextview;

import android.content.Context;
import android.content.res.TypedArray;

import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.appcompat.widget.AppCompatTextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class SocialTextView extends AppCompatTextView {

    /* Constants for social media flags */
    private static final int HASHTAG = 1;
    private static final int MENTION = 2;
    private static final int PHONE = 4;
    private static final int URL = 8;
    private static final int READ_MORE = 9;
    private static final int EMAIL = 16;

    private static Pattern patternHashtag;
    private static Pattern patternMention;

    /* Mutable properties */
    private boolean underlineEnabled;
    private int selectedColor;
    private int hashtagColor;
    private int mentionColor;
    private int phoneColor;
    private int emailColor;
    private int urlColor;

    private String id;

    public void setPostId(String id){
        this.id = id;
    }
    public String getPostId(){
        return  id != null ? id : "";
    }


    private OnLinkClickListener linkClickListener;

    /* Stores enabled link modes */
    private int flags;
    @Override
    public void scrollTo(int x, int y) {

    }

    /**
     * Expandable variables
     * @param c
     */

    public SocialTextView(Context c) {
        this(c, null);
    }

    public SocialTextView(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public SocialTextView(Context c, AttributeSet attrs, int def) {
        super(c, attrs, def);
        // Set the link movement method by default
        setMovementMethod(AccurateMovementMethod.getInstance());

        // Set XML attributes
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SocialTextView);
        this.flags = a.getInt(R.styleable.SocialTextView_linkModes, -1);
        this.hashtagColor = a.getColor(R.styleable.SocialTextView_hashtagColor, Color.RED);
        this.mentionColor = a.getColor(R.styleable.SocialTextView_mentionColor, Color.RED);
        this.phoneColor = a.getColor(R.styleable.SocialTextView_phoneColor, Color.RED);
        this.emailColor = a.getColor(R.styleable.SocialTextView_emailColor, Color.RED);
        this.urlColor = a.getColor(R.styleable.SocialTextView_urlColor, Color.RED);
        this.selectedColor = a.getColor(R.styleable.SocialTextView_selectedColor, Color.LTGRAY);
        this.underlineEnabled = a.getBoolean(R.styleable.SocialTextView_underlineEnabled, false);
        if (a.hasValue(R.styleable.SocialTextView_android_text)) {
            setLinkText(a.getString(R.styleable.SocialTextView_android_text));
        }
        if (a.hasValue(R.styleable.SocialTextView_android_hint)) {
            setLinkHint(a.getString(R.styleable.SocialTextView_android_hint));
        }
        a.recycle();
    }

    /**
     * Overridden to ensure that highlighted text is always transparent.
     */
    @Override
    public void setHighlightColor(@ColorInt int color) {
        super.setHighlightColor(Color.TRANSPARENT);
    }

    public void setLinkText(String text) {
        setText(createSocialMediaSpan(text));

    }

    public void appendLinkText(String text) {
        append(createSocialMediaSpan(text));
    }

    public void setLinkHint(String textHint) {
        setHint(createSocialMediaSpan(textHint));
    }

    public void setOnLinkClickListener(OnLinkClickListener linkClickListener) {
        this.linkClickListener = linkClickListener;
    }

    public OnLinkClickListener getOnLinkClickListener() {
        return linkClickListener;
    }

    /**
     * Creates a spannable string containing the touchable spans of each link item
     * for each enabled link mode in the given text.
     *
     * @param text Text
     * @return {@link SpannableString}
     */
    private SpannableString createSocialMediaSpan(final String text) {
        final Set<LinkItem> items = collectLinkItemsFromText(text);
        final SpannableString textSpan = new SpannableString(text);

        // Create a span for each link item
        for (final LinkItem item : items) {
            textSpan.setSpan(new TouchableSpan(getColorByMode(item.mode), getColorByMode(item.mode), false) {
                @Override
                public void onClick(View widget) {
                    // Trigger callback when span is touched
                    if (linkClickListener != null) {
                        linkClickListener.onLinkClicked(item.mode, item.matched);
                    }
                }
            }, item.start, item.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return textSpan;
    }

    /**
     * Checks which flags are enable so that the appropriate link items can be
     * collected from each respective mode.
     *
     * @param text Text
     * @return Set of {@link LinkItem}
     */
    private Set<LinkItem> collectLinkItemsFromText(String text) {
        final Set<LinkItem> items = new HashSet<>();

        // Check for hashtag links, if possible
        collectLinkItems(HASHTAG, items, getHashtagPattern().matcher(text),text);


        // Check for mention links, if possible
        collectLinkItems(MENTION, items, getMentionPattern().matcher(text),text);


        // Check for phone links, if possible
        /*if ((flags & PHONE) == PHONE) {
            collectLinkItems(PHONE, items, Patterns.PHONE.matcher(text));
        }*/

        // Check for url links, if possible
        collectLinkItems(URL, items, Patterns.WEB_URL.matcher(text),text);


        // Check for email links, if possible
        /*if ((flags & EMAIL) == EMAIL) {
            collectLinkItems(EMAIL, items, Patterns.EMAIL_ADDRESS.matcher(text));
        }*/

        return items;
    }

    /**
     * Iterates through all the matches found by the given matcher and adds a new
     * {@link LinkItem} for each match into the given collection of link items.
     *
     * @param mode    {@link LinkOptions}
     * @param items   Collection of {@link LinkItem}
     * @param matcher {@link Matcher}
     */
    private void collectLinkItems(@LinkOptions int mode, Collection<LinkItem> items, Matcher matcher, String inputText) {
        while (matcher.find()) {
            if(mode == URL){
                int matcherStart = matcher.start();
                String matchedText = matcher.group();

                if (matchedText.startsWith(" ")) {
                    matcherStart += 1;
                    matchedText = matchedText.substring(1);
                }

                if(matcherStart>0){
                    char ch = inputText.charAt(matcherStart-1);
                    if(Character.toString(ch).equals("@")){
                        if(matcherStart>1){
                            if(Character.toString(inputText.charAt(matcherStart-2)).equals(" ")){
                                items.add(new LinkItem(
                                        matchedText,
                                        matcherStart,
                                        matcher.end(),
                                        MENTION
                                ));
                            }

                        }
                        else{
                            items.add(new LinkItem(
                                    matchedText,
                                    matcherStart,
                                    matcher.end(),
                                    MENTION
                            ));
                        }
                    }
                    else{
                        items.add(new LinkItem(
                                matchedText,
                                matcherStart,
                                matcher.end(),
                                mode
                        ));
                    }
                }
                else{
                    items.add(new LinkItem(
                            matchedText,
                            matcherStart,
                            matcher.end(),
                            mode
                    ));
                }
            }
            else {
                int matcherStart = matcher.start();
                String matchedText = matcher.group();

                if (matchedText.startsWith(" ")) {
                    matcherStart += 1;
                    matchedText = matchedText.substring(1);
                }
                items.add(new LinkItem(
                        matchedText,
                        matcherStart,
                        matcher.end(),
                        mode
                ));
            }


        }
    }




    LineCountListener listener;
    public void setLineCountListener(LineCountListener listener){
        this.listener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(listener!=null){
            listener.onLineCount(this.getLineCount());
        }

    }

    /**
     * Gets the corresponding color for a given mode.
     *
     * @param mode {@link #HASHTAG}, {@link #MENTION}, {@link #EMAIL}, {@link #PHONE}, {@link #URL}
     * @return Color
     */
    private int getColorByMode(@LinkOptions int mode) {
        switch (mode) {
            case HASHTAG:
                return hashtagColor;
            case MENTION:
                return mentionColor;
            case PHONE:
                return phoneColor;
            case URL:
                return urlColor;
            case EMAIL:
                return emailColor;

            default:
                throw new IllegalArgumentException("Invalid mode!");
        }
    }

    /**
     * Lazy loads the 'hashtag' regex pattern.
     *
     * @return {@link Pattern}
     */
    private static Pattern getHashtagPattern() {
        if (patternHashtag == null) {
            patternHashtag = Pattern.compile("#(\\w+)");
        }
        return patternHashtag;
    }

    /**
     * Lazy loads the 'mention' regex pattern.
     *
     * @return {@link Pattern}
     */
    private static Pattern getMentionPattern() {
        if (patternMention == null) {
            patternMention = Pattern.compile("(^|[^\\w])@([\\w\\_\\.]+)");
        }
        return patternMention;
    }




    /**
     * Data structure to store information about a link.
     */

    //
    private final class LinkItem {
        private final String matched;
        private final int start;
        private final int end;
        private final int mode;

        private LinkItem(String matched, int start, int end, int mode) {
            this.matched = matched;
            this.start = start;
            this.end = end;
            this.mode = mode;
        }
    }


    @Retention(RetentionPolicy.SOURCE)
    @Target({PARAMETER, METHOD, LOCAL_VARIABLE, FIELD})
    @IntDef(value = {HASHTAG, MENTION, PHONE, EMAIL, URL})
    public @interface LinkOptions {
    }


    /**
     * Listener for link clicks in text view.
     */
    public interface OnLinkClickListener {
        void onLinkClicked(int linkType, String matchedText);
    }

    public interface LineCountListener {
        void onLineCount(int lineCount);
    }







}