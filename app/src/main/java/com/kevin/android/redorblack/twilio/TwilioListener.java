package com.kevin.android.redorblack.twilio;



public interface TwilioListener {

    void onRemoteParticipantConnected();
    void onOtherPersonDisconnected();
    void onIWasDisconnected();
    void onErrorConnecting();


}
