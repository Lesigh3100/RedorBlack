package com.kevin.android.redorblack.utility;


import android.support.annotation.Keep;

import com.kevin.android.redorblack.dataclasses.GameContainer;
import com.kevin.android.redorblack.dataclasses.GameInfo;
import com.kevin.android.redorblack.dataclasses.GameVariables;
@Keep
public class GameContainerConverter {


    public static GameContainer createContainerToSend(GameVariables gameVariables, int sendingCode){
        GameContainer gameContainer = new GameContainer();
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


    public static GameContainer createContainerToSendRoom(GameVariables gameVariables, GameInfo gameInfo, int sendingCode){
        GameContainer gameContainer = new GameContainer(gameInfo);
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





}
