package com.kevin.android.redorblack.signin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kevin.android.redorblack.MainActivity;
import com.kevin.android.redorblack.R;

import static com.kevin.android.redorblack.constants.GameConstants.RC_SIGN_IN;

public class GoogleSignInHandler {

private final String TAG = "GoogleSignInHolder:";
private GoogleSignInClient mGoogleSignInClient;
    private MainActivity mMainActivity;


    private ConnectionListener connectionListener;
    // The currently signed in account, used to check the account has changed outside of this activity when resuming.
    private GoogleSignInAccount mSignedInAccount = null;


    public GoogleSignInHandler(MainActivity mMainActivity, ConnectionListener connectionListener) {
        this.mMainActivity = mMainActivity;
        googleSignInAndInitilizations();
        this.connectionListener = connectionListener;
    }

    // creates google sign in options and then signs in using them
    private void googleSignInAndInitilizations() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestIdToken(mMainActivity.getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mMainActivity, gso);
    }

    public void signInSilently() {
        Log.d(TAG, "signInSilently()");

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(mMainActivity,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                            onDisconnected();
                        }
                    }
                });
    }

    public void startSignInIntent() {
        Log.d(TAG, "START SIGN IN INTENT CALLED");
        mMainActivity.startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    public void signOut() {
        Log.d(TAG, "signOut()");

        mGoogleSignInClient.signOut().addOnCompleteListener(mMainActivity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signOut(): success");
                       //     switchToLoginScreen();
                            FirebaseAuth.getInstance().signOut();
                        } else {
                       //     handleException(task.getException(), "signOut() failed!");
                        }
                        onDisconnected();
                    }
                });
    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");
        mSignedInAccount = null;
        connectionListener.onDisconnectedFromGoogle();
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        if (mSignedInAccount != googleSignInAccount) {
            mSignedInAccount = googleSignInAccount;
            connectionListener.onConnectedToGoogle(googleSignInAccount);
        }
    }

    public void resultOfSignIn(Intent intent){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            onConnected(account);
        } catch (ApiException apiException) {
            String message = apiException.getMessage();
            if (message == null || message.isEmpty()) {
                message = mMainActivity.getString(R.string.signin_other_error);
            }
            onDisconnected();
            new AlertDialog.Builder(mMainActivity)
                    .setMessage(message)
                    .setNeutralButton(R.string.login_fail, null)
                    .show();
        }
    }
    // returns true if the user is signed in
    public boolean isSignedIn() {
        return mSignedInAccount != null;
    }

    public void signInHandlerCleanUp(){
        if (isSignedIn()){
            signOut();
        }
    }

}
