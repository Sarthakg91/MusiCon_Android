package com.sarthakghosh.musicon_spotifyscreen.pam;

import android.graphics.drawable.Drawable;

/**
 * Created by Sarthak Ghosh on 06-03-2016.
 */
public class UtilClass {
    public static Drawable getMyDrawable() {
        return myDrawable;
    }

    public static void setMyDrawable(Drawable myDrawable) {
        UtilClass.myDrawable = myDrawable;
    }

    private static Drawable myDrawable;

}
