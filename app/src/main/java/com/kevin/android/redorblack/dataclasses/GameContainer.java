package com.kevin.android.redorblack.dataclasses;

import android.support.annotation.Keep;

import java.io.Serializable;

import static com.kevin.android.redorblack.constants.GameConstants.NO_CHOICE_RECEIVED;
import static com.kevin.android.redorblack.constants.GameConstants.NO_ONE_PAYING;
@Keep
public class GameContainer implements Serializable {

    Player player1;
    Player player2;
    GameInfo gameInfo;
    int sendingCode;

    public GameContainer(GameInfo gameInfo) {
        this.player1 = new Player();
        this.player2 = new Player();
        this.gameInfo = gameInfo;
    }

    public GameContainer() {
        this.player1 = new Player();
        this.player2 = new Player();
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public int getSendingCode() {
        return sendingCode;
    }

    public void setSendingCode(int sendingCode) {
        this.sendingCode = sendingCode;
    }

    public class Player implements Serializable {
        private boolean iWantToContinue;
        private int myChoice;
        private boolean firstToPick;
        private int payingForGameContinue;


        private Player() {
            this.iWantToContinue = false;
            this.myChoice = NO_CHOICE_RECEIVED;
            this.firstToPick = false;
            this.payingForGameContinue = NO_ONE_PAYING;
        }

        public boolean isiWantToContinue() {
            return iWantToContinue;
        }

        public void setiWantToContinue(boolean iWantToContinue) {
            this.iWantToContinue = iWantToContinue;
        }

        public int getMyChoice() {
            return myChoice;
        }

        public void setMyChoice(int myChoice) {
            this.myChoice = myChoice;
        }

        public boolean isFirstToPick() {
            return firstToPick;
        }

        public void setFirstToPick(boolean firstToPick) {
            this.firstToPick = firstToPick;
        }

        public int getPayingForGameContinue() {
            return payingForGameContinue;
        }

        public void setPayingForGameContinue(int payingForGameContinue) {
            this.payingForGameContinue = payingForGameContinue;
        }
    }
}
