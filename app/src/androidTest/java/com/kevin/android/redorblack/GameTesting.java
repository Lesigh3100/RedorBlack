package com.kevin.android.redorblack;


import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.kevin.android.redorblack.messagereceiver.MessageReceiver;
import com.kevin.android.redorblack.messagereceiver.MessagedReceivedListener;
import com.kevin.android.redorblack.dataclasses.GameContainer;
import com.kevin.android.redorblack.dataclasses.GameInfo;
import com.kevin.android.redorblack.dataclasses.GameVariables;
import com.kevin.android.redorblack.utility.Serializer;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_COLOR;
import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_GAME_INFO;
import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_I_WANT_TO_CONTINUE;
import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_WHO_GOES_FIRST;
import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_WHO_IS_PAYING;
import static com.kevin.android.redorblack.constants.GameConstants.I_AM_PAYING;
import static com.kevin.android.redorblack.constants.GameConstants.NO_CHOICE_RECEIVED;
import static com.kevin.android.redorblack.constants.GameConstants.OPPONENT_PAYING;
import static com.kevin.android.redorblack.constants.GameConstants.RED;
import static com.kevin.android.redorblack.utility.GameContainerConverter.*;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GameTesting {

GameVariables gameVariables;
/*
MessageReceiver messageReceiver;
GameContainer gameContainer;
GameInfo gameInfo;
*/

/*
@Test
 public void testMessages(){
    MessageReceiver messageReceiver; messageReceiver = new MessageReceiver(messagedReceivedListener);
    GameVariables gameVariables = new GameVariables();
    GameInfo gameInfo = new GameInfo("room1", "bigref", 124124);

    gameVariables.setMyChoice(RED);
    gameVariables.setPayingForGameContinue(I_AM_PAYING);
   // gameVariables.setFirstToPick(false);
    gameVariables.setOpponentWantsToContinue(true);

    assertNotNull(sendGameContainer(gameVariables, CHANGED_COLOR));

    messageReceiver.validateIncomingMessage(sendGameContainer(gameVariables, CHANGED_COLOR));
    messageReceiver.validateIncomingMessage(sendGameContainer(gameVariables, CHANGED_I_WANT_TO_CONTINUE));
    messageReceiver.validateIncomingMessage(sendGameContainer(gameVariables, CHANGED_GAME_INFO));
    messageReceiver.validateIncomingMessage(sendGameContainer(gameVariables, CHANGED_WHO_GOES_FIRST));
    messageReceiver.validateIncomingMessage(sendGameContainer(gameVariables, CHANGED_WHO_IS_PAYING));


}


@Test
public void testObjects(){
    MessageReceiver messageReceiver; messageReceiver = new MessageReceiver(messagedReceivedListener);
    gameVariables = new GameVariables();
    GameInfo gameInfo = new GameInfo("room1", "bigref", 124124);
    GameContainer gameContainer = new GameContainer(gameInfo);
    assertNotNull(messageReceiver);
    assertNotNull(gameVariables);
    assertNotNull(gameInfo);
    assertNotNull(gameContainer);
    assertNotNull(gameContainer.getGameInfo());
    assertNotNull(gameContainer.getPlayer1());
    assertNotNull(gameContainer.getPlayer2());
    assertEquals(NO_CHOICE_RECEIVED, gameContainer.getPlayer1().getMyChoice());

    gameContainer.setSendingCode(CHANGED_COLOR);
    assertNotNull(gameContainer);
    byte[] bytes;
    try {
        bytes = Serializer.convertToBytes(gameContainer);
        assertNotNull(bytes);

            assertTrue(isValidMessage(bytes));
            if (isValidMessage(bytes)){
                return bytes;
            }
        gameContainer = (GameContainer) Serializer.convertFromBytes(bytes);
        assertNotNull(gameContainer);
        assertEquals(CHANGED_COLOR, gameContainer.getSendingCode());


    } catch (IOException | ClassNotFoundException io) {
        Log.v("Broadcaster", "IO EXCEPTION, " + io.toString());
        assertNotNull(gameContainer);
        assertEquals(CHANGED_COLOR, gameContainer.getSendingCode());


    }

}



  private MessagedReceivedListener messagedReceivedListener = new MessagedReceivedListener() {
        @Override
        public void onColorReceived(int color) {
            assertNotNull(gameVariables);
            gameVariables.setOpponentChoice(color);
            assertEquals(RED , gameVariables.getOpponentChoice());
        }

        @Override
        public void onWhoPicksFirstReceived(boolean iPickFirst) {
            gameVariables.setFirstToPick(iPickFirst);
            if (iPickFirst){
                // recoverButtons()
            }
        }

      @Override
      public void onOpponentWantsToContinueReceived(int paying) {
                  if (paying == I_AM_PAYING){
                      gameVariables.setPayingForGameContinue(OPPONENT_PAYING);
                  }
          assertEquals(OPPONENT_PAYING, gameVariables.getPayingForGameContinue());
      }

        @Override
        public void onGameInfoReceived(GameInfo gameInfo) {
         //   receiveGameInfoAndJoinRoom(gameInfo);
            assertEquals("room1", gameInfo.getRoomId());
        }
    };

@Test
public void testGameContainer() {
    GameInfo gameInfo = new GameInfo("room1", "bigref", 124124);
GameContainer gameContainer = new GameContainer(gameInfo);
GameVariables gameVariables = new GameVariables();
MessageReceiver messageReceiver = new MessageReceiver(messagedReceivedListener);
assertNotNull(sendGameContainer(gameVariables, CHANGED_COLOR));
assertNotNull(messageReceiver);
assertNotNull(messagedReceivedListener);
messageReceiver.validateIncomingMessage(sendGameContainer(gameVariables, CHANGED_COLOR));

}


    public byte[] sendGameContainer(GameVariables gameVariables, int changedCode){
        GameContainer container;
        container = createContainerToSend(gameVariables, changedCode);
        container.setSendingCode(changedCode);
            try{
            byte[] bytes = Serializer.convertToBytes(container);
            return bytes;
            /*
            assertTrue(isValidMessage(bytes));
            if (isValidMessage(bytes)){
                return bytes;
            }
        } catch (IOException io) {
            Log.v("Broadcaster", "IO EXCEPTION, " + io.toString());
        }
        return null;
    }

    // returns true if byte[0] isn't an empty string
    private boolean isValidMessage(byte[] message){
        try{
            GameContainer gameContainer = (GameContainer) Serializer.convertFromBytes(message);
            if (gameContainer != null){
                return true;
            }
        } catch ( ClassNotFoundException | ClassCastException | IOException exception){
            Log.d("isValidMessage = false" , "exception = " + exception);
        }
        return false;
    }
*/

}
