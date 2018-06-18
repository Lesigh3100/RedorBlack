package com.kevin.android.redorblack.firebasemethods;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kevin.android.redorblack.dataclasses.Purchases;
import com.kevin.android.redorblack.utility.ServerTime;
import com.kevin.android.redorblack.utility.Utility;

import java.util.ArrayList;

import static com.kevin.android.redorblack.billing.BillingConstants.FIVE_TICKETS_PURCHASE;
import static com.kevin.android.redorblack.billing.BillingConstants.THIRTY_TICKETS_PURCHASE;
import static com.kevin.android.redorblack.billing.BillingConstants.SKU_FIVE_TICKETS;
import static com.kevin.android.redorblack.billing.BillingConstants.SKU_THIRTY_TICKETS;
import static com.kevin.android.redorblack.constants.FirebaseConstants.*;
import static com.kevin.android.redorblack.constants.GameConstants.ILLEGAL_CAST_EXCEPTION;

@Keep
public class PurchaseUpdater {

   private DatabaseReference myPurchasesRef;
   public ArrayList<Purchases> purchases;
   private DatabaseReference myUserRef;
    ServerTime serverTime;

    public PurchaseUpdater (DatabaseReference db, String myUserId){
        myPurchasesRef = db.child(PURCHASES_REFERENCE).child(myUserId);
        purchases = new ArrayList<>();
        myUserRef = db.child(USER_REFERENCE).child(myUserId);
        serverTime = new ServerTime(db);
    }

    public void getAllOfMyPurchases(final OnPurchasesUpdateFinishedListener listener){
        if (listener == null){
            return;
        }
        myPurchasesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Purchases purchase = dataSnapshot1.getValue(Purchases.class);
                    if (!purchases.contains(purchase)){
                        purchases.add(purchase);
                    }
                }
                if (purchases != null && purchases.size() > 0){
                    listener.purchasesRetrieved(purchases);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public interface OnPurchasesUpdateFinishedListener {
        void purchasesRetrieved(ArrayList<Purchases> purchases);
    }


    // method to add a purchase to firebase DB, also adds tickets to the user's account
    public void addPurchase(final Purchases purchase){
        serverTime.getTime(new ServerTime.OnTimeRetrievedListener() {
            @Override
            public void onTimeRetrieved(Long timestamp) {
                Purchases purchases1 = purchase;
                purchases1.setFirebase_time_consumed(timestamp);
                DatabaseReference thisPurchase = myPurchasesRef.push();
                thisPurchase.setValue(purchases1);
                consumePurchase(purchase.getFirebase_SKU_ID(), thisPurchase);
            }
        });

    }

    private void consumePurchase(final String sku, final DatabaseReference thisPurchase){
        myUserRef.child(PAID_TICKET_REFERENCE).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                int currentPaidTickets;
                // validate our data
                if (mutableData.getValue() != null && Utility.safeObjectToInt(mutableData.getValue()) != ILLEGAL_CAST_EXCEPTION) {
                    currentPaidTickets = Utility.safeObjectToInt(mutableData.getValue());
                } else {
                    currentPaidTickets = 0;
                }
                switch (sku){
                    case SKU_FIVE_TICKETS:
                        currentPaidTickets += FIVE_TICKETS_PURCHASE;
                        break;
                    case SKU_THIRTY_TICKETS:
                        currentPaidTickets += THIRTY_TICKETS_PURCHASE;
                        break;
                    default:
                        return Transaction.abort();
                }
                mutableData.setValue(currentPaidTickets);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean notaborted, @Nullable DataSnapshot dataSnapshot) {
            if (notaborted){
                thisPurchase.child(PURCHASE_CONSUMED).setValue(true);
            }
            }
        });
    }




    public void setPurchaseConsumed(final Purchases purchase){
        myPurchasesRef.child(purchase.getFirebase_purchaseToken()).child(PURCHASE_CONSUMED).setValue(true);
    }

    public Purchases retrievePurchase(String purchaseToken){
        if (purchases != null && purchases.size() > 0){
            for (Purchases purchases1 : purchases){
                if (purchases1.firebase_purchaseToken.equals(purchaseToken)){
                    return purchases1;
                }
            }
        }
            return null;
    }

}
