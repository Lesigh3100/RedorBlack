package com.kevin.android.redorblack.GameLogic;

import android.support.annotation.Keep;

import com.kevin.android.redorblack.dataclasses.GameInfo;
@Keep
public interface MessagedReceivedListener {
    void onColorReceived(int color);
    void onWhoPicksFirstReceived(boolean iPickFirst);
    void onOpponentWantsToContinueReceived(boolean opponentWantsToContinue, int paying);
    void onGameInfoReceived(GameInfo gameInfo);
}
