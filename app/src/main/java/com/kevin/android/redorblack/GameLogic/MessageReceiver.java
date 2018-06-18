package com.kevin.android.redorblack.GameLogic;

import android.support.annotation.Keep;
import android.util.Log;

import com.kevin.android.redorblack.dataclasses.GameContainer;
import com.kevin.android.redorblack.utility.Serializer;

import java.io.IOException;

import static com.kevin.android.redorblack.constants.GameConstants.*;

@Keep
public class MessageReceiver {

    private GameContainer gameContainer;
    private final String TAG = "MessageReceiver";
    private MessagedReceivedListener listener;
    private boolean isPlayer1;


    public MessageReceiver(final MessagedReceivedListener listener) {
        this.listener = listener;
    }

    public boolean isPlayer1() {
        return isPlayer1;
    }

    public void setPlayer1(boolean player1) {
        isPlayer1 = player1;
    }

    private void handleMessage(GameContainer container) {
        gameContainer = container;
        switch (gameContainer.getSendingCode()) {
            case CHANGED_COLOR:
                Log.d(TAG, "INCOMING: COLOR");
                listener.onColorReceived(isPlayer1() ? gameContainer.getPlayer2().getMyChoice() : gameContainer.getPlayer1().getMyChoice());
                break;
            case CHANGED_GAME_INFO:
                Log.d(TAG, "INCOMING: GAME INFO");
                listener.onGameInfoReceived(gameContainer.getGameInfo());
                break;
            case CHANGED_IWANTTOCONTINUE:
                Log.d(TAG, "INCOMING: IWANTTOCONTINUE");
                listener.onOpponentWantsToContinueReceived(isPlayer1() ? gameContainer.getPlayer2().isiWantToContinue() : gameContainer.getPlayer1().isiWantToContinue(), isPlayer1() ? gameContainer.getPlayer2().getPayingForGameContinue() : gameContainer.getPlayer1().getPayingForGameContinue());
                break;
            case CHANGED_WHO_GOES_FIRST:
                Log.d(TAG, "INCOMING: WHO GOES FIRST");
                listener.onWhoPicksFirstReceived(isPlayer1() ? gameContainer.getPlayer2().isFirstToPick() : gameContainer.getPlayer1().isFirstToPick());
                break;
                /*
            case CHANGED_WHO_IS_PAYING:
                Log.d(TAG, "INCOMING: WHO IS PAYING");
                listener.onWhoIsPayingReceived(isPlayer1() ? gameContainer.getPlayer2().getPayingForGameContinue() : gameContainer.getPlayer1().getPayingForGameContinue());
                break; */
            default:
                Log.d(TAG, "NO INCOMING CODE");
                break;
        }
    }

    private GameContainer decodeMessage(byte[] message) {
        try {
            return (GameContainer) Serializer.convertFromBytes(message);
        } catch (IOException | ClassCastException | ClassNotFoundException exception) {
            Log.d(TAG, "Exception decoding message: " + exception);
        }
        return null;
    }

    public void incomingMessage(byte[] message) {
        if (decodeMessage(message) == null) {
            Log.d(TAG, "Decode Message produced null, something went wrong");
        } else {
            handleMessage(decodeMessage(message));
            Log.d(TAG, "called handleMessage");
        }
    }

}
