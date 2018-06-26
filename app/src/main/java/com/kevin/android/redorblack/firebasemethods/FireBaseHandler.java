package com.kevin.android.redorblack.firebasemethods;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.android.redorblack.MainActivity;
import com.kevin.android.redorblack.dataclasses.Game;
import com.kevin.android.redorblack.dataclasses.User;


import com.kevin.android.redorblack.uicontrols.UIListener;
import com.kevin.android.redorblack.utility.ServerTime;
import com.kevin.android.redorblack.utility.Utility;

import static com.kevin.android.redorblack.constants.FirebaseConstants.FREE_TICKET_REFERENCE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.GAMES_REFERENCE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.PAID_TICKET_REFERENCE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.PLAYER_TWO_REFERENCE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.TIME_TOKEN_USED_REFERENCE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.USER_GAMES_REFERENCE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.USER_REFERENCE;
import static com.kevin.android.redorblack.constants.GameConstants.FREE_TICKET;
import static com.kevin.android.redorblack.constants.GameConstants.NEED_TO_PURCHASE_TOKENS;
import static com.kevin.android.redorblack.constants.GameConstants.NEW_USER_FREE_STARTING_TOKENS;
import static com.kevin.android.redorblack.constants.GameConstants.PAID_TICKET;

public class FireBaseHandler {

    private final String TAG = "FireBaseSignIn";
    // Firebase
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    ServerTime serverTime;
    public DatabaseReference myRef;
    public DatabaseReference userRef;
    public String myFBId;
    UIListener uiListener;
    FireBaseSetUpListener fireBaseSetUpListener;
    private TicketManager ticketManager;
    User mUser;
    MainActivity mainActivity;
    Reporter reporter;

    public FireBaseHandler(UIListener uiListener, FireBaseSetUpListener fireBaseSetUpListener ,MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        FirebaseApp.initializeApp(mainActivity);
        setUpFirebase();
        this.uiListener = uiListener;
        this.fireBaseSetUpListener = fireBaseSetUpListener;
    }

    private void setUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        reporter = new Reporter(myRef);
        serverTime = new ServerTime(myRef);
    }

    // sign into firebase's auth with googlesignin
    public void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                userRef = myRef.child(USER_REFERENCE).child(user.getUid());
                                // uid updated, initilize billing now
                                myFBId = mAuth.getCurrentUser().getUid();
                                fireBaseSetUpListener.onFireBaseSetUp(myRef, myFBId);
                                // set ticket manager now that we have written userRef
                                ticketManager = new TicketManager(userRef, uiListener);
                                // add new user to database here & give them tokens to start playing
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    writeNewUser(myFBId, user.getDisplayName());
                                } else {
                                    retrieveUser(user);
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            uiListener.onSignInFailure();
                        }
                    }
                });
    }

    // retrieves the user from FB database, sets several listeners to follow tokens being updated
    private void retrieveUser(final FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            myFBId = firebaseUser.getUid();
            // made this into 1 time listener
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null) {
                        writeNewUser(firebaseUser.getUid(), firebaseUser.getDisplayName());
                    } else {
                        mUser = user;
                        // if the user is less than full on free tokens, check to see if they have acquired any
                        ticketManager.setInternals(mUser.getFreeTokens(), mUser.getPaidTokens(), (long)mUser.getTimeTokenUsed());
                        if (ticketManager.tokensLessThanMax()) {
                            Log.d(TAG, "tokenslessThanMax called");
                            ticketManager.checkIfWeNeedToStartTimer();
                        }
                        uiListener.onUpdateUserUi(mUser.getFreeTokens(), mUser.getPaidTokens());

                        setDataBaseListenersFireBase();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "FIREBASE DATABASE ERROR: " + databaseError);

                }
            });

        }
    }

    // create a new user on first login, sending their ID to the firebase database
    private void writeNewUser(String userId, String username) {
        User user = new User(username, NEW_USER_FREE_STARTING_TOKENS);
        myRef.child("users").child(userId).setValue(user);
    }

    public DatabaseReference getMyRef() {
        return myRef;
    }

    public DatabaseReference getUserRef() {
        return userRef;
    }

    public String getMyFBId() {
        return myFBId;
    }

    /*
    // firebase listeners
     */
    ValueEventListener freeTokenListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null && mUser != null) {
                int tickets = Utility.safeObjectToInt(dataSnapshot.getValue());
                mUser.freeTokens = tickets;
                ticketManager.setCurrentFreeTokensInternal(tickets);
                uiListener.onUpdateUserUi(mUser.getFreeTokens(), mUser.getPaidTokens());
            } else {
                Log.d(TAG, "Error updating free tokens");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ValueEventListener paidTokenListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null && mUser != null) {
                int tickets = Utility.safeObjectToInt(dataSnapshot.getValue());
                mUser.paidTokens = tickets;
                ticketManager.setCurrentPaidTokensInternal(tickets);
                uiListener.onUpdateUserUi(mUser.getFreeTokens(), mUser.getPaidTokens());
            } else {
                Log.d(TAG, "Error updating paid tokens");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ValueEventListener timeTokenUsedListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null && mUser != null) {
                mUser.timeTokenUsed = dataSnapshot.getValue();
                ticketManager.setTimeTokenUsedInternal((long)dataSnapshot.getValue());
            } else {
                Log.d(TAG, "Error updating free token timestamp");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    public void setDataBaseListenersFireBase(){
        userRef.child(FREE_TICKET_REFERENCE).addValueEventListener(freeTokenListener);
        userRef.child(PAID_TICKET_REFERENCE).addValueEventListener(paidTokenListener);
        userRef.child(TIME_TOKEN_USED_REFERENCE).addValueEventListener(timeTokenUsedListener);
    }

    public void removeDatabaseListenersFireBase(){
        if (userRef != null){
            userRef.removeEventListener(freeTokenListener);
            userRef.removeEventListener(paidTokenListener);
            userRef.removeEventListener(timeTokenUsedListener);
        }
    }

    public int whatTokenToUse() {
        if (mUser.freeTokens > 0) {
            return FREE_TICKET;
        } else if (mUser.paidTokens > 0) {
            return PAID_TICKET;
        } else return NEED_TO_PURCHASE_TOKENS;
    }

    public boolean hasPaidTokens() {
        return mUser.paidTokens > 0;
    }

    public void reportUser(final int report, String lastGameNumber){
        reporter.reportUser(report, lastGameNumber, myFBId, mUser);
    }

    public void subtractFreeTicket(){
        ticketManager.subtractFreeTokenFromUser();
    }

    public void subtractPaidTicket(){
        ticketManager.subtractPaidTokenFromUser();
    }

    public void receiveAndLogNewGame(String gameReference, Object timeGameCreated){
        myRef.child(GAMES_REFERENCE).child(gameReference).child(PLAYER_TWO_REFERENCE).setValue(myFBId);
        myRef.child(USER_GAMES_REFERENCE).child(myFBId).child(gameReference).setValue(timeGameCreated);
    }


    public String createNewGameAndLog(Long timestamp){
        DatabaseReference newGameReference = myRef.child(GAMES_REFERENCE).push();
        String gameReference = newGameReference.getKey();
        Game game = new Game(getMyFBId(), timestamp);
        myRef.child(USER_GAMES_REFERENCE).child(getMyFBId()).child(gameReference).setValue(timestamp);
        newGameReference.setValue(game);

        return gameReference;
    }

    public boolean isFireBaseUserNotNull(){
        return mUser != null;
    }

}
