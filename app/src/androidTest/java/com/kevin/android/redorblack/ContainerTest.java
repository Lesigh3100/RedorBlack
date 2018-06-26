package com.kevin.android.redorblack;

import android.support.test.runner.AndroidJUnit4;

import com.kevin.android.redorblack.messagereceiver.MessageReceiver;
import com.kevin.android.redorblack.messagereceiver.MessagedReceivedListener;
import com.kevin.android.redorblack.dataclasses.GameContainer;
import com.kevin.android.redorblack.dataclasses.GameInfo;
import com.kevin.android.redorblack.dataclasses.GameVariables;
import com.kevin.android.redorblack.utility.GameContainerConverter;
import com.kevin.android.redorblack.utility.Serializer;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_COLOR;
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
        GameContainer gameContainer = new GameContainer(CHANGED_COLOR, RED);

        assertEquals(CHANGED_COLOR, gameContainer.getSendingCode());
        // up to here we're good
        MessageReceiver messageReceiver = new MessageReceiver(messagedReceivedListener);


        messageReceiver.validateIncomingMessage(sendGameContainer(gameInfo, CHANGED_COLOR));
    }




    @Test
    public void testSendGameContainer(){
            GameVariables gameVariables = new GameVariables();
            gameVariables.setPlayerOne(true);
            GameInfo gameInfo = new GameInfo("bob","big" , 11122);
            int sendingCode = CHANGED_COLOR;


            GameContainer gameContainer = GameContainerConverter.createContainerToSendRoom(gameInfo, sendingCode);

        assertEquals(CHANGED_COLOR, gameContainer.getSendingCode());

        byte[] bytes = sendGameContainer(gameInfo, sendingCode);
        assertNotNull(bytes);
    }



    public byte[] sendGameContainer(GameInfo gameInfo, int changedCode){
        GameContainer container = createContainerToSendRoom(gameInfo, changedCode);
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
        public void onWhoPicksFirstReceived(int iPickFirst) {

        }

        @Override
        public void onGameInfoReceived(GameInfo gameInfo) {
            assertEquals("room1", gameInfo.getRoomId());
        }

        @Override
        public void onOpponentWantsToContinueReceived(int paying) {

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
        byte[] bytes = sendGameContainer(gameInfo, sendingCode);

        assertNotNull(decodeMessage(bytes));
        GameContainer gameContainer = decodeMessage(bytes);
        assertNotNull(gameContainer);
        assertEquals(RED, gameContainer.getChangedCode());

    }

}
