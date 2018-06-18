package com.kevin.android.redorblack.dataclasses;

import android.support.annotation.Keep;

@Keep
public class Purchases {

public String firebase_SKU_ID;
public Object firebase_time_consumed;
public boolean firebase_purchase_consumed;
public String firebase_purchaseToken;

    public Purchases (String firebase_purchaseToken, String firebase_SKU_ID, boolean firebase_purchase_consumed){
        this.firebase_SKU_ID = firebase_SKU_ID;
        this.firebase_purchaseToken = firebase_purchaseToken;
        this.firebase_purchase_consumed = firebase_purchase_consumed;
    }

    public Purchases() {
    }

    public String getFirebase_SKU_ID() {
        return firebase_SKU_ID;
    }

    public void setFirebase_SKU_ID(String firebase_SKU_ID) {
        this.firebase_SKU_ID = firebase_SKU_ID;
    }

    public Object getFirebase_time_consumed() {
        return firebase_time_consumed;
    }

    public void setFirebase_time_consumed(Object firebase_time_consumed) {
        this.firebase_time_consumed = firebase_time_consumed;
    }

    public boolean isFirebase_purchase_consumed() {
        return firebase_purchase_consumed;
    }

    public void setFirebase_purchase_consumed(boolean firebase_purchase_consumed) {
        this.firebase_purchase_consumed = firebase_purchase_consumed;
    }

    public String getFirebase_purchaseToken() {
        return firebase_purchaseToken;
    }

    public void setFirebase_purchaseToken(String firebase_purchaseToken) {
        this.firebase_purchaseToken = firebase_purchaseToken;
    }
}
