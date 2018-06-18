package com.kevin.android.redorblack.constants;

import android.support.annotation.Keep;

@Keep
public class GameConstants {
    public final static int GAME_DURATION = 30;
    public final static int GAME_ROUND_DURATION = 15;
    public final static int GAME_CONTINUED_DURATION = 300;

    public final static int RED = 0;
    public final static int BLACK = 1;
    public final static int NO_CHOICE_RECEIVED = -1;
    public final static int NEW_USER_FREE_STARTING_TOKENS = 10;
    public final static int USER_MAX_FREE_TOKENS = 5;
    public final static int FREE_TOKEN_REFRESH_TIME_SECONDS = 180;
    public final static int FREE_TOKEN_REFRESH_TIME_MILLIS = FREE_TOKEN_REFRESH_TIME_SECONDS * 1000;
    public final static int FREE_TOKEN = 9;
    public final static int PAID_TOKEN = 8;
    public final static int NEED_TO_PURCHASE_TOKENS = 11;
    public final static int I_AM_PAYING = 12;
    public final static int OPPONENT_PAYING = 13;
    public final static int NO_ONE_PAYING = -11;


    // char codes for broadcast

    public final static int CHANGED_COLOR = 51;
    public final static int CHANGED_IWANTTOCONTINUE = 52;
    public final static int CHANGED_WHO_IS_PAYING = 53;
    public final static int CHANGED_WHO_GOES_FIRST = 54;
    public final static int CHANGED_GAME_INFO = 55;

    public final static int GAME_CONTINUE_CODE = 3;
    public final static int GAME_DO_NOT_CONTINUE_CODE = 5;
    public final static char COLOR_CHOICE = 'C';
    public final static char NO_CHOICE = 'N';
    public final static char RED_CHOSEN = 'R';
    public final static char BLACK_CHOSEN = 'B';
    public final static char PICKING = 'I';
    public final static char I_AM_PICKING = 'M';
    public final static char YOU_ARE_PICKING = 'U';
    public final static char CONTINUE_THE_GAME = 'P';


    // code for exception
    public final static int ILLEGAL_CAST_EXCEPTION = -1;

    // game codes for tokens
    public final static int CODE_ADD_FREE_TOKENS = 20;
    public final static int CODE_SUBTRACT_FREE_TOKEN = 21;
    public final static int CODE_SUBTRACT_PAID_TOKEN = 22;
    public final static int CODE_CLOCK_TIME_RAN_OUT = 23;
    public final static int CODE_SET_NEW_TIME_STAMP = 24;
    public final static int CODE_RESTART_CLOCK = 25;
    public final static int CODE_CHECK_IF_WE_NEED_TO_START_OR_CONTINUE_TIMER = 26;
    public final static int CODE_TOKEN_ADDED = 33;
    public final static int CODE_TOKEN_SUBTRACTED = 34;
    public final static int CODE_TOKEN_MATH_IRRELEVANT = 35;

    public final static long CODE_DONT_NEED_TIME_STAMP = 27;
    public final static long CODE_NEED_TIME_STAMP = 28;

    // twilio
    public static final String LOCAL_AUDIO_TRACK_NAME = "mic";
    public static final String LOCAL_VIDEO_TRACK_NAME = "camera";

    // google codes
    // Request codes for the UIs that we show with startActivityForResult:
    public final static int RC_SELECT_PLAYERS = 10000;
    public final static int RC_INVITATION_INBOX = 10001;
    public final static int RC_WAITING_ROOM = 10002;
    // Request code used to invoke sign in user interactions.
    public static final int RC_SIGN_IN = 9001;
}
