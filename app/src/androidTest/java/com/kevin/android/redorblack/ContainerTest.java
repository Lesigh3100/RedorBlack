package com.kevin.android.redorblack;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.kevin.android.redorblack.GameLogic.MessageReceiver;
import com.kevin.android.redorblack.GameLogic.MessagedReceivedListener;
import com.kevin.android.redorblack.dataclasses.GameContainer;
import com.kevin.android.redorblack.dataclasses.GameInfo;
import com.kevin.android.redorblack.dataclasses.GameVariables;
import com.kevin.android.redorblack.utility.GameContainerConverter;
import com.kevin.android.redorblack.utility.Serializer;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_COLOR;
import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_GAME_INFO;
import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_IWANTTOCONTINUE;
import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_WHO_GOES_FIRST;
import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_WHO_IS_PAYING;
import static com.kevin.android.redorblack.constants.GameConstants.I_AM_PAYING;
import static com.kevin.android.redorblack.constants.GameConstants.NO_ONE_PAYING;
import static com.kevin.android.redorblack.constants.GameConstants.OPPONENT_PAYING;
import static com.kevin.android.redorblack.constants.GameConstants.RED;
import static com.kevin.android.redorblack.utility.GameContainerConverter.createContainerToSendRoom;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ContainerTest {

    @Test
    public void updateContainerToSend(){
    GameVariables gameVariables = new GameVariables();
    GameInfo gameInfo = new GameInfo("room1","big" , 11122);
        gameVariables.setPlayerOne(true);
        gameVariables.setMyChoice(RED);
     //   gameVariables.setFirstToPick(true);
     //   gameVariables.setiWantToContinue(true);
      //  gameVariables.setPayingForGameContinue(OPPONENT_PAYING);
     //   GameContainer gameContainer = new GameContainer(gameInfo);
        GameContainer gameContainer = new GameContainer();
        gameContainer.setSendingCode(CHANGED_COLOR);
        if (gameVariables.isPlayerOne()){
            gameContainer.getPlayer1().setMyChoice(gameVariables.getMyChoice());
            gameContainer.getPlayer1().setPayingForGameContinue(gameVariables.getPayingForGameContinue());
            gameContainer.getPlayer1().setFirstToPick(gameVariables.isFirstToPick());
            gameContainer.getPlayer1().setiWantToContinue(gameVariables.isiWantToContinue());
        } else {
            gameContainer.getPlayer2().setMyChoice(gameVariables.getMyChoice());
            gameContainer.getPlayer2().setPayingForGameContinue(gameVariables.getPayingForGameContinue());
            gameContainer.getPlayer2().setFirstToPick(gameVariables.isFirstToPick());
            gameContainer.getPlayer2().setiWantToContinue(gameVariables.isiWantToContinue());
        }

        assertEquals(CHANGED_COLOR, gameContainer.getSendingCode());
        assertNotNull(gameContainer.getPlayer1());
        // up to here we're good
        MessageReceiver messageReceiver = new MessageReceiver(messagedReceivedListener);



        messageReceiver.incomingMessage(sendGameContainer(gameVariables, gameInfo, CHANGED_COLOR));
       // messageReceiver.incomingMessage(sendGameContainer(gameVariables, gameInfo, CHANGED_WHO_GOES_FIRST));
      //  messageReceiver.incomingMessage(sendGameContainer(gameVariables, gameInfo, CHANGED_IWANTTOCONTINUE));
      //  messageReceiver.incomingMessage(sendGameContainer(gameVariables, gameInfo, CHANGED_WHO_IS_PAYING));
      //  messageReceiver.incomingMessage(sendGameContainer(gameVariables, gameInfo, CHANGED_GAME_INFO));

    }




    @Test
    public void testSendGameContainer(){
            GameVariables gameVariables = new GameVariables();
            gameVariables.setPlayerOne(true);
            GameInfo gameInfo = new GameInfo("bob","big" , 11122);
            int sendingCode = CHANGED_COLOR;


            GameContainer gameContainer = GameContainerConverter.createContainerToSendRoom(gameVariables, gameInfo, sendingCode);

        assertEquals(CHANGED_COLOR, gameContainer.getSendingCode());
        assertNotNull(gameContainer.getPlayer1());

        byte[] bytes = sendGameContainer(gameVariables, gameInfo, sendingCode);
        assertNotNull(bytes);
    }



    public byte[] sendGameContainer(GameVariables gameVariables, GameInfo gameInfo, int changedCode){
        GameContainer container = createContainerToSendRoom(gameVariables, gameInfo, changedCode);
        try {
            return Serializer.convertToBytes(container);
        } catch (IOException io) {
        }
        return null;
    }


    private MessagedReceivedListener messagedReceivedListener = new MessagedReceivedListener() {
        @Override
        public void onColorReceived(int color) {
            assertEquals(RED , color);
        }

        @Override
        public void onWhoPicksFirstReceived(boolean iPickFirst) {
           assertTrue(iPickFirst);
        }
        @Override
        public void onOpponentWantsToContinueReceived(boolean opponentWantsToContinue) {
            assertTrue(opponentWantsToContinue);
        }

        @Override
        public void onGameInfoReceived(GameInfo gameInfo) {
            assertEquals("room1", gameInfo.getRoomId());
        }

        @Override
        public void onWhoIsPayingReceived(int paying) {
            assertEquals(OPPONENT_PAYING, paying);
        }
    };


    private GameContainer decodeMessage(byte[] message) {
        try {
            return (GameContainer) Serializer.convertFromBytes(message);
        } catch (IOException | ClassCastException | ClassNotFoundException exception) {
        }
        return null;
    }

    @Test
    public void incomingMessage() {
        GameVariables gameVariables = new GameVariables();
        gameVariables.setPlayerOne(true);
        GameInfo gameInfo = new GameInfo("bob","big" , 11122);
        int sendingCode = CHANGED_COLOR;
        gameVariables.setMyChoice(RED);
        byte[] bytes = sendGameContainer(gameVariables, gameInfo, sendingCode);

        assertNotNull(decodeMessage(bytes));
        GameContainer gameContainer = decodeMessage(bytes);
        assertNotNull(gameContainer);
        assertEquals(RED, gameContainer.getPlayer1().getMyChoice());

    }

}
