package com.kevin.android.redorblack.messagereceiver;

import android.support.annotation.Keep;
import android.util.Log;

import com.kevin.android.redorblack.dataclasses.GameContainer;
import com.kevin.android.redorblack.utility.Serializer;

import java.io.IOException;

import static com.kevin.android.redorblack.constants.GameConstants.*;

@Keep
public class MessageReceiver {


    private final String TAG = "MessageReceiver";
    private MessagedReceivedListener listener;

    public MessageReceiver(final MessagedReceivedListener listener) {
        this.listener = listener;

    }

    private void handleMessage(GameContainer container) {

        switch (container.getSendingCode()) {
            case CHANGED_COLOR:
                Log.d(TAG, "INCOMING: COLOR");
                listener.onColorReceived(container.getChangedCode());
                break;
            case CHANGED_I_WANT_TO_CONTINUE:
                Log.d(TAG, "INCOMING: I WANT TO CONTINUE");
                listener.onOpponentWantsToContinueReceived(container.getChangedCode());
                break;
            case CHANGED_WHO_GOES_FIRST:
                Log.d(TAG, "INCOMING: WHO GOES FIRST");
          //      listener.onWhoPicksFirstReceived(isPlayer1() ? container.getPlayer2().isFirstToPick() : container.getPlayer1().isFirstToPick());
                listener.onWhoPicksFirstReceived(container.getChangedCode());
                break;
            case CHANGED_GAME_INFO:
                Log.d(TAG, "INCOMING: GAME INFO");
                listener.onGameInfoReceived(container.getGameInfo());
                break;
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

    public void validateIncomingMessage(byte[] message) {
        if (decodeMessage(message) == null) {
            Log.d(TAG, "Decode Message produced null, something went wrong");
        } else {
            handleMessage(decodeMessage(message));
            Log.d(TAG, "called handleMessage");
        }
    }
}
