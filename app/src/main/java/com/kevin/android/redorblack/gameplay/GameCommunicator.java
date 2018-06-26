package com.kevin.android.redorblack.gameplay;

public interface GameCommunicator {
    void onSetMyChoice(int color);
    void onSetOtherPlayerChoice(int color);
    void onGameFinished();
    void onSetIWantToContinue(int paying);
    void onSetOpponentWantsToContinueReceived(int paying);
    void onSetWhoPicksFirst(int whoPicksFirst);
    void onSetIsPlayerOne(boolean playerOne);
}
