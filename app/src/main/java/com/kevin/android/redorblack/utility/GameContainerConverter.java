package com.kevin.android.redorblack.utility;


import android.support.annotation.Keep;

import com.kevin.android.redorblack.dataclasses.GameContainer;
import com.kevin.android.redorblack.dataclasses.GameInfo;

@Keep
public class GameContainerConverter {


    public static GameContainer createContainerToSend(int sendingCode, int changedCode){
        return new GameContainer(sendingCode, changedCode);
    }

    public static GameContainer createContainerToSendRoom(GameInfo gameInfo, int sendingCode){
        return new GameContainer(gameInfo, sendingCode);
    }




    // previous gamecontainer
/*
 public static GameContainer createContainerToSend(GameVariables gameVariables, int sendingCode){
        GameContainer gameContainer = new GameContainer(sendingCode);
        gameContainer.setSendingCode(sendingCode);

        if (gameVariables.isPlayerOne){
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
        return gameContainer;
    }
    */


}
