package com.kevin.android.redorblack;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.annotation.Keep;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.billingclient.api.BillingClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.GamesActivityResultCodes;

import com.google.firebase.database.DatabaseReference;
import com.kevin.android.redorblack.gameplay.GameHandler;
import com.kevin.android.redorblack.gameplay.GameListener;
import com.kevin.android.redorblack.messagereceiver.IncomingMessageListener;
import com.kevin.android.redorblack.messagereceiver.MessageReceiver;
import com.kevin.android.redorblack.messagereceiver.MessagedReceivedListener;
import com.kevin.android.redorblack.billing.AcquireFragment;
import com.kevin.android.redorblack.billing.BillingHelperRedorBlack;
import com.kevin.android.redorblack.billing.BillingManager;
import com.kevin.android.redorblack.billing.BillingProvider;
import com.kevin.android.redorblack.dataclasses.GameInfo;
import com.kevin.android.redorblack.firebasemethods.DataRetrieval;
import com.kevin.android.redorblack.firebasemethods.FireBaseHandler;
import com.kevin.android.redorblack.firebasemethods.FireBaseSetUpListener;
import com.kevin.android.redorblack.signin.ConnectionListener;
import com.kevin.android.redorblack.signin.GoogleGamesHandler;
import com.kevin.android.redorblack.signin.GoogleSignInHandler;
import com.kevin.android.redorblack.twilio.TwilioHandler;
import com.kevin.android.redorblack.twilio.TwilioListener;
import com.kevin.android.redorblack.utility.PermissionChecker;
import com.kevin.android.redorblack.utility.ReportChoiceListener;
import com.kevin.android.redorblack.utility.ServerTime;
import com.kevin.android.redorblack.utility.SoundEffects;
import com.kevin.android.redorblack.uicontrols.ViewController;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;


import java.util.ArrayList;
import java.util.UUID;


import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static com.kevin.android.redorblack.utility.Broadcaster.*;
import static com.kevin.android.redorblack.constants.GameConstants.*;
import static com.kevin.android.redorblack.constants.FirebaseConstants.*;
import static com.kevin.android.redorblack.billing.BillingConstants.DIALOG_TAG;
import static com.kevin.android.redorblack.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED;

@Keep
public class MainActivity extends AppCompatActivity implements BillingProvider {

    // our context
    Context mContext;
    /*
     * API INTEGRATION SECTION. This section contains the code that integrates
     * the game with the Google Play game services API.
     */
    final static String TAG = "RedorBlack";
    // google billing objects
    private BillingManager mBillingManager;
    private BillingHelperRedorBlack mBillingHelperRedorBlack;
    private AcquireFragment acquireFragment;
    // room needs
    String accessToken;
    // Firebase
    FireBaseHandler fireBaseHandler;
    // gets time from server
    ServerTime serverTime;
    // object that controls google signin flow
    GoogleSignInHandler googleSignInHandler;
    // controls google games needs such as sending real time messages
    GoogleGamesHandler googleGamesHandler;
    // our object taht updates most of our views
    ViewController viewController;
    // object that controls twilio
    TwilioHandler twilioHandler;
    // object that controls gameplay
    GameHandler gameHandler;
    // class that handles sound effects
    SoundEffects soundEffects;
    // get specific data from FB
    DataRetrieval dataRetrieval;
    // handles incoming realtime messages
    MessageReceiver messageReceiver;
    // checks permissions
    PermissionChecker permissionChecker;

    private int ticketBeingUsed;
    private String twilioRoomToJoin;
    private String lastGameNumber;
    private boolean freeContinueMessageReceived;
    boolean gameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndInitializeClasses();
        if (!permissionChecker.allPermissionsGranted()) {
            permissionChecker.checkAllPermissions();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop called");
        cleanUpLooseEnds();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        if (twilioHandler != null) {
            twilioHandler.checkIfInRoom();
        }
        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        if (googleSignInHandler != null) {
            googleSignInHandler.signInSilently();
        }
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ON PAUSE CALLED");
    }


    @Override
    public void onBackPressed() {
        viewController.onBackPressedAlertDialogue();
    }

    @Override
    protected void onDestroy() {
        destructionCleanUp();
        super.onDestroy();
    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.play_button:
                    if (googleSignInHandler != null) {
                        if (googleSignInHandler.isSignedIn() && fireBaseHandler.isFireBaseUserNotNull()) {
                            if (!getAudioState()) {
                                viewController.onMakeToast(getResources().getString(R.string.please_turn_up_volume));
                                return;
                            }
                            if (!permissionChecker.allPermissionsGranted()) {
                                permissionChecker.checkAllPermissions();
                                viewController.onMakeToast(getResources().getString(R.string.please_grant_permissions));
                                return;
                            }
                            if (gameHandler != null){
                                gameHandler.resetGamePlay();
                            } else {
                                checkAndInitializeClasses();
                                viewController.onMakeToast(getResources().getString(R.string.error_with_game));
                                return;
                            }

                            if (fireBaseHandler.whatTokenToUse() == NEED_TO_PURCHASE_TOKENS) {
                                onPurchaseButtonClicked();
                                viewController.onMakeToast(getResources().getString(R.string.in_call_billing_not_supported));
                            } else {
                                Log.d(TAG, "STARTING GAME : : : ");
                                resetGameFlowVariables();
                                ticketBeingUsed = fireBaseHandler.whatTokenToUse();
                                viewController.onSwitchToPlayingScreen();
                                twilioHandler.createAudioAndVideoTracks();
                                googleGamesHandler.startQuickGame();
                            }
                        }
                    } else {
                        checkAndInitializeClasses();
                    }
                    break;
                case R.id.red_button:
                    gameHandler.onSetMyChoice(RED);
                    googleGamesHandler.broadcastMessage(sendGameContainer(CHANGED_COLOR, RED));
                    viewController.onPushedRedButton();
                    break;
                case R.id.black_button:
                    gameHandler.onSetMyChoice(BLACK);
                    googleGamesHandler.broadcastMessage(sendGameContainer(CHANGED_COLOR, BLACK));
                    viewController.onPushedBlackButton();
                    break;
                case R.id.sign_in_button:
                    Log.d(TAG, "SIGN IN BUTTON CLICKED");
                    googleSignInHandler.startSignInIntent();
                    break;
                case R.id.stop_call_button:
                    gameHandler.onGameFinished();
                    break;
                case R.id.continue_call_button:
                    // haven't received a message on continuing or not, need to pay to continue game
                    if (!freeContinueMessageReceived && fireBaseHandler.hasPaidTokens()) {
                        viewController.onDismissContinueCallButton();
                        gameHandler.onSetIWantToContinue(I_AM_PAYING);
                        googleGamesHandler.broadcastMessage(sendGameContainer(CHANGED_I_WANT_TO_CONTINUE, I_AM_PAYING));
                    } else if (!gameHandler.getGameVariables().isContinueMessageReceived() && !fireBaseHandler.hasPaidTokens()) {
                        // need to purchase tokens, pause video & go to billing
                        // on success before time limit add tokens and send Iwanttocontinue
                        viewController.onMakeToast(getResources().getString(R.string.in_call_billing_not_supported));
                    } else if (freeContinueMessageReceived) {
                        viewController.onDismissContinueCallButton();
                        gameHandler.onSetIWantToContinue(OPPONENT_PAYING);
                        googleGamesHandler.broadcastMessage(sendGameContainer(CHANGED_I_WANT_TO_CONTINUE, OPPONENT_PAYING));
                    }
                    break;
                case R.id.ticket_paid_count:
                    // start purchase transaction here
                    onPurchaseButtonClicked();
                    break;
                case R.id.report_user_button:
                    viewController.onInflateReportChoices();
                    break;
                case R.id.sign_out_button:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setCancelable(false);
                    builder.setMessage(getResources().getString(R.string.do_you_want_to_sign_out));
                    builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            googleSignInHandler.signOut();
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;

            }
        }
    };

    // if you are assigned the role of picking who goes first you then create the video room name and send it to the other player
    private void configureGame() {
        Log.d(TAG, "CONFIGURE GAME CALLED");
        viewController.checkIfWeHaveCorrectScreen();
        gameController = googleGamesHandler.pickGameController();
        Log.d(TAG, "WE ARE GAME CONTROLLER =" + Boolean.toString(gameController));
        if (gameController) {
            makeVideoRoomTokenAndJoin(true);
            gameHandler.onSetIsPlayerOne(true);
        } else {
            gameHandler.onSetIsPlayerOne(false);
        }
    }

    // if true generates a random room name, sends the room name, then retrieves the access token from the server which then starts video chat upon receiving the token
    private void makeVideoRoomTokenAndJoin(Boolean iMakeRoom) {
        if (iMakeRoom) {
            Log.d(TAG, "I MAKE ROOM CALLED");
            twilioRoomToJoin = UUID.randomUUID().toString();
            logAndSendNewGame(twilioRoomToJoin);
            twilioHandler.retrieveAccessTokenfromServer(fireBaseHandler.getMyFBId(), twilioRoomToJoin);
        } else {
            twilioHandler.retrieveAccessTokenfromServer(fireBaseHandler.getMyFBId(), twilioRoomToJoin);
        }
    }

    // log a new game then send the room to the other player
    private void logAndSendNewGame(final String roomId) {
        serverTime.getTime(new ServerTime.OnTimeRetrievedListener() {
            @Override
            public void onTimeRetrieved(final Long timestamp) {
                lastGameNumber = fireBaseHandler.createNewGameAndLog(timestamp);
                googleGamesHandler.broadcastMessage(sendMyRoom(roomId, lastGameNumber, timestamp, CHANGED_GAME_INFO));
            }
        });
    }

    // gameinfo received from realtimemessagelistener: unpack it, add it to firebase, and join the room
    private void receiveGameInfoAndJoinRoom(final GameInfo gameInfo) {
        fireBaseHandler.receiveAndLogNewGame(gameInfo.getGameReference(), gameInfo.getTime());
        lastGameNumber = gameInfo.getGameReference();
        twilioRoomToJoin = gameInfo.getRoomId();
        makeVideoRoomTokenAndJoin(false);
    }

    // disconnect from google play room and twilio room
    private void disconnectFromPlayRoomAndTwilio() {
        if (twilioHandler != null) {
            twilioHandler.disconnectFromTwilioRoom();
        }
        if (googleGamesHandler != null) {
            googleGamesHandler.leaveRoom();
        }

        accessToken = "";
        safeReleaseAudioVideo();
    }

    // checks if video or audio tracks are null and if not releases them
    public void safeReleaseAudioVideo() {
        Log.d(TAG, "SAFE RELEASE AUDIO VIDEO CALLED ! ! ! ! !");
        if (viewController != null) {
            if (viewController.getLocalVideoTrack() != null) {
                viewController.getLocalVideoTrack().release();
            }
        }
        if (twilioHandler != null) {
            twilioHandler.releaseAudioTrack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "resultcode = " + Integer.toString(resultCode));
        if (requestCode == RC_SIGN_IN) {
            googleSignInHandler.resultOfSignIn(intent);
            if (resultCode == RESULT_OK) {
                //     FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Log.d(TAG, "FIREBASE FAILED RESULT = " + resultCode);
            }
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // we got the result from the "select players" UI -- ready to create the room
            if (googleGamesHandler != null) {
                googleGamesHandler.handleSelectPlayersResult(resultCode, intent);
            }
        } else if (requestCode == RC_INVITATION_INBOX) {
            // we got the result from the "select invitation" UI (invitation inbox). We're
            // ready to accept the selected invitation:
            if (googleGamesHandler != null) {
                googleGamesHandler.handleInvitationInboxResult(resultCode, intent);
            }
        } else if (requestCode == RC_WAITING_ROOM) {
            // we got the result from the "waiting room" UI.
            if (resultCode == Activity.RESULT_OK) {
                // ready to start playing
                Log.d(TAG, "Starting game (waiting room returned OK).");
                configureGame();
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player indicated that they want to leave the room
                disconnectFromPlayRoomAndTwilio();
                viewController.onSwitchToMainScreen(false);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Dialog was cancelled (user pressed back key, for instance). In our game,
                // this means leaving the room too. In more elaborate games, this could mean
                // something else (like minimizing the waiting room UI).
                disconnectFromPlayRoomAndTwilio();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    // this tells us if the user has volume
    private boolean getAudioState() {
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch (audio.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                return true;
            case AudioManager.RINGER_MODE_SILENT:
                return false;
            case AudioManager.RINGER_MODE_VIBRATE:
                return false;
            default:
                return true;
        }
    }

    // starts our purchase flow
    public void onPurchaseButtonClicked() {
        Log.d(TAG, "Purchase button clicked.");

        if (acquireFragment == null) {
            acquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            acquireFragment.show(getSupportFragmentManager(), DIALOG_TAG);

            if (mBillingManager != null
                    && mBillingManager.getBillingClientResponseCode()
                    > BILLING_MANAGER_NOT_INITIALIZED) {
                acquireFragment.onManagerReady(this);
            }
        }
    }

    // returns true if acquireFragment is visible
    public boolean isAcquireFragmentShown() {
        return acquireFragment != null && acquireFragment.isVisible();
    }

    // gets our last piece of our 64bit
    private void startBillingManager(DatabaseReference myRef) {
        dataRetrieval = new DataRetrieval(myRef);
        dataRetrieval.retrieve(new DataRetrieval.OnRetrievedListener() {
            @Override
            public void onRetrieved(String payload) {

                if (mBillingHelperRedorBlack != null) {
                    mBillingManager = new BillingManager(MainActivity.this, mBillingHelperRedorBlack.getUpdateListener(), getResources().getString(R.string.base_sixty_key), payload);
                }
            }
        });
    }

    // this is called when our billing manager is ready
    public void onBillingManagerSetupFinished() {
        if (acquireFragment != null) {
            acquireFragment.onManagerReady(this);
        }
    }

    // this listener tells us that we have received game information and passes it to the game handler or changes views we need changed
    MessagedReceivedListener messagedReceivedListener = new MessagedReceivedListener() {
        @Override
        public void onColorReceived(int color) {
            Log.d(TAG, "onColorReceived, opponent color = " + Integer.toString(color) + " RED = 0, BLACK = 1, NONE = -1");
            gameHandler.onSetOtherPlayerChoice(color);
        }

        @Override
        public void onWhoPicksFirstReceived(int whoPicksFirst) {
            Log.d(TAG, "onWhoPicksFirstReceived: who picks = " + Integer.toString(whoPicksFirst));
            // only player 2 receives this message, so if whoPicksFirst == PLAYER_TWO_FIRST, player two gets their buttons back
            gameHandler.onSetWhoPicksFirst(whoPicksFirst);
            if (whoPicksFirst == PLAYER_TWO_FIRST) {
                viewController.onMakeToast(getString(R.string.i_pick_first));
                viewController.onRecoverButtons();
            }
        }

        @Override
        public void onOpponentWantsToContinueReceived(int paying) {
            gameHandler.onSetOpponentWantsToContinueReceived(paying);
            if (paying == I_AM_PAYING) {
                freeContinueMessageReceived = true;
                viewController.onRecoverContinueCallButton(getString(R.string.continue_call_free));
            }
        }

        @Override
        public void onGameInfoReceived(GameInfo gameInfo) {
            receiveGameInfoAndJoinRoom(gameInfo);
            Log.d(TAG, "onGameInfoReceived");
        }
    };

    // this passes incoming messages to our message receiver
    IncomingMessageListener incomingMessageListener = new IncomingMessageListener() {
        @Override
        public void onIncomingMessage(byte[] bytes) {
            messageReceiver.validateIncomingMessage(bytes);
        }
    };

    // this is our listener that tells us if we have been connected or disconnected from google
    public ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void onDisconnectedFromGoogle() {
            if (googleGamesHandler != null) {
                googleGamesHandler = null;
            }
            safeReleaseAudioVideo();
            viewController.onSwitchToLoginScreen();
        }

        @Override
        public void onConnectedToGoogle(GoogleSignInAccount googleSignInAccount) {
            if (viewController != null) {
                viewController.onSwitchToMainScreen(false);
            }
            if (googleGamesHandler == null) {
                googleGamesHandler = new GoogleGamesHandler(MainActivity.this, googleSignInAccount, incomingMessageListener, viewController);
            }
            if (fireBaseHandler != null) {
                fireBaseHandler.firebaseAuthWithGoogle(googleSignInAccount);
            }
        }
    };

    // this is our listener that tells firebase what we reported someone for
    public ReportChoiceListener reportChoiceListener = new ReportChoiceListener() {
        @Override
        public void onReportResult(int code) {
            if (code == CODE_ABUSE || code == CODE_NUDITY) {
                if (fireBaseHandler.isFireBaseUserNotNull()) {
                    fireBaseHandler.reportUser(code, lastGameNumber);
                }
            }
        }
    };

    // this tells us when firebase is set up and ready and passes our database reference and our user ID
    public FireBaseSetUpListener fireBaseSetUpListener = new FireBaseSetUpListener() {
        @Override
        public void onFireBaseSetUp(DatabaseReference myRef, String myUID) {
            serverTime = new ServerTime(myRef);
            mBillingHelperRedorBlack = new BillingHelperRedorBlack(MainActivity.this, myRef, myUID, viewController);
            startBillingManager(myRef);
        }
    };

    // this is our listener for events called by our Twilio Handler
    public TwilioListener twilioListener = new TwilioListener() {

        @Override
        public void onRemoteParticipantConnected() {
            gameHandler.startGame(gameController);
        }

        @Override
        public void onOtherPersonDisconnected() {
            disconnectFromPlayRoomAndTwilio();
            gameHandler.onGameFinished();
        }

        @Override
        public void onIWasDisconnected() {
            disconnectFromPlayRoomAndTwilio();
            gameHandler.onGameFinished();
        }

        @Override
        public void onErrorConnecting() {
            disconnectFromPlayRoomAndTwilio();
            viewController.onMakeToast(getResources().getString(R.string.error_with_game));
        }
    };

    // this is our gameListener which handles events coming from our active game
    GameListener gameListener = new GameListener() {
        @Override
        public void onSubtractPaidTicket() {
            fireBaseHandler.subtractPaidTicket();
        }

        @Override
        public void onGameStarted() {
            switch (ticketBeingUsed) {
                case FREE_TICKET:
                    fireBaseHandler.subtractFreeTicket();
                    break;
                case PAID_TICKET:
                    fireBaseHandler.subtractPaidTicket();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onBroadCastMessage(byte[] bytes) {
            googleGamesHandler.broadcastMessage(bytes);
        }

        @Override
        public void onPlaySoundEffect(int resId) {
            soundEffects.playSound(resId);
        }

        @Override
        public void onEndGameSoundEffects(boolean win) {
            soundEffects.endGameSound(win);
        }

        @Override
        public void onGameEnded(boolean disconnected) {
            viewController.onSwitchToMainScreen(true);
            disconnectFromPlayRoomAndTwilio();
        }

        @Override
        public void onGameContinued() {
            freeContinueMessageReceived = false;
        }
    };

    // cleans up possible leaks for onStop
    private void cleanUpLooseEnds() {
        if (fireBaseHandler != null) {
            fireBaseHandler.removeDatabaseListenersFireBase();
        }
        if (googleGamesHandler != null) {
            googleGamesHandler.leaveRoom();
        }
        if (soundEffects != null) {
            soundEffects.cleanUpSoundEffects();
        }
        if (viewController != null) {
            viewController.onSwitchToMainScreen(false);
        }
        safeReleaseAudioVideo();
    }

    // cleans up classes for onDestroy
    private void destructionCleanUp() {
        safeReleaseAudioVideo();
        if (googleSignInHandler != null) {
            googleSignInHandler = null;
        }
        if (googleGamesHandler != null) {
            googleGamesHandler = null;
        }
        if (fireBaseHandler != null) {
            fireBaseHandler.removeDatabaseListenersFireBase();
            fireBaseHandler = null;
        }
        if (gameHandler != null) {
            gameHandler = null;
        }
        if (twilioHandler != null) {
            twilioHandler = null;
        }
        if (permissionChecker != null) {
            permissionChecker = null;
        }
    }

    // makes sure all the classes we need are initialized
    private void checkAndInitializeClasses() {
        if (mContext == null) {
            mContext = this;
        }
        if (permissionChecker == null) {
            permissionChecker = new PermissionChecker(this);
        }

        if (googleSignInHandler == null) {
            googleSignInHandler = new GoogleSignInHandler(this, connectionListener);
        }
        if (viewController == null) {
            viewController = new ViewController(mContext, onClickListener, reportChoiceListener);
        }
        if (fireBaseHandler == null) {
            fireBaseHandler = new FireBaseHandler(viewController, fireBaseSetUpListener, this);
        }
        if (soundEffects == null) {
            soundEffects = new SoundEffects(mContext);
        }
        if (messageReceiver == null) {
            messageReceiver = new MessageReceiver(messagedReceivedListener);
        }
        if (twilioHandler == null) {
            twilioHandler = new TwilioHandler(viewController, this, twilioListener);
        }
        if (gameHandler == null) {
            gameHandler = new GameHandler(viewController, gameListener, this);
        }
    }

    // resets our variables to be ready for the next round
    private void resetGameFlowVariables() {
        gameController = false;
        freeContinueMessageReceived = false;
        twilioRoomToJoin = "";
        lastGameNumber = "";
    }

}

