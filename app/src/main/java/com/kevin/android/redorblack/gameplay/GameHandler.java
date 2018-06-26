package com.kevin.android.redorblack.gameplay;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.kevin.android.redorblack.R;
import com.kevin.android.redorblack.dataclasses.GameVariables;
import com.kevin.android.redorblack.uicontrols.ViewController;

import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_COLOR;
import static com.kevin.android.redorblack.constants.GameConstants.CHANGED_WHO_GOES_FIRST;
import static com.kevin.android.redorblack.constants.GameConstants.GAME_ROUND_DURATION;
import static com.kevin.android.redorblack.constants.GameConstants.I_AM_PAYING;
import static com.kevin.android.redorblack.constants.GameConstants.NO_CHOICE_RECEIVED;
import static com.kevin.android.redorblack.constants.GameConstants.OPPONENT_PAYING;
import static com.kevin.android.redorblack.constants.GameConstants.PLAYER_ONE_FIRST;
import static com.kevin.android.redorblack.constants.GameConstants.PLAYER_TWO_FIRST;
import static com.kevin.android.redorblack.constants.GameConstants.RED;
import static com.kevin.android.redorblack.dataclasses.GameVariables.getShouldContinueGame;
import static com.kevin.android.redorblack.utility.Broadcaster.getRandomChoice;
import static com.kevin.android.redorblack.utility.Broadcaster.sendGameContainer;

public class GameHandler implements GameCommunicator {
private final String TAG = "Gamehandler";
private GameVariables gameVariables;
private ViewController viewController;
private GameListener gameListener;
private Context context;
private boolean gameplayHasFinished;

    public GameHandler(ViewController viewController, GameListener gameListener, Context context) {
        this.viewController = viewController;
        this.gameListener = gameListener;
        this.context = context;
        if (gameVariables == null){
            gameVariables = new GameVariables();
        }
    }

    public void resetGamePlay(){
        gameplayHasFinished = false;
        if (gameVariables == null){
            gameVariables = new GameVariables();
        } else {
            gameVariables.resetGameVariables();
        }
    }

    // Start the gameplay phase of the game.
    public void startGame(boolean playerIsGameController) {
        gameVariables.setGamestate(GameVariables.Gamestate.FirstPlayerPicking);
        viewController.onRecoverStopCallButtonAndTimeCountDownClock();
        // use token here
        gameListener.onGameStarted();

        Log.d(TAG, "START GAME CALLED");
        if (playerIsGameController) {
            int iPickColor = Math.random() >= .5 ? PLAYER_ONE_FIRST : PLAYER_TWO_FIRST;
            gameVariables.setFirstToPick(iPickColor);
            if (gameVariables.getFirstToPick() == PLAYER_ONE_FIRST) {
                Log.d(TAG, "I PICK COLOR");
                //      broadcastMessage(broadcastWhoPicks(iPickColor));

                viewController.onMakeToast(context.getString(R.string.i_pick_first));
                viewController.onRecoverButtons();
            } else {
                Log.d(TAG, "I DO NOT PICK COLOR");
                //       broadcastMessage(broadcastWhoPicks(!iPickColor));
                viewController.onMakeToast(context.getString(R.string.they_pick_first));
                viewController.onDismissButtons();
            }
            Log.d(TAG, "PLAYER ONE PICKS FIRST = " + Boolean.toString(gameVariables.getFirstToPick() == PLAYER_ONE_FIRST));
            Log.d(TAG, "PLAYER TWO PICKS FIRST = " + Boolean.toString(gameVariables.getFirstToPick() == PLAYER_TWO_FIRST));
            gameListener.onBroadCastMessage(sendGameContainer(CHANGED_WHO_GOES_FIRST, gameVariables.getFirstToPick()));
        }
        Log.d(TAG, "PLAYER ONE SHOULD PICK FIRST = " + Boolean.toString(gameVariables.shouldPickColorFirst()));
        Log.d(TAG, "PLAYER TWO SHOULD PICK FIRST = " + Boolean.toString(gameVariables.shouldPickColorSecond()));
        gameVariables.setSecondsLeft(GAME_ROUND_DURATION);
        startGameHandler();
    }

    // run the gameTick() method every second to update the game.
    private void startGameHandler() {
        Log.d(TAG, "START GAME HANDLER CALLED");
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameplayHasFinished) {
                    Log.d(TAG, "gamePlayFinished coming up");
                    endGame((gameVariables.getMyChoice() != NO_CHOICE_RECEIVED) && (gameVariables.getOpponentChoice() != NO_CHOICE_RECEIVED));
                    return;
                }
                if (gameVariables.getSecondsLeft() <= 0) {
                    switch (gameVariables.getGamestate()) {
                        case FirstPlayerPicking:
                            gameVariables.setGamestate(GameVariables.Gamestate.PlayerGuessing);
                            Log.d(TAG, "GAMESTATE CURRENTLY = " + gameVariables.getGamestate().name());
                            gameVariables.setSecondsLeft(GAME_ROUND_DURATION);
                            // times up, first picker gets a random color
                            if (gameVariables.shouldPickColorFirst() && gameVariables.getMyChoice() == NO_CHOICE_RECEIVED) {
                                gameVariables.setMyChoice(getRandomChoice());
                                if (gameVariables.getMyChoice() == RED){
                                    viewController.onPushedRedButton();
                                } else {
                                    viewController.onPushedBlackButton();
                                }
                                gameListener.onBroadCastMessage(sendGameContainer(CHANGED_COLOR, gameVariables.getMyChoice()));
                            } else if (gameVariables.shouldPickColorSecond()){
                                viewController.onRecoverButtons();
                            }
                            break;
                        case PlayerGuessing:
                            gameVariables.setGamestate(GameVariables.Gamestate.GameEnding);
                            Log.d(TAG, "GAMESTATE CURRENTLY = " + gameVariables.getGamestate().name());
                            gameVariables.setSecondsLeft(GAME_ROUND_DURATION);
                            viewController.onRecoverContinueCallButton(context.getResources().getString(R.string.continue_call));
                            // time up, second picker gets a random color
                            if (gameVariables.shouldPickColorSecond() && gameVariables.getMyChoice() == NO_CHOICE_RECEIVED) {
                                gameVariables.setMyChoice(getRandomChoice());
                                if (gameVariables.getMyChoice() == RED){
                                    viewController.onPushedRedButton();
                                } else {
                                    viewController.onPushedBlackButton();
                                }
                                gameListener.onBroadCastMessage(sendGameContainer(CHANGED_COLOR, gameVariables.getMyChoice()));
                            }
                            break;
                        case GameEnding:
                            endGameAnimation();
                            // continue game, users want to talk more
                            if (getShouldContinueGame(gameVariables)) {

                                if (gameVariables.getPayingForGameContinue() == I_AM_PAYING) {
                                    gameListener.onSubtractPaidTicket();
                                }
                                // reset flags here in case they want to continue the call again
                                gameVariables.setGameContinued(gameVariables);
                                gameListener.onGameContinued();
                                viewController.onRecoverContinueCallButton(context.getResources().getString(R.string.continue_call));
                                break;
                            } else {
                                endGame(false);
                                return;
                            }
                        case GameContinued:
                            if (getShouldContinueGame(gameVariables)) {
                                if (gameVariables.getPayingForGameContinue() == I_AM_PAYING) {
                                    gameListener.onSubtractPaidTicket();
                                }
                                // reset flags here in case they want to continue the call again
                                gameListener.onGameContinued();
                                gameVariables.setGameContinued(gameVariables);
                                viewController.onRecoverContinueCallButton(context.getResources().getString(R.string.continue_call));
                                break;
                            } else {
                                endGame(false);
                                return;
                            }
                        default:
                            endGame(false);
                            return;
                    }
                }
                gameTick();
                h.postDelayed(this, 1000);
            }
        }, 1000);
    }

    // Game tick -- update countdown, check if game ended.
    private void gameTick() {
        // media player problem here, gets created & released too fast for quick sounds
        if (gameVariables.shouldPickColorFirst() && gameVariables.getGamestate() == GameVariables.Gamestate.FirstPlayerPicking && gameVariables.getSecondsLeft() == 1){
            gameListener.onPlaySoundEffect(R.raw.time_up);
        } else if (!gameVariables.shouldPickColorSecond() && gameVariables.getGamestate() == GameVariables.Gamestate.PlayerGuessing && gameVariables.getSecondsLeft() == 1){
            gameListener.onPlaySoundEffect(R.raw.time_up);
        }

        if (gameVariables.getSecondsLeft() > 0) {
            gameVariables.setSecondsLeft(gameVariables.getSecondsLeft() - 1);
        }
        // update countdown
        viewController.onUpdateCountDownClock(gameVariables.getSecondsLeft());
    }


    // show winner animation & losing animation
    private void endGameAnimation() {
        if (gameVariables.shouldPickColorFirst()) {
            if (gameVariables.getMyChoice() == gameVariables.getOpponentChoice()) {
                // I lose
                Log.d(TAG, "I LOSE END GAME CALLED");
                gameListener.onEndGameSoundEffects(false);
                viewController.onDropEmojis(false);
            } else {
                // I win
                Log.d(TAG, "I WIN END GAME CALLED");
                gameListener.onEndGameSoundEffects(true);
                viewController.onDropEmojis(true);
            }
        } else {
            if (gameVariables.getMyChoice() == gameVariables.getOpponentChoice()) {
                // I win
                Log.d(TAG, "I WIN END GAME CALLED");
                gameListener.onEndGameSoundEffects(true);
                viewController.onDropEmojis(true);
            } else {
                // I lose
                Log.d(TAG, "I LOSE END GAME CALLED");
                gameListener.onEndGameSoundEffects(false);
                viewController.onDropEmojis(false);
            }
        }
    }

    // finishes the game
    private void endGame(boolean runAnimation) {
        if (runAnimation){
            endGameAnimation();
        }
        gameListener.onGameEnded(false);
        gameVariables.setGamestate(GameVariables.Gamestate.NotPlaying);
        viewController.onSwitchToMainScreen(true);
    }


    public GameVariables getGameVariables(){
        return gameVariables;
    }

    @Override
    public void onSetMyChoice(int color) {
        gameVariables.setMyChoice(color);
    }

    @Override
    public void onSetOtherPlayerChoice(int color) {
        gameVariables.setOpponentChoice(color);
    }

    @Override
    public void onGameFinished() {
        gameplayHasFinished = true;
    }

    @Override
    public void onSetIWantToContinue(int paying) {
        if (paying == I_AM_PAYING){
            gameVariables.setPayingForGameContinue(I_AM_PAYING);
        }
        gameVariables.setiWantToContinue(true);
    }

    @Override
    public void onSetOpponentWantsToContinueReceived(int paying) {
        gameVariables.setOpponentWantsToContinue(true);
        if (paying == I_AM_PAYING){
            gameVariables.setPayingForGameContinue(OPPONENT_PAYING);
        }
        Log.d(TAG, "opponent wants to continue = " + Boolean.toString(gameVariables.isOpponentWantsToContinue()));
        Log.d(TAG, "I WANT TO CONTINUE = " + Boolean.toString(gameVariables.isiWantToContinue()));
    }

    @Override
    public void onSetWhoPicksFirst(int whoPicksFirst) {
        gameVariables.setFirstToPick(whoPicksFirst);
    }

    @Override
    public void onSetIsPlayerOne(boolean playerOne) {
        gameVariables.setPlayerOne(playerOne);
    }

}
