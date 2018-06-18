package com.kevin.android.redorblack.firebasemethods;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kevin.android.redorblack.dataclasses.Game;
import com.kevin.android.redorblack.dataclasses.User;
import com.kevin.android.redorblack.utility.Utility;

import static com.kevin.android.redorblack.constants.FirebaseConstants.CODE_ABUSE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.CODE_NUDITY;
import static com.kevin.android.redorblack.constants.FirebaseConstants.GAMES_REFERENCE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.TIMES_MADE_REPORT_REFERENCE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.TIMES_REPORTED_FOR_ABUSE_REFERENCE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.TIMES_REPORTED_FOR_NUDITY_REFERENCE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.USER_REFERENCE;
import static com.kevin.android.redorblack.constants.GameConstants.ILLEGAL_CAST_EXCEPTION;

@Keep
public class Reporter {
    public interface OnUserRetrievedListener {
        void onUserRetrieved(String id);
    }

    private final DatabaseReference db;

    public Reporter(DatabaseReference db) {
        this.db = db;
    }


    public void getUser(String lastGameNumber, final String myId, final Reporter.OnUserRetrievedListener listener) {
        if (listener == null) {
            return;
        }
        db.child(GAMES_REFERENCE).child(lastGameNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    Log.d("Redorblack", "SNAPSHOT COMING BACK NULL IN REPORTER");
                } else {
                    Log.d("Redorblack", "SNAPSHOT COMING BACK NOT NULL");
                }

                try {
                    Game game = dataSnapshot.getValue(Game.class);
                    String user = game.player1.equals(myId) ? game.player2 : game.player1;
                    listener.onUserRetrieved(user);
                } catch (Exception exception) {
                    Log.d("Redorblack", "EXCEPTION IN REPORTER: " + exception);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    // report the player you played against last for misconduct
    public void reportUser(final int report, String lastGameNumber, final String myFBId, User user){
        // no last game reference, can't report
        if (lastGameNumber.equals("")){
            return;
        }
        if (canReport(user)){
            getUser(lastGameNumber, myFBId ,new Reporter.OnUserRetrievedListener() {
                @Override
                public void onUserRetrieved(String id) {
                    if (report == CODE_ABUSE){
                        db.child(USER_REFERENCE).child(id).child(TIMES_REPORTED_FOR_ABUSE_REFERENCE).runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                if (mutableData.getValue() == null) {
                                    mutableData.setValue(1);
                                    return Transaction.success(mutableData);
                                }
                                int reports = Utility.safeObjectToInt(mutableData.getValue());
                                if (reports != ILLEGAL_CAST_EXCEPTION){
                                    reports += 1;
                                    mutableData.setValue(reports);
                                    return Transaction.success(mutableData);
                                } else {
                                    return Transaction.abort();
                                }
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                if (!b){
                                    Log.d("Redorblack", "Report transaction aborted");
                                } else {
                                    addTimesReported(db.child(USER_REFERENCE).child(myFBId));
                                }
                            }
                        });

                    } else if (report == CODE_NUDITY){
                        db.child(USER_REFERENCE).child(id).child(TIMES_REPORTED_FOR_NUDITY_REFERENCE).runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                if (mutableData.getValue() == null) {
                                    mutableData.setValue(1);
                                    return Transaction.success(mutableData);
                                }
                                int reports = Utility.safeObjectToInt(mutableData.getValue());
                                if (reports != ILLEGAL_CAST_EXCEPTION){
                                    reports += 1;
                                    mutableData.setValue(reports);
                                    return Transaction.success(mutableData);
                                } else {
                                    return Transaction.abort();
                                }
                            }
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                if (!b){
                                    Log.d("Redorblack", "Report transaction aborted");
                                } else {
                                    addTimesReported(db.child(USER_REFERENCE).child(myFBId));
                                }
                            }
                        });

                    }


                }
            });
        } else {
            addTimesReported(db.child(USER_REFERENCE).child(myFBId));
        }

    }

    // returns true if user hasn't reported excessively
    private boolean canReport(User mUser){
        if (mUser.gamesPlayed > 0){
            final double ratio = mUser.gamesPlayed / mUser.timesMadeReport;
            if (mUser.gamesPlayed < 20 && mUser.timesMadeReport < 5){
                return true;
            }
            return ratio <= .25;
        } else {
            return true;
        }
    }

    // adds +1 to the amount of times a player has made reports to make sure they aren't just constantly reporting
    private void addTimesReported(DatabaseReference userRef){
        userRef.child(TIMES_MADE_REPORT_REFERENCE).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                if (mutableData.getValue() == null){
                    mutableData.setValue(1);
                    return Transaction.success(mutableData);
                } else {
                    int i = Utility.safeObjectToInt(mutableData.getValue());
                    mutableData.setValue(i + 1);
                    return Transaction.success(mutableData);
                }
            }
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }
}
