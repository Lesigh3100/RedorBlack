package com.kevin.android.redorblack.dataclasses;

import android.support.annotation.Keep;
import android.util.Log;

import com.twilio.video.Room;

import static com.kevin.android.redorblack.constants.GameConstants.GAME_CONTINUED_DURATION;
import static com.kevin.android.redorblack.constants.GameConstants.NO_CHOICE_RECEIVED;
import static com.kevin.android.redorblack.constants.GameConstants.NO_ONE_PAYING;


// class to hold all game variables
@Keep
public class GameVariables {
    // game tokens
    private int tokenBeingUsed;
    private int tokensBeingAdded;

    private String lastGameNumber;
    private String reportedId;

    private String roomToJoin;
    public com.twilio.video.Room twilioRoom;

    // flag if user has played a match since opening the app
    private boolean gameEndedInDisconnect;
    private boolean continueMessageSent;
    private boolean continueMessageReceived;

    private int payingForGameContinue;

    // Game Variables
    private int secondsLeft; // how long until the game ends (seconds)
    private boolean iWantToContinue;
    private boolean opponentWantsToContinue;
    private int myChoice;
    private int opponentChoice;
    private boolean firstToPick;
    private boolean playerDisconnected;

    public boolean isPlayerOne;

    public enum Gamestate {
        NotPlaying,
        GameConnecting,
        FirstPlayerPicking,
        PlayerGuessing,
        GameEnding,
        GameContinued
    }

    Gamestate gamestate;

    public Gamestate getGamestate() {
        return gamestate;
    }

    public void setGamestate(Gamestate gamestate) {
        this.gamestate = gamestate;
    }

    // constructor that when called creates a new game
    public GameVariables() {
        gamestate = Gamestate.NotPlaying;
        tokenBeingUsed = NO_CHOICE_RECEIVED;
        tokensBeingAdded = 0;
        lastGameNumber = "";
        reportedId = "";
        gameEndedInDisconnect = false;
        continueMessageSent = false;
        continueMessageReceived = false;
        payingForGameContinue = NO_ONE_PAYING;
        secondsLeft = -1;
        iWantToContinue = false;
        opponentWantsToContinue = false;
        myChoice = NO_CHOICE_RECEIVED;
        opponentChoice = NO_CHOICE_RECEIVED;
        firstToPick = false;
        playerDisconnected = false;
        roomToJoin = "";
        twilioRoom = null;
        isPlayerOne = false;
    }

    public void resetGameVariables(boolean startingNewGame) {
        gamestate = Gamestate.NotPlaying;
        tokenBeingUsed = NO_CHOICE_RECEIVED;
        tokensBeingAdded = 0;
        reportedId = "";
        continueMessageSent = false;
        continueMessageReceived = false;
        payingForGameContinue = NO_ONE_PAYING;
        secondsLeft = -1;
        iWantToContinue = false;
        opponentWantsToContinue = false;
        myChoice = NO_CHOICE_RECEIVED;
        opponentChoice = NO_CHOICE_RECEIVED;
        firstToPick = false;
        playerDisconnected = false;
        roomToJoin = "";
        twilioRoom = null;
        isPlayerOne = false;
        if (startingNewGame) {
            gameEndedInDisconnect = false;
            lastGameNumber = "";
        }


    }

    public static boolean getShouldContinueGame(GameVariables gameVariables) {
        return gameVariables.isiWantToContinue() && gameVariables.isOpponentWantsToContinue();
    }


    // make this return a gamevariables
    public void setGameContinued(GameVariables gameVariables) {
        gameVariables.setPayingForGameContinue(NO_ONE_PAYING);
        gameVariables.setiWantToContinue(false);
        gameVariables.setContinueMessageReceived(false);
        gameVariables.setOpponentWantsToContinue(false);
        gameVariables.setGamestate(Gamestate.GameContinued);
        gameVariables.setSecondsLeft(GAME_CONTINUED_DURATION);
    }

    public boolean isPlayerOne() {
        return isPlayerOne;
    }

    public void setPlayerOne(boolean playerOne) {
        isPlayerOne = playerOne;
    }

    public Room getTwilioRoom() {
        return twilioRoom;
    }

    public void setTwilioRoom(Room twilioRoom) {
        this.twilioRoom = twilioRoom;
    }

    public String getRoomToJoin() {
        return roomToJoin;
    }

    public void setRoomToJoin(String roomToJoin) {
        this.roomToJoin = roomToJoin;
    }

    public int getTokenBeingUsed() {
        return tokenBeingUsed;
    }

    public void setTokenBeingUsed(int tokenBeingUsed) {
        this.tokenBeingUsed = tokenBeingUsed;
    }

    public int getTokensBeingAdded() {
        return tokensBeingAdded;
    }

    public void setTokensBeingAdded(int tokensBeingAdded) {
        this.tokensBeingAdded = tokensBeingAdded;
    }

    public String getLastGameNumber() {
        return lastGameNumber;
    }

    public void setLastGameNumber(String lastGameNumber) {
        this.lastGameNumber = lastGameNumber;
    }

    public String getReportedId() {
        return reportedId;
    }

    public void setReportedId(String reportedId) {
        this.reportedId = reportedId;
    }

    public boolean isGameEndedInDisconnect() {
        return gameEndedInDisconnect;
    }

    public void setGameEndedInDisconnect(boolean gameEndedInDisconnect) {
        this.gameEndedInDisconnect = gameEndedInDisconnect;
    }

    public boolean isContinueMessageSent() {
        return continueMessageSent;
    }

    public void setContinueMessageSent(boolean continueMessageSent) {
        this.continueMessageSent = continueMessageSent;
    }

    public boolean isContinueMessageReceived() {
        return continueMessageReceived;
    }

    public void setContinueMessageReceived(boolean continueMessageReceived) {
        this.continueMessageReceived = continueMessageReceived;
    }

    public int getPayingForGameContinue() {
        return payingForGameContinue;
    }

    public void setPayingForGameContinue(int payingForGameContinue) {
        this.payingForGameContinue = payingForGameContinue;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public boolean isiWantToContinue() {
        return iWantToContinue;
    }

    public void setiWantToContinue(boolean iWantToContinue) {
        this.iWantToContinue = iWantToContinue;
    }

    public boolean isOpponentWantsToContinue() {
        return opponentWantsToContinue;
    }

    public void setOpponentWantsToContinue(boolean opponentWantsToContinue) {
        this.opponentWantsToContinue = opponentWantsToContinue;
    }

    public int getMyChoice() {
        return myChoice;
    }

    public void setMyChoice(int myChoice) {
        this.myChoice = myChoice;
    }

    public int getOpponentChoice() {
        return opponentChoice;
    }

    public void setOpponentChoice(int opponentChoice) {
        this.opponentChoice = opponentChoice;
    }

    public boolean isFirstToPick() {
        return firstToPick;
    }

    public void setFirstToPick(boolean firstToPick) {
        this.firstToPick = firstToPick;
    }

    public boolean isPlayerDisconnected() {
        return playerDisconnected;
    }

    public void setPlayerDisconnected(boolean playerDisconnected) {
        this.playerDisconnected = playerDisconnected;
    }
}
