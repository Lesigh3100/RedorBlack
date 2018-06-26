package com.kevin.android.redorblack.firebasemethods;

import com.google.firebase.database.DatabaseReference;

public interface FireBaseSetUpListener {
    void onFireBaseSetUp(DatabaseReference myRef, String myUID);
}
