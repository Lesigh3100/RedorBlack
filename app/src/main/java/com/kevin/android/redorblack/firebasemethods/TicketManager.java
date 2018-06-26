package com.kevin.android.redorblack.firebasemethods;

import android.os.Handler;
import android.support.annotation.Keep;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kevin.android.redorblack.dataclasses.User;
import com.kevin.android.redorblack.uicontrols.UIListener;
import com.kevin.android.redorblack.utility.ServerTime;
import com.kevin.android.redorblack.utility.Utility;

import static com.kevin.android.redorblack.constants.GameConstants.*;
import static com.kevin.android.redorblack.constants.FirebaseConstants.*;
@Keep
public class TicketManager {

    private final String TAG = "Ticketmanager";
    private int tokensBeingAdded;
    private int secondsUntilFreeToken;
    private DatabaseReference userRef;
    private ServerTime serverTime;


    private int currentFreeTokensInternal;
    private int currentPaidTokensInternal;
  //  private Object timeTokenUsedInternal;
    private long timeTokenUsedInternal;
    private UIListener uiListener;

    public TicketManager(DatabaseReference userRef, UIListener uiListener) {
        this.userRef = userRef;
        serverTime = new ServerTime(userRef);
        this.uiListener = uiListener;
    }

    /*
    public void setInternals(int currentFreeTokensInternal, int currentPaidTokensInternal, Object timeTokenUsedInternal){
    this.currentFreeTokensInternal = currentFreeTokensInternal;
    this.currentPaidTokensInternal = currentPaidTokensInternal;
    this.timeTokenUsedInternal = timeTokenUsedInternal;
    } */

    public void setInternals(int currentFreeTokensInternal, int currentPaidTokensInternal, long timeTokenUsedInternal){
        this.currentFreeTokensInternal = currentFreeTokensInternal;
        this.currentPaidTokensInternal = currentPaidTokensInternal;
        this.timeTokenUsedInternal = timeTokenUsedInternal;
    }

    public void setCurrentFreeTokensInternal(int currentFreeTokensInternal) {
        this.currentFreeTokensInternal = currentFreeTokensInternal;
    }

    public void setCurrentPaidTokensInternal(int currentPaidTokensInternal) {
        this.currentPaidTokensInternal = currentPaidTokensInternal;
    }

    public void setTimeTokenUsedInternal(long timeTokenUsedInternal) {
        this.timeTokenUsedInternal = timeTokenUsedInternal;
    }

    /*
    public void setTimeTokenUsedInternal(Object timeTokenUsedInternal) {
        this.timeTokenUsedInternal = timeTokenUsedInternal;
    } */

    // start process of subtracting a paid token from the User
    public void subtractFreeTokenFromUser(){
        tokenUpdater(CODE_SUBTRACT_FREE_TOKEN, CODE_NEED_TIME_STAMP, CODE_TOKEN_MATH_IRRELEVANT);
    }

    public void subtractPaidTokenFromUser(){
        tokenUpdater(CODE_SUBTRACT_PAID_TOKEN, CODE_DONT_NEED_TIME_STAMP, CODE_TOKEN_MATH_IRRELEVANT);
    }

    public void checkIfWeNeedToStartTimer(){
        Log.d(TAG, "checkIfweneedtostarttimer called");
        tokenUpdater(CODE_CHECK_IF_WE_NEED_TO_START_OR_CONTINUE_TIMER, CODE_NEED_TIME_STAMP, CODE_TOKEN_MATH_IRRELEVANT);
    }

    // switch to control token logic, if we need time stamp it retrieves it before feeding back into the switch
    private void tokenUpdater(final int incomingCode, final long timestamp, final int tokenAddedOrSubtractedCode) {
        if (timestamp == CODE_NEED_TIME_STAMP) {
            getTimeStampForTokenUpdater(incomingCode, tokenAddedOrSubtractedCode);
        } else {
            switch (incomingCode) {
                case CODE_ADD_FREE_TOKENS:
                    addFreeTokens(timestamp);
                    break;
                case CODE_SUBTRACT_FREE_TOKEN:
                    subtractFreeToken();
                    break;
                case CODE_SUBTRACT_PAID_TOKEN:
                    subtractPaidToken();
                    break;
                case CODE_CLOCK_TIME_RAN_OUT:
                    freeTokenClockTimeExpired();
                    break;
                // this one currently not being used
                case CODE_SET_NEW_TIME_STAMP:
                    setNewTimeUntilFreeToken(timestamp);
                    break;
                case CODE_RESTART_CLOCK:
                    restartCountDownClock(timestamp);
                    break;
                case CODE_CHECK_IF_WE_NEED_TO_START_OR_CONTINUE_TIMER:
                    checkIfWeNeedTimer(timestamp, tokenAddedOrSubtractedCode);
                    break;
            }
        }
    }


    // figures out if we need to restart the timer for free tokens
    private void checkIfWeNeedTimer(final long timestamp, final int tokenAddedOrSubtractedCode){
        switch (tokenAddedOrSubtractedCode){
            case CODE_TOKEN_ADDED:
                if (tokensLessThanMax()){
                    // figure out new timestamp & restart the clock
                    final long newTimeStamp = timestamp + (FREE_TOKEN_REFRESH_TIME_MILLIS * tokensBeingAdded);
                    userRef.child(TIME_TOKEN_USED_REFERENCE).setValue(newTimeStamp);
                    tokensBeingAdded = 0;
                    tokenUpdater(CODE_RESTART_CLOCK, newTimeStamp, CODE_TOKEN_MATH_IRRELEVANT);
                }
                break;
            case CODE_TOKEN_SUBTRACTED:
                // need to restart the timer
                if (currentFreeTokensInternal == USER_MAX_FREE_TOKENS - 1){
                    tokenUpdater(CODE_SET_NEW_TIME_STAMP, timestamp, CODE_TOKEN_MATH_IRRELEVANT);
                }
                break;
            case CODE_TOKEN_MATH_IRRELEVANT:
                if (tokensLessThanMax()){
                    Log.d(TAG, "tokens math irrelevant, tokens less than max is true");
                    if (timestamp <= timeTokenUsedInternal){
                        Log.d(TAG, "add free tokens");
                        tokenUpdater(CODE_ADD_FREE_TOKENS, timestamp, CODE_TOKEN_MATH_IRRELEVANT);
                    } else {
                        Log.d(TAG, "restart clock");
                        tokenUpdater(CODE_RESTART_CLOCK, timeTokenUsedInternal, CODE_TOKEN_MATH_IRRELEVANT);
                    }
                }
                break;
            default:
                break;
        }
    }
    // returns true if we have less than max free tokens currently
    public boolean tokensLessThanMax(){
        return currentFreeTokensInternal < USER_MAX_FREE_TOKENS;
    }

    // free token clock ran out, figure out what to do
    private void freeTokenClockTimeExpired(){
        tokenUpdater(CODE_ADD_FREE_TOKENS, CODE_NEED_TIME_STAMP, CODE_TOKEN_MATH_IRRELEVANT);
    }


    // gets a timestamp from firebase server and goes back to TokenUpdater now entering the switch
    private void getTimeStampForTokenUpdater(final int incomingCode, final int tokenAddedOrSubtractedCode) {
        serverTime.getTime(new ServerTime.OnTimeRetrievedListener() {
            @Override
            public void onTimeRetrieved(Long timestamp) {
                tokenUpdater(incomingCode, timestamp, tokenAddedOrSubtractedCode);
            }
        });
    }

    // this method
    private void restartCountDownClock(final Long updatedUserTimestamp) {
        // timestamp here is the time when we get a new token so we need to get the current time then set a new countdown
        serverTime.getTime(new ServerTime.OnTimeRetrievedListener() {
            @Override
            public void onTimeRetrieved(Long currentServerTimestamp) {

                int countdown = Utility.safeLongToInt((updatedUserTimestamp + FREE_TOKEN_REFRESH_TIME_MILLIS - currentServerTimestamp) / 1000);
                if (countdown > FREE_TOKEN_REFRESH_TIME_SECONDS){
                    countdown = FREE_TOKEN_REFRESH_TIME_SECONDS;
                }

                if (countdown <= 0) {
                    tokenUpdater(CODE_ADD_FREE_TOKENS, currentServerTimestamp, CODE_TOKEN_MATH_IRRELEVANT);
                }
                else {
                    startFreeTokenCountDown(countdown);
                }
            }
        });
    }

    // set the new timestamp for
    private void setNewTimeUntilFreeToken(final Long timestamp) {
        userRef.child(TIME_TOKEN_USED_REFERENCE).setValue(timestamp);
        userRef.child(TIME_TOKEN_USED_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timeTokenUsedInternal = (long)dataSnapshot.getValue();
                tokenUpdater(CODE_RESTART_CLOCK, timestamp, CODE_TOKEN_MATH_IRRELEVANT);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // method to add 1 or more free tokens based on how much time has passed since our previous server timestamp
    private void addFreeTokens(final long timestamp) {
        if (currentFreeTokensInternal >= USER_MAX_FREE_TOKENS) {
            return;
        }
        final long timeLastTokenUsed = timeTokenUsedInternal;
        if (timestamp >= timeLastTokenUsed + FREE_TOKEN_REFRESH_TIME_MILLIS) {
            userRef.child(FREE_TICKET_REFERENCE).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    // keep track of how many tokens were added
                    int tokensAdded = 0;
                    // local variable for current tokens
                    int currentFreeTokens;
                    // validate our data
                    if (mutableData.getValue() != null && Utility.safeObjectToInt(mutableData.getValue()) != ILLEGAL_CAST_EXCEPTION) {
                        currentFreeTokens = Utility.safeObjectToInt(mutableData.getValue());
                    } else {
                        currentFreeTokens = currentFreeTokensInternal;
                    }
                    // this is the timestamp of what time we get a new free token
                    long timeWeGetNewFreeToken = (timeLastTokenUsed + FREE_TOKEN_REFRESH_TIME_MILLIS);

                    // check if we are under the max # of free tokens
                    if (currentFreeTokens < USER_MAX_FREE_TOKENS) {
                        // for loop to determine how many tokens we get, every time it compares the current time vs the time it needs to get another token - which is increased by the amount of time to get a new free token
                        for (int i = currentFreeTokens; i < USER_MAX_FREE_TOKENS; i++) {
                            if (timestamp >= timeWeGetNewFreeToken) {
                                currentFreeTokens++;
                                tokensAdded++;
                                timeWeGetNewFreeToken += FREE_TOKEN_REFRESH_TIME_MILLIS;
                            }
                        }

                        // tokensBeingAdded = tokensAdded;

                    } else {
                        return Transaction.abort();
                    }
                    // if we still don't have max tokens, set the clock again
                    if (currentFreeTokens < USER_MAX_FREE_TOKENS) {
                        tokensBeingAdded = tokensAdded;
                        // restart clock after data is pushed
                    }
                    mutableData.setValue(currentFreeTokens);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean notAborted, DataSnapshot dataSnapshot) {
                    if (notAborted){
                        tokenUpdater(CODE_CHECK_IF_WE_NEED_TO_START_OR_CONTINUE_TIMER, timeLastTokenUsed, CODE_TOKEN_ADDED);
                    } else {
                        // user had max tokens
                    }
                }
                // passing false here so that function isn't locally executed
            }, false);
        }

    }

    // method to subtract a free token
    private void subtractFreeToken() {
        userRef.child(FREE_TICKET_REFERENCE).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData mutableData) {
                int currentFreeTokens;
                // validate our data
                if (mutableData.getValue() != null && Utility.safeObjectToInt(mutableData.getValue()) != ILLEGAL_CAST_EXCEPTION) {
                    currentFreeTokens = Utility.safeObjectToInt(mutableData.getValue());
                }
                else {
                    currentFreeTokens = 0;
                }
                if (currentFreeTokens <= 0) {
                    return Transaction.abort();
                }
                --currentFreeTokens;

                mutableData.setValue(currentFreeTokens);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean notAborted, DataSnapshot dataSnapshot) {
                if (notAborted){
                    tokenUpdater(CODE_CHECK_IF_WE_NEED_TO_START_OR_CONTINUE_TIMER, CODE_NEED_TIME_STAMP, CODE_TOKEN_SUBTRACTED);
                } else {
                    // transaction aborted
                }
            }
        }, false);
    }

    // method to subtract a paid token
    private void subtractPaidToken() {
        userRef.child(PAID_TICKET_REFERENCE).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData mutableData) {
                int currentPaidTokens;
                if (mutableData.getValue() != null && Utility.safeObjectToInt(mutableData.getValue()) != ILLEGAL_CAST_EXCEPTION) {
                    currentPaidTokens = Utility.safeObjectToInt(mutableData.getValue());
                } else {
                    currentPaidTokens = currentPaidTokensInternal;
                }
                if (currentPaidTokens <= 0) {
                    // not enough tokens
                    return Transaction.abort();
                } else {
                    --currentPaidTokens;
                }
                mutableData.setValue(currentPaidTokens);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean notAborted, DataSnapshot dataSnapshot) {
                if (!notAborted) {

                }
            }
        }, false);
    }

    // handles counting down the time until player gets another free token
    private void startFreeTokenCountDown(int timeToRun) {
        secondsUntilFreeToken = timeToRun;
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (secondsUntilFreeToken <= 0) {
                    uiListener.onFreeTokenCountDownTextUpdate(secondsUntilFreeToken);
                    tokenUpdater(CODE_CLOCK_TIME_RAN_OUT, CODE_NEED_TIME_STAMP, CODE_TOKEN_MATH_IRRELEVANT);
                    return;
                }
                freeTokenCounterTick();
                h.postDelayed(this, 1000);
            }
        }, 1000);
    }
    // updates count for the free token countdown clock
    private void freeTokenCounterTick() {
        if (secondsUntilFreeToken > 0) {
            --secondsUntilFreeToken;
        }
        //   Log.d("FREE TOKEN COUNTER","TIME = " + Integer.toString(secondsUntilFreeToken));
        uiListener.onFreeTokenCountDownTextUpdate(secondsUntilFreeToken);
    }


}
