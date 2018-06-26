package com.kevin.android.redorblack.gameplay;

public interface GameListener {


    void onSubtractPaidTicket();
    void onBroadCastMessage(byte[] bytes);
    void onPlaySoundEffect(int resId);
    void onEndGameSoundEffects(boolean win);
    void onGameEnded(boolean disconnected);
    void onGameStarted();
    void onGameContinued();

}
