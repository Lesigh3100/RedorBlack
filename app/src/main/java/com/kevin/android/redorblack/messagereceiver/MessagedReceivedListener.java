package com.kevin.android.redorblack.messagereceiver;

import android.support.annotation.Keep;

import com.kevin.android.redorblack.dataclasses.GameInfo;
@Keep
public interface MessagedReceivedListener {
    void onColorReceived(int color);
    void onWhoPicksFirstReceived(int iPickFirst);
    void onOpponentWantsToContinueReceived(int paying);
    void onGameInfoReceived(GameInfo gameInfo);
}
