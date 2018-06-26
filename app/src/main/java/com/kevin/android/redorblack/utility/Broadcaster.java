package com.kevin.android.redorblack.utility;

import android.support.annotation.Keep;
import android.util.Log;

import com.kevin.android.redorblack.dataclasses.GameContainer;
import com.kevin.android.redorblack.dataclasses.GameInfo;
import com.kevin.android.redorblack.dataclasses.GameVariables;

import java.io.IOException;

import static com.kevin.android.redorblack.constants.GameConstants.BLACK;
import static com.kevin.android.redorblack.constants.GameConstants.RED;
import static com.kevin.android.redorblack.utility.GameContainerConverter.createContainerToSend;
import static com.kevin.android.redorblack.utility.GameContainerConverter.createContainerToSendRoom;

@Keep
public class Broadcaster {

    // if time runs out and the player hasn't picked, make a random choice and broadcast it
    public static int getRandomChoice() {
            return Math.random() >= .5 ? RED : BLACK;
    }

        // returns true if byte[0] isn't an empty string
        private static boolean isValidMessage(byte[] message){
        try{
            GameContainer gameContainer = (GameContainer) Serializer.convertFromBytes(message);
            if (gameContainer != null){
                Log.d("Broadcaster", "isValidMessage! Sucess!");
                return true;
            }
        } catch ( ClassNotFoundException | ClassCastException | IOException exception){
            Log.d("isValidMessage = false" , "exception = " + exception);
        }
      return false;
        }


    public static byte[] sendGameContainer(int changedCode, int changedInformation){
      GameContainer container = createContainerToSend(changedCode, changedInformation);
        try {
            byte[] bytes = Serializer.convertToBytes(container);
            if (isValidMessage(bytes)){
                return bytes;
            }
        } catch (IOException io) {
            Log.v("Broadcaster", "IO EXCEPTION, " + io.toString());
        }
        return null;
    }


    // sends the string of my room ID so that the other person can generate a token for it
    public static byte[] sendMyRoom(String room, String roomFBReference, Object timestamp, int changedCode) {
        GameInfo gameInfo = new GameInfo(room, roomFBReference, timestamp);
        GameContainer container = createContainerToSendRoom(gameInfo, changedCode);
        try {
            byte[] bytes = Serializer.convertToBytes(container);
            if (isValidMessage(bytes)){
                return bytes;
            }
        } catch (IOException io) {
            Log.v("Broadcaster", "IO EXCEPTION, " + io.toString());
        }
        return null;
    }


}
