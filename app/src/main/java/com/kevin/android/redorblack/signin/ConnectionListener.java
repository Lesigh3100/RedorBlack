package com.kevin.android.redorblack.signin;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface ConnectionListener {

    void onDisconnectedFromGoogle();
    void onConnectedToGoogle(GoogleSignInAccount googleSignInAccount);
}
