package com.kevin.android.redorblack.utility;

import android.support.annotation.Keep;
import android.util.Log;

@Keep
public class Utility {
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    public static int safeObjectToInt(Object object){
    try{
        return safeLongToInt((long)object);
    }catch (ClassCastException exception){
        Log.e("Exception: ", "CLASSCAST " + exception);
        return  0;
    }
    }


}
