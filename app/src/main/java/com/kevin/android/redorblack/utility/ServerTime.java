package com.kevin.android.redorblack.utility;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
@Keep
public class ServerTime {

    public interface OnTimeRetrievedListener {
        void onTimeRetrieved(Long timestamp);
    }

    private final DatabaseReference db;

    public ServerTime(DatabaseReference db) {
        this.db = db.child("serverTime");
    }

    /**
     * Gets the server's timestamp in milliseconds.
     * @param listener {@link OnTimeRetrievedListener}
     */
    public void getTime(final OnTimeRetrievedListener listener) {
        if (listener == null) {
            return;
        }

        db.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listener.onTimeRetrieved(dataSnapshot.getValue(Long.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

            }
        });
    }
}