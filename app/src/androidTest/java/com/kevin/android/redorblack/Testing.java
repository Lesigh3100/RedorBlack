package com.kevin.android.redorblack;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.util.Preconditions;
import android.test.mock.MockContext;

import com.kevin.android.redorblack.dataclasses.GameVariables;
import com.kevin.android.redorblack.utility.Utility;
import com.twilio.video.LocalAudioTrack;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.kevin.android.redorblack.constants.GameConstants.LOCAL_AUDIO_TRACK_NAME;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class Testing {



    private int mSecondsLeft = 1;
    boolean shouldContinueGame = false;
    GameVariables.Gamestate gamestate;
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.kevin.android.redorblack", appContext.getPackageName());
    }

    @Test
    public void testGameStateFlow(){
        gamestate = GameVariables.Gamestate.FirstPlayerPicking;
        Looper.prepare();
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSecondsLeft <= 0) {
                    switch (gamestate) {
                        case FirstPlayerPicking:
                            gamestate = GameVariables.Gamestate.PlayerGuessing;
                            mSecondsLeft = 1;
                            break;
                        case PlayerGuessing:
                            gamestate = GameVariables.Gamestate.GameEnding;
                            mSecondsLeft = 1;
                            break;
                        case GameEnding:
                            // continue game, users want to talk more
                            shouldContinueGame = Math.random() >= .5;
                            if (shouldContinueGame) {
                                /*
                                if (payingForGameContinue == I_AM_PAYING){
                                    tokenUpdater(CODE_SUBTRACT_PAID_TOKEN, CODE_DONT_NEED_TIME_STAMP, CODE_TOKEN_MATH_IRRELEVANT);
                                } */

                                mSecondsLeft = 1;
                                gamestate = GameVariables.Gamestate.GameContinued;
                                break;
                            } else {
                           assertEquals(GameVariables.Gamestate.GameEnding, gamestate);
                                return;
                            }
                        case GameContinued:
                                assertEquals(GameVariables.Gamestate.GameContinued, gamestate);
                                break;
                        default:

                            return;
                    }
                }
                gameTick();
                h.postDelayed(this, 1000);
            }
        }, 1000);

    }
    // Game tick -- update countdown, check if game ended.
    void gameTick() {
        if (mSecondsLeft <= 3) {
            // sound effect
            //todo this might not work well with media player, check

        }
        if (mSecondsLeft > 0) {
            --mSecondsLeft;
        }
    }

    @Test
    public void testObjectToInt(){
        Object l = (long)1341341345;
       int actual = Utility.safeObjectToInt(l);
       int expected = 1341341345;
       assertEquals(expected, actual);

       String number = "1345";
       l = number;
       int expected1 = 0;
       int actual1 = Utility.safeObjectToInt(number);
       assertEquals(expected1, actual1);
    }


    @Test
    public void testAudioTrack(){
LocalAudioTrack localAudioTrack;
        Context appContext = InstrumentationRegistry.getTargetContext();

        localAudioTrack = LocalAudioTrack.create(appContext, true, LOCAL_AUDIO_TRACK_NAME);
        Preconditions.checkNotNull(localAudioTrack, "LocalAudioTrack List must not be null");


    }


}
