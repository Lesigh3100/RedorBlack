package com.kevin.android.redorblack;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.util.BillingHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationCallback;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.android.redorblack.GameLogic.MessageReceiver;
import com.kevin.android.redorblack.GameLogic.MessagedReceivedListener;
import com.kevin.android.redorblack.billing.AcquireFragment;
import com.kevin.android.redorblack.billing.BillingHelperRedorBlack;
import com.kevin.android.redorblack.billing.BillingManager;
import com.kevin.android.redorblack.billing.BillingProvider;
import com.kevin.android.redorblack.dataclasses.Game;
import com.kevin.android.redorblack.dataclasses.GameContainer;
import com.kevin.android.redorblack.dataclasses.GameInfo;
import com.kevin.android.redorblack.dataclasses.User;
import com.kevin.android.redorblack.firebasemethods.DataRetrieval;
import com.kevin.android.redorblack.twilio.CameraCapturerCompat;
import com.kevin.android.redorblack.utility.Animations;
import com.kevin.android.redorblack.utility.EmojiDropper;
import com.kevin.android.redorblack.dataclasses.GameVariables;
import com.kevin.android.redorblack.firebasemethods.Reporter;
import com.kevin.android.redorblack.utility.ServerTime;
import com.kevin.android.redorblack.utility.SoundEffects;
import com.kevin.android.redorblack.firebasemethods.TicketManager;
import com.kevin.android.redorblack.utility.Utility;
import com.luolc.emojirain.EmojiRainLayout;
import com.twilio.video.AudioOptions;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.EncodingParameters;
import com.twilio.video.G722Codec;
import com.twilio.video.H264Codec;
import com.twilio.video.I420Frame;
import com.twilio.video.IsacCodec;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.OpusCodec;
import com.twilio.video.PcmaCodec;
import com.twilio.video.PcmuCodec;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoCodec;
import com.twilio.video.VideoRenderer;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


import com.twilio.video.Vp8Codec;
import com.twilio.video.Vp9Codec;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static com.kevin.android.redorblack.utility.Broadcaster.*;
import static com.kevin.android.redorblack.constants.GameConstants.*;
import static com.kevin.android.redorblack.constants.FirebaseConstants.*;
import static com.kevin.android.redorblack.dataclasses.GameVariables.*;
import static com.kevin.android.redorblack.billing.BillingConstants.DIALOG_TAG;
import static com.kevin.android.redorblack.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED;
import static com.kevin.android.redorblack.twilio.TwilioConstants.*;

@Keep
public class MainActivity extends AppCompatActivity implements BillingProvider {

    // our context
    Context mContext;
    /*
     * API INTEGRATION SECTION. This section contains the code that integrates
     * the game with the Google Play game services API.
     */
    final static String TAG = "RedorBlack";
    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;

    // google billing objects
    private BillingManager mBillingManager;
    private BillingHelperRedorBlack mBillingHelperRedorBlack;
    private AcquireFragment acquireFragment;
    private BillingHelper mBillingHelper;

    // test item SKU
    static final String ITEM_SKU = "android.test.purchased";


    // Client used to interact with the real time multiplayer system.
    private RealTimeMultiplayerClient mRealTimeMultiplayerClient = null;

    // Client used to interact with the Invitation system.
    private InvitationsClient mInvitationsClient = null;

    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    String mRoomId = null;

    // Holds the configuration of the current room.
    RoomConfig mRoomConfig;
    // Are we playing in multiplayer mode?
    boolean mMultiplayer = false;

    // The participants in the currently active game
    ArrayList<Participant> mParticipants = null;
    // My participant ID in the currently active game
    String mMyId = null;
    // Other person's ID


    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;

    // permission request code
    final int MY_PERMISSIONS_GRANTED = 9666;

    // Twilio declarations
    /*
     * AudioCodec and VideoCodec represent the preferred codec for encoding and decoding audio and
     * video.
     */
    private com.twilio.video.AudioCodec audioCodec;
    private VideoCodec videoCodec;
    com.twilio.video.Room.Listener mRoomListener;

    public EncodingParameters encodingParameters;
    private CameraCapturerCompat cameraCapturerCompat;
    private String remoteParticipantIdentity;
    CameraCapturer cameraCapturer;
    LocalVideoTrack localVideoTrack;
    LocalAudioTrack localAudioTrack;
    AudioManager audioManager;
    MediaPlayer mediaPlayer;
    AudioOptions audioOptions;
    private int previousAudioMode;
    private boolean previousMicrophoneMute;

    // room needs
    private String myIdentity;
    String accessToken;

    private LocalParticipant localParticipant;
    private com.twilio.video.VideoRenderer localVideoView;

    /*
     * Android shared preferences used for settings
     */
    private SharedPreferences preferences;

    // Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    DatabaseReference userRef;
    private FirebaseAuth mAuth;
    ServerTime serverTime;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
    );
    public String myFBId;
    // class that handles reporting users
    Reporter reporter;
    // My Views
    ImageView downArrowBlackButton;
    ImageView downArrowRedButton;
    ImageButton stopCallButton;
    ImageButton continueCallButton;
    ImageButton reportLastUserButton;
    ImageButton signOutButton;
    ImageButton playButton;
    Button blackButton;
    Button redButton;
    SignInButton signInButton;
    VideoView primaryVideoView;
    VideoView thumbnailVideoView;
    TextView reportText;
    TextView playText;
    TextView redText;
    TextView orText;
    TextView blackText;
    TextView freeTokenCountdownClock;
    TextView freeTicketsLeft;
    TextView paidTicketsLeft;
    TextView timeCountDownClock;
    TextView continueCallText;
    RelativeLayout signInScreen;
    RelativeLayout mainScreen;
    LinearLayout redOrBlackTextLayout;

    private View mScreenWait, mScreenMain;

    EmojiRainLayout emojiRainLayout;



    // User object for current user
    User mUser;
    // declaration of animator & sound effects
    Animations animations;
    // class that handles sound effects
    SoundEffects soundEffects;
    // string format for tokens
    final String format = String.format("%%0%dd", 2);
    // class to create & drop random emojis
    EmojiDropper emojiDropper;
    // class that manages game variables
    GameVariables gameVariables;
    // class that manages tickets
    TicketManager ticketManager;
    // get specific data from FB
    DataRetrieval dataRetrieval;
    // class to pass between players via realtime messages
    GameContainer gameContainer;
    // handles incoming realtime messages
    MessageReceiver messageReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;


        // Create the client used to sign in
        googleSignInAndInitilizations();

        // Firebase
        FirebaseApp.initializeApp(mContext);
        setUpFirebase();


        initializeViews();

        soundEffects = new SoundEffects(mContext);
        emojiDropper = new EmojiDropper(emojiRainLayout);
        reporter = new Reporter(myRef);
        // ensure we have all permissions
        if (!allPermissionsGranted()) {
            checkAllPermissions();
        }
        // check our preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        animations = new Animations(mContext, redOrBlackTextLayout, redText, blackText);
        messageReceiver = new MessageReceiver(messagedReceivedListener);

    }



    @Override
    protected void onStop() {
        Log.d(TAG, "onStop called");
        if (userRef != null){
            userRef.removeEventListener(freeTokenListener);
            userRef.removeEventListener(paidTokenListener);
            userRef.removeEventListener(timeTokenUsedListener);
        }
        soundEffects.cleanUpSoundEffects();
        stopKeepingScreenOn();
        leaveRoom();
        switchToMainScreen();
        safeReleaseAudioVideo();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");


        // sign in silently or get user reference here?

        if (gameVariables != null) {
            gameVariables.setGamestate(GameVariables.Gamestate.NotPlaying);
        } else {
            gameVariables = new GameVariables();
        }

        /*
         * Update preferred audio and video codec in case changed in settings
        */
/*
    audioCodec = getAudioCodecPreference(SettingsActivity.PREF_AUDIO_CODEC,
                SettingsActivity.PREF_AUDIO_CODEC_DEFAULT);
        videoCodec = getVideoCodecPreference(SettingsActivity.PREF_VIDEO_CODEC,
                SettingsActivity.PREF_VIDEO_CODEC_DEFAULT);
*/

        audioCodec = getAudioCodecPreference(PREF_AUDIO_CODEC,
                PREF_AUDIO_CODEC_DEFAULT);
        videoCodec = getVideoCodecPreference(PREF_VIDEO_CODEC,
                PREF_VIDEO_CODEC_DEFAULT);

        final EncodingParameters newEncodingParameters = getEncodingParameters();
        encodingParameters = newEncodingParameters;
        if (!allPermissionsGranted()) {
            checkAllPermissions();
        } else {
            if (localVideoTrack == null) {
                /*
                 * If connected to a Room then share the local video track.
                 */
                if (localParticipant != null) {
                    localVideoTrack = LocalVideoTrack.create(this,
                            true,
                            cameraCapturerCompat.getVideoCapturer(),
                            LOCAL_VIDEO_TRACK_NAME);
                    localVideoTrack.addRenderer(localVideoView);
                    localParticipant.publishTrack(localVideoTrack);

                    /*
                     * Update encoding parameters if they have changed.
                     */
                    if (!newEncodingParameters.equals(encodingParameters)) {
                        localParticipant.setEncodingParameters(newEncodingParameters);
                    }
                }
            }
        }
        if (audioManager == null) {
            audioVideoSetup();
        }

        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        signInSilently();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ON PAUSE CALLED");
        // unregister our listeners.  They will be re-registered via onResume->signInSilently->onConnected.
        if (mInvitationsClient != null) {
            mInvitationsClient.unregisterInvitationCallback(mInvitationCallback);
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.do_you_want_to_exit));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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
    }

    public void onDisconnected() {
        Log.d(TAG, "onDisconnected()");
        mInvitationsClient = null;
        safeReleaseAudioVideo();
        switchToLoginScreen();
    }


    @Override
    protected void onDestroy() {
        if (mRealTimeMultiplayerClient != null) {
            mRealTimeMultiplayerClient = null;
        }
        safeReleaseAudioVideo();
        super.onDestroy();
    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }


// hides the bottom bar, only during a video chat
    // currently not using because it causes issues with buttons
    /*
    private void hideBottomBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (decorView.getSystemUiVisibility() != uiOptions){
            decorView.setSystemUiVisibility(uiOptions);
        }
    } */

    // checks if video or audio tracks are null and if not releases them
    private void safeReleaseAudioVideo() {
        Log.d(TAG, "SAFE RELEASE AUDIO VIDEO CALLED ! ! ! ! !");
        if (localVideoTrack != null) {
            localVideoTrack.release();
        }
        if (localAudioTrack != null) {
            localAudioTrack.release();
        }
    }

    // currently not using options menu
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.speaker_menu_item:
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    item.setIcon(R.drawable.ic_phonelink_ring_white_24dp);
                } else {
                    audioManager.setSpeakerphoneOn(true);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_volume_up_white_24dp));
                }
                return true;
            default:
                return false;
        }
    }
*/

    // make a view invisible
    private void dismissView(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    // make a view visible
    private void recoverView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    // Sets view for playing a round of the game
    private void switchToPlayingScreen() {
        switchToScreen(R.id.main_screen);
        dismissView(signInScreen);
        dismissView(playButton);
        dismissView(playText);
        dismissView(redOrBlackTextLayout);
        dismissButtons();
        dismissView(freeTokenCountdownClock);
        dismissView(paidTicketsLeft);
        dismissView(freeTicketsLeft);
        dismissView(reportText);
        dismissView(reportLastUserButton);
        // might need to fix this
        recoverView(primaryVideoView);
    }

    // Sets view to the main page where you can start playing
    private void switchToMainScreen() {
        switchToScreen(R.id.main_screen);
        dismissView(signInScreen);
        dismissButtons();
        recoverView(playButton);
        recoverView(playText);
        recoverView(redOrBlackTextLayout);
        recoverView(freeTokenCountdownClock);
        recoverView(paidTicketsLeft);
        recoverView(freeTicketsLeft);
        recoverView(signOutButton);
        dismissView(primaryVideoView);
        dismissView(thumbnailVideoView);
        dismissView(stopCallButton);
        dismissView(continueCallButton);
        dismissView(continueCallText);
        dismissView(timeCountDownClock);
        thumbnailVideoView.setVisibility(View.GONE);
        if (!gameVariables.getLastGameNumber().equals("")) {
            recoverView(reportLastUserButton);
            recoverView(reportText);
        }
        // start color changing animation
        animations.colorChanges();
    }

    // switches to login screen
    private void switchToLoginScreen() {
        switchToScreen(R.id.sign_in_screen);
        dismissView(mainScreen);
    }

    // makes both buttons invisible
    private void dismissButtons() {
        dismissView(redButton);
        dismissView(blackButton);
        dismissView(downArrowRedButton);
        dismissView(downArrowBlackButton);
    }

    // brings both buttons back to the screen & makes sure they are clickable
    private void recoverButtons() {
        Log.d(TAG, "RECOVER BUTTONS CALLED");
        recoverView(blackButton);
        recoverView(redButton);
        recoverView(downArrowBlackButton);
        recoverView(downArrowRedButton);
        redButton.bringToFront();
        blackButton.bringToFront();
        redButton.setClickable(true);
        blackButton.setClickable(true);
        animations.pulseAnimation(blackButton);
        animations.pulseAnimation(redButton);
    }

    // Initialization for the views used
    private void initializeViews() {
        blackButton = findViewById(R.id.black_button);
        redButton = findViewById(R.id.red_button);
        downArrowBlackButton = findViewById(R.id.black_button_arrow);
        downArrowRedButton = findViewById(R.id.red_button_arrow);
        playButton = findViewById(R.id.play_button);
        playText = findViewById(R.id.play_text);
        signInButton = findViewById(R.id.sign_in_button);
        mainScreen = findViewById(R.id.main_screen);
        signInScreen = findViewById(R.id.sign_in_screen);
        redText = findViewById(R.id.red_text);
        blackText = findViewById(R.id.black_text);
        orText = findViewById(R.id.or_text);
        reportText = findViewById(R.id.report_user_text);
        freeTicketsLeft = findViewById(R.id.ticket_free_count);
        paidTicketsLeft = findViewById(R.id.ticket_paid_count);
        freeTokenCountdownClock = findViewById(R.id.token_countdown_clock);
        redOrBlackTextLayout = findViewById(R.id.red_or_black_text_layout);
        stopCallButton = findViewById(R.id.stop_call_button);
        continueCallButton = findViewById(R.id.continue_call_button);
        continueCallText = findViewById(R.id.continue_call_text);
        emojiRainLayout = findViewById(R.id.group_emoji_container);
        timeCountDownClock = findViewById(R.id.countdown_clock);
        primaryVideoView = findViewById(R.id.videoview);
        thumbnailVideoView = findViewById(R.id.thumbnail_video_view);
        reportLastUserButton = findViewById(R.id.report_user_button);
        signOutButton = findViewById(R.id.sign_out_button);

        mScreenWait = findViewById(R.id.screen_wait);


        signOutButton.setOnClickListener(onClickListener);
        paidTicketsLeft.setOnClickListener(onClickListener);
        reportLastUserButton.setOnClickListener(onClickListener);
        blackButton.setOnClickListener(onClickListener);
        redButton.setOnClickListener(onClickListener);
        playButton.setOnClickListener(onClickListener);
        signInButton.setOnClickListener(onClickListener);
        stopCallButton.setOnClickListener(onClickListener);
        continueCallButton.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.play_button:
                    gameVariables.resetGameVariables(true);
                    if (isSignedIn() && mUser != null) {

                        if (!getAudioState()) {
                            makeToast(getResources().getString(R.string.please_turn_up_volume));
                            return;
                        }
                        if (!allPermissionsGranted()) {
                            checkAllPermissions();
                        }

                        if (whatTokenToUse() == NEED_TO_PURCHASE_TOKENS) {
                            onPurchaseButtonClicked();
                            makeToast(getResources().getString(R.string.need_more_tickets));
                        } else {
                            Log.d(TAG, "STARTING GAME : : : ");
                            gameVariables.setTokenBeingUsed(whatTokenToUse());
                            dismissView(playButton);
                            soundEffects.playSound(R.raw.gong);
                            createAudioAndVideoTracks();
                            switchToPlayingScreen();
                            startQuickGame();
                        }
                    } else {
                        signInSilently();
                        makeToast(getResources().getString(R.string.not_connected));
                    }
                    break;
                case R.id.red_button:
                    gameVariables.setMyChoice(RED);
                    broadcastMessage(sendGameContainer(gameVariables, CHANGED_COLOR));
                    dismissView(blackButton);
                    dismissView(downArrowBlackButton);
                    dismissView(downArrowRedButton);
                    redButton.setClickable(false);
                    break;
                case R.id.black_button:
                    gameVariables.setMyChoice(BLACK);
                    broadcastMessage(sendGameContainer(gameVariables, CHANGED_COLOR));
                    dismissView(downArrowBlackButton);
                    dismissView(downArrowRedButton);
                    dismissView(redButton);
                    blackButton.setClickable(false);
                    break;
                case R.id.sign_in_button:
                    Log.d(TAG, "SIGN IN BUTTON CLICKED");
                    startSignInIntent();
                    break;
                case R.id.stop_call_button:
                    gameVariables.setPlayerDisconnected(true);
                    endGame(true);
                    break;
                case R.id.continue_call_button:
                    // haven't received a message on continuing or not, need to pay to continue game
                    if (!gameVariables.isContinueMessageReceived() && hasPaidTokens()) {
                        gameVariables.setPayingForGameContinue(I_AM_PAYING);
                        gameVariables.setiWantToContinue(true);
                        broadcastMessage(sendGameContainer(gameVariables, CHANGED_IWANTTOCONTINUE));
                        gameVariables.setContinueMessageSent(true);
                        dismissView(continueCallButton);
                        dismissView(continueCallText);
                    } else if (!gameVariables.isContinueMessageReceived() && !hasPaidTokens()) {
                        // need to purchase tokens, pause video & go to billing
                        // on success before time limit add tokens and send Iwanttocontinue
                        makeToast(getResources().getString(R.string.in_call_billing_not_supported));
                    } else if (gameVariables.isContinueMessageReceived()) {
                        gameVariables.setiWantToContinue(true);
                        broadcastMessage(sendGameContainer(gameVariables, CHANGED_IWANTTOCONTINUE));
                        gameVariables.setContinueMessageSent(true);
                        dismissView(continueCallButton);
                        dismissView(continueCallText);
                    }
                    break;
                case R.id.ticket_paid_count:
                    // start purchase transaction here
                    onPurchaseButtonClicked();
                    break;
                case R.id.report_user_button:
                    // drop down menu to report someone
                    PopupMenu popupMenu = new PopupMenu(mContext, reportLastUserButton);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_report, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_abusive:
                                    reporter.reportUser(CODE_ABUSE, gameVariables.getLastGameNumber(), myFBId, mUser);
                                    dismissView(reportLastUserButton);
                                    dismissView(reportText);
                                    break;
                                case R.id.menu_nudity:
                                    reporter.reportUser(CODE_NUDITY, gameVariables.getLastGameNumber(), myFBId, mUser);
                                    dismissView(reportLastUserButton);
                                    dismissView(reportText);
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                    break;
                case R.id.sign_out_button:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setCancelable(false);
                    builder.setMessage(getResources().getString(R.string.do_you_want_to_sign_out));
                    builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            signOut();
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

    // determines what token to use when playing a normal game round
    private int whatTokenToUse() {
        if (mUser.freeTokens > 0) {
            return FREE_TOKEN;
        } else if (mUser.paidTokens > 0) {
            return PAID_TOKEN;
        } else return NEED_TO_PURCHASE_TOKENS;
    }

    //
    private boolean hasPaidTokens() {
        return mUser.paidTokens > 0;
    }


    // update the user interface with current tokens & time until they get a new free token
    private void updateUserUi() {
        if (mUser != null) {
            freeTicketsLeft.setText(String.format(format, mUser.freeTokens));
            paidTicketsLeft.setText(String.format(format, mUser.paidTokens));
        }

    }


    // creates google sign in options and then signs in using them
    private void googleSignInAndInitilizations() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    // retrieves the user from FB database, sets several listeners to follow tokens being updated
    private void retrieveUser(final FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            myFBId = firebaseUser.getUid();
            // made this into 1 time listener
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null) {
                        writeNewUser(firebaseUser.getUid(), firebaseUser.getDisplayName());
                    } else {
                        mUser = user;
                        // if the user is less than full on free tokens, check to see if they have acquired any
                        updateUserUi();
                        if (ticketManager.tokensLessThanMax()) {
                            ticketManager.checkIfWeNeedToStartTimer();
                        }
                        userRef.child(FREE_TICKET_REFERENCE).addValueEventListener(freeTokenListener);
                        userRef.child(PAID_TICKET_REFERENCE).addValueEventListener(paidTokenListener);
                        userRef.child(TIME_TOKEN_USED_REFERENCE).addValueEventListener(timeTokenUsedListener);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "FIREBASE DATABASE ERROR: " + databaseError);

                }
            });

        }
    }

    // create a new user on first login, sending their ID to the firebase database
    private void writeNewUser(String userId, String username) {
        User user = new User(username, NEW_USER_FREE_STARTING_TOKENS);
        myRef.child("users").child(userId).setValue(user);
    }

    // returns true if the user is signed in
    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    // initilization of firebase objects
    private void setUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        serverTime = new ServerTime(myRef);
    }

    // sign into firebase's auth with googlesignin
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                userRef = myRef.child(USER_REFERENCE).child(user.getUid());
                                // uid updated, initilize billing now
                                myFBId = mAuth.getCurrentUser().getUid();
                                mBillingHelperRedorBlack = new BillingHelperRedorBlack(MainActivity.this, myRef, myFBId);
                                startBillingManager(MainActivity.this);
                                // set ticket manager now that we have written userRef
                                ticketManager = new TicketManager(mContext, userRef, freeTokenCountdownClock);
                                // add new user to database here & give them tokens to start playing
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    writeNewUser(myFBId, user.getDisplayName());
                                } else {
                                    retrieveUser(user);
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.sign_in_screen), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // quick-start a game with a randomly selected opponent
    void startQuickGame() {
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        keepScreenOn();
        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria)
                .build();
        mRealTimeMultiplayerClient.create(mRoomConfig);
    }

    /**
     * Try to sign in without displaying dialogs to the user.
     * <p>
     * If the user has already signed in previously, it will not show dialog.
     */
    public void signInSilently() {
        Log.d(TAG, "signInSilently()");

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                            onDisconnected();
                        }
                    }
                });
    }


    public void signOut() {
        Log.d(TAG, "signOut()");

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signOut(): success");
                            switchToLoginScreen();
                            FirebaseAuth.getInstance().signOut();
                        } else {
                            handleException(task.getException(), "signOut() failed!");
                        }

                        onDisconnected();
                    }
                });
    }
    /*
     * CALLBACKS SECTION. Google Game API Callbacks
     */

    private String mPlayerId;

    // The currently signed in account, used to check the account has changed outside of this activity when resuming.
    GoogleSignInAccount mSignedInAccount = null;

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        if (mSignedInAccount != googleSignInAccount) {
            mSignedInAccount = googleSignInAccount;
            firebaseAuthWithGoogle(mSignedInAccount);
            // update the clients
            mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, googleSignInAccount);
            mInvitationsClient = Games.getInvitationsClient(MainActivity.this, googleSignInAccount);

            // get the playerId from the PlayersClient
            PlayersClient playersClient = Games.getPlayersClient(this, googleSignInAccount);
            playersClient.getCurrentPlayer()
                    .addOnSuccessListener(new OnSuccessListener<Player>() {
                        @Override
                        public void onSuccess(Player player) {
                            mPlayerId = player.getPlayerId();
                            myIdentity = mPlayerId;
                            switchToMainScreen();
                        }
                    })
                    .addOnFailureListener(createFailureListener("There was a problem getting the player id!"));
        }

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
//        mInvitationsClient.registerInvitationCallback(mInvitationCallback);

        // get the invitation from the connection hint
        // Retrieve the TurnBasedMatch from the connectionHint
        GamesClient gamesClient = Games.getGamesClient(MainActivity.this, googleSignInAccount);
        gamesClient.getActivationHint()
                .addOnSuccessListener(new OnSuccessListener<Bundle>() {
                    @Override
                    public void onSuccess(Bundle hint) {
                        if (hint != null) {
                            Invitation invitation =
                                    hint.getParcelable(Multiplayer.EXTRA_INVITATION);

                            if (invitation != null && invitation.getInvitationId() != null) {
                                // retrieve and cache the invitation ID
                                Log.d(TAG, "onConnected: connection hint has a room invite!");
                                acceptInviteToRoom(invitation.getInvitationId());
                            }
                        }
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem getting the activation hint!"));
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invitationId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invitationId);

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setInvitationIdToAccept(invitationId)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .build();

        switchToScreen(R.id.screen_wait);
        keepScreenOn();

        mRealTimeMultiplayerClient.join(mRoomConfig)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Room Joined Successfully!");
                    }
                });
    }

    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(e, string);
            }
        };
    }

    /**
     * Since a lot of the operations use tasks, we can use a common handler for whenever one fails.
     *
     * @param exception The exception to evaluate.  Will try to display a more descriptive reason for the exception.
     * @param details   Will display alongside the exception if you wish to provide more details for why the exception
     *                  happened
     */
    private void handleException(Exception exception, String details) {
        int status = 0;

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        String errorString = null;
        switch (status) {
            case GamesCallbackStatusCodes.OK:
                break;
            case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                errorString = getString(R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
                errorString = getString(R.string.match_error_already_rematched);
                break;
            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
                errorString = getString(R.string.network_error_operation_failed);
                break;
            case GamesClientStatusCodes.INTERNAL_ERROR:
                errorString = getString(R.string.internal_error);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
                errorString = getString(R.string.match_error_inactive_match);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
                errorString = getString(R.string.match_error_locally_modified);
                break;
            default:
                errorString = getString(R.string.unexpected_status, GamesClientStatusCodes.getStatusCodeString(status));
                break;
        }

        if (errorString == null) {
            return;
        }

        String message = getString(R.string.status_exception_error, details, status, exception);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Error")
                .setMessage(message + "\n" + errorString)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    private InvitationCallback mInvitationCallback = new InvitationCallback() {
        // Called when we get an invitation to play a game. We react by showing that to the user.
        @Override
        public void onInvitationReceived(@NonNull Invitation invitation) {
            // We got an invitation to play a game! So, store it in
            // mIncomingInvitationId
            // and show the popup on the screen.
            mIncomingInvitationId = invitation.getInvitationId();
            ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                    invitation.getInviter().getDisplayName() + " " +
                            getString(R.string.is_inviting_you));
            switchToScreen(mCurScreen); // This will show the invitation popup
        }

        @Override
        public void onInvitationRemoved(@NonNull String invitationId) {

            if (mIncomingInvitationId.equals(invitationId) && mIncomingInvitationId != null) {
                mIncomingInvitationId = null;
                switchToScreen(mCurScreen); // This will hide the invitation popup
            }
        }
    };
    private RoomStatusUpdateCallback mRoomStatusUpdateCallback = new RoomStatusUpdateCallback() {
        // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
        // is connected yet).
        @Override
        public void onConnectedToRoom(Room room) {
            Log.d(TAG, "onConnectedToRoom.");

            //get participants and my ID:
            mParticipants = room.getParticipants();
            mMyId = room.getParticipantId(mPlayerId);

            // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
            if (mRoomId == null) {
                mRoomId = room.getRoomId();
            }

            // print out the list of participants (for debug purposes)
            Log.d(TAG, "Room ID: " + mRoomId);
            Log.d(TAG, "My ID " + mMyId);
            Log.d(TAG, "<< CONNECTED TO ROOM>>");
        }

        // Called when we get disconnected from the room. We return to the main screen.
        @Override
        public void onDisconnectedFromRoom(Room room) {
            mRoomId = null;
            mRoomConfig = null;
            showGameError();
        }


        // We treat most of the room update callbacks in the same way: we update our list of
        // participants and update the display. In a real game we would also have to check if that
        // change requires some action like removing the corresponding player avatar from the screen,
        // etc.
        @Override
        public void onPeerDeclined(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerInvitedToRoom(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onP2PDisconnected(@NonNull String participant) {
        }

        @Override
        public void onP2PConnected(@NonNull String participant) {
        }

        @Override
        public void onPeerJoined(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerLeft(Room room, @NonNull List<String> peersWhoLeft) {
            updateRoom(room);
        }

        @Override
        public void onRoomAutoMatching(Room room) {
            updateRoom(room);
        }

        @Override
        public void onRoomConnecting(Room room) {
            updateRoom(room);
        }

        @Override
        public void onPeersConnected(Room room, @NonNull List<String> peers) {
            updateRoom(room);
        }

        @Override
        public void onPeersDisconnected(Room room, @NonNull List<String> peers) {
            updateRoom(room);
        }
    };

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.game_problem))
                .setNeutralButton(android.R.string.ok, null).create();

        switchToMainScreen();
    }

    private RoomUpdateCallback mRoomUpdateCallback = new RoomUpdateCallback() {

        // Called when room has been created
        @Override
        public void onRoomCreated(int statusCode, Room room) {
            Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
                showGameError();
                return;
            }

            // save room ID so we can leave cleanly before the game starts.
            mRoomId = room.getRoomId();

            // show the waiting room UI
            showWaitingRoom(room);
        }

        // Called when room is fully connected.
        @Override
        public void onRoomConnected(int statusCode, Room room) {
            Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
                showGameError();
                return;
            }
            updateRoom(room);
        }

        @Override
        public void onJoinedRoom(int statusCode, Room room) {
            Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
                showGameError();
                return;
            }

            // show the waiting room UI
            showWaitingRoom(room);
        }

        // Called when we've successfully left the room (this happens a result of voluntarily leaving
        // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
        @Override
        public void onLeftRoom(int statusCode, @NonNull String roomId) {
            // we have left the room; return to main screen.
            Log.d(TAG, "onLeftRoom, code " + statusCode);
            switchToMainScreen();
        }
    };

    void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
        }
    }

    // Leave the room.
    void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        stopKeepingScreenOn();
        if (mRoomId != null && mRealTimeMultiplayerClient != null) {
            mRealTimeMultiplayerClient.leave(mRoomConfig, mRoomId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mRoomId = null;
                            mRoomConfig = null;
                        }
                    });
            switchToScreen(R.id.screen_wait);
        } else {
            switchToMainScreen();
        }
    }

    /**
     * Start a sign in activity.  To properly handle the result, call tryHandleSignInResult from
     * your Activity's onActivityResult function
     */
    public void startSignInIntent() {
        Log.d(TAG, "START SIGN IN INTENT CALLED");
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    OnRealTimeMessageReceivedListener mOnRealTimeMessageReceivedListener = new OnRealTimeMessageReceivedListener() {
        @Override
        public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
            byte[] buf = realTimeMessage.getMessageData();
            //  String sender = realTimeMessage.getSenderParticipantId();
            Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);
            messageReceiver.incomingMessage(buf);
        }
    };
    // This array lists all the individual screens our game has.
    final static int[] SCREENS = {
            R.id.main_screen, R.id.sign_in_screen, R.id.invitation_popup,
            R.id.screen_wait
    };
    int mCurScreen = -1;

    void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
        mCurScreen = screenId;

        // should we show the invitation popup?
        boolean showInvPopup = true;
        if (mIncomingInvitationId == null) {
            // no invitation, so no popup
            showInvPopup = false;
        } else if (mMultiplayer) {
            // if in multiplayer, only show invitation on main screen
            showInvPopup = (mCurScreen == R.id.main_screen);
        }
        findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
    }

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    void showWaitingRoom(Room room) {
        if (room == null) {
            return;
        }
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = 2;
        mRealTimeMultiplayerClient.getWaitingRoomIntent(room, MIN_PLAYERS)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        // show waiting room UI
                        startActivityForResult(intent, RC_WAITING_ROOM);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem getting the waiting room!"));
    }

      /*
       GAME LOGIC SECTION. Methods that implement the game's rules.
      */

    // if you are assigned the role of picking who goes first you then create the video room name
    private void configureGame() {
        Log.d(TAG, "CONFIGURE GAME CALLED");
        if (pickWhoGoesFirst()) {
            makeVideoRoomTokenAndJoin(true);
            messageReceiver.setPlayer1(true);
            gameVariables.setPlayerOne(true);
        } else {
            messageReceiver.setPlayer1(false);
            gameVariables.setPlayerOne(false);
        }
    }

    // Start the gameplay phase of the game.
    private void startGame() {
        gameVariables.setGamestate(GameVariables.Gamestate.FirstPlayerPicking);
        recoverView(stopCallButton);
        recoverView(timeCountDownClock);
        // use token here
        Log.d(TAG, "TOKEN BEING USED = " + Integer.toString(gameVariables.getTokenBeingUsed()));
        if (gameVariables.getTokenBeingUsed() == FREE_TOKEN) {
            ticketManager.subtractFreeTokenFromUser();
        } else if (gameVariables.getTokenBeingUsed() == PAID_TOKEN) {
            ticketManager.subtractPaidTokenFromUser();
        } else {
            // something went wrong here
            Log.d(TAG, "No token selected to be used");
        }
        Log.d(TAG, "START GAME CALLED");
        if (pickWhoGoesFirst()) {
            boolean iPickColor = Math.random() >= .5;
            if (iPickColor) {
                Log.d(TAG, "I PICK COLOR");
          //      broadcastMessage(broadcastWhoPicks(iPickColor));
                gameVariables.setFirstToPick(true);
                makeToast(getString(R.string.i_pick_first));
                recoverButtons();
            } else {
                Log.d(TAG, "I DO NOT PICK COLOR");
         //       broadcastMessage(broadcastWhoPicks(!iPickColor));
                gameVariables.setFirstToPick(false);
                dismissButtons();
                makeToast(getString(R.string.they_pick_first));
            }
            broadcastMessage(sendGameContainer(gameVariables, CHANGED_WHO_GOES_FIRST));
        }
        gameVariables.setSecondsLeft(GAME_ROUND_DURATION);
        startGameHandler();
    }


    // run the gameTick() method every second to update the game.
    private void startGameHandler() {
        Log.d(TAG, "START GAME HANDLER CALLED");
        final boolean notDisconnected = false;
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameVariables.isPlayerDisconnected()) {
                    Log.d(TAG, "gameVariables.isPlayerDisconnected coming up");
                    endGame(gameVariables.isPlayerDisconnected());
                    makeToast(getResources().getString(R.string.other_player_disconnected));
                    return;
                }
                if (gameVariables.getSecondsLeft() <= 0) {
                    switch (gameVariables.getGamestate()) {
                        case FirstPlayerPicking:
                            gameVariables.setGamestate(GameVariables.Gamestate.PlayerGuessing);
                            Log.d(TAG, "GAMESTATE CURRENTLY = " + gameVariables.getGamestate().name());
                            gameVariables.setSecondsLeft(GAME_ROUND_DURATION);
                            if (gameVariables.isFirstToPick() && gameVariables.getMyChoice() == NO_CHOICE_RECEIVED) {
                                // time up, choose random color
                                gameVariables.setMyChoice(getRandomChoice());
                                if (gameVariables.getMyChoice() == RED){
                                    dismissView(blackButton);
                                    dismissView(downArrowBlackButton);
                                    dismissView(downArrowRedButton);
                                    redButton.setClickable(false);
                                } else {
                                    dismissView(redButton);
                                    dismissView(downArrowBlackButton);
                                    dismissView(downArrowRedButton);
                                    redButton.setClickable(false);
                                }
                                broadcastMessage(sendGameContainer(gameVariables, CHANGED_COLOR));
                            } else if (!gameVariables.isFirstToPick()){
                                recoverButtons();
                            }
                            break;
                        case PlayerGuessing:
                            gameVariables.setGamestate(GameVariables.Gamestate.GameEnding);
                            Log.d(TAG, "GAMESTATE CURRENTLY = " + gameVariables.getGamestate().name());
                            gameVariables.setSecondsLeft(GAME_ROUND_DURATION);
                            recoverView(continueCallButton);
                            setContinueCallText();
                            recoverView(continueCallText);
                            if (!gameVariables.isFirstToPick()) {
                                // time up, choose random color
                                gameVariables.setMyChoice(getRandomChoice());
                                if (gameVariables.getMyChoice() == RED){
                                    dismissView(blackButton);
                                    dismissView(downArrowBlackButton);
                                    dismissView(downArrowRedButton);
                                    redButton.setClickable(false);
                                } else {
                                    dismissView(redButton);
                                    dismissView(downArrowBlackButton);
                                    dismissView(downArrowRedButton);
                                    redButton.setClickable(false);
                                }
                                broadcastMessage(sendGameContainer(gameVariables, CHANGED_COLOR));
                            }
                            break;
                        case GameEnding:
                            // continue game, users want to talk more
                            if (getShouldContinueGame(gameVariables)) {
                                if (gameVariables.getPayingForGameContinue() == I_AM_PAYING) {
                                    ticketManager.subtractPaidTokenFromUser();
                                }
                                // reset flags here in case they want to continue the call again
                                gameVariables.setGameContinued(gameVariables);
                                endGameAnimation();
                                recoverView(continueCallButton);
                                setContinueCallText();
                                recoverView(continueCallText);
                                break;
                            } else {
                                endGameAnimation();
                                endGame(notDisconnected);
                                return;
                            }
                        case GameContinued:
                            if (getShouldContinueGame(gameVariables)) {
                                if (gameVariables.getPayingForGameContinue() == I_AM_PAYING) {
                                    ticketManager.subtractPaidTokenFromUser();
                                }
                                // reset flags here in case they want to continue the call again
                                gameVariables.setGameContinued(gameVariables);
                                recoverView(continueCallButton);
                                setContinueCallText();
                                recoverView(continueCallText);
                                break;
                            } else {
                                endGame(notDisconnected);
                                return;
                            }
                        default:
                            endGame(true);
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
        // media player problem here, gets created & released too fast for quick sounds
        if (gameVariables.isFirstToPick() && gameVariables.getGamestate() == Gamestate.FirstPlayerPicking && gameVariables.getSecondsLeft() == 1){
            soundEffects.playSound(R.raw.time_up);
        } else if (!gameVariables.isFirstToPick() && gameVariables.getGamestate() == Gamestate.PlayerGuessing && gameVariables.getSecondsLeft() == 1){
            soundEffects.playSound(R.raw.time_up);
        }

        if (gameVariables.getSecondsLeft() > 0) {
            gameVariables.setSecondsLeft(gameVariables.getSecondsLeft() - 1);
        }
        // update countdown
        timeCountDownClock.setText("0:" + (gameVariables.getSecondsLeft() < 10 ? "0" : "") + String.valueOf(gameVariables.getSecondsLeft()));
    }


    // show winner animation & losing animation
    private void endGameAnimation() {
        if (gameVariables.isFirstToPick()) {
            if (gameVariables.getMyChoice() == gameVariables.getOpponentChoice()) {
                // I lose
                Log.d(TAG, "I LOSE END GAME CALLED");
                soundEffects.endGameSound(false);
                emojiDropper.losingEmojiDrop();
            } else {
                // I win
                Log.d(TAG, "I WIN END GAME CALLED");
                soundEffects.endGameSound(true);
                emojiDropper.winningEmojiDrop();
            }
        } else {
            if (gameVariables.getMyChoice() == gameVariables.getOpponentChoice()) {
                // I win
                Log.d(TAG, "I WIN END GAME CALLED");
                soundEffects.endGameSound(true);
                emojiDropper.winningEmojiDrop();
            } else {
                // I lose
                Log.d(TAG, "I LOSE END GAME CALLED");
                soundEffects.endGameSound(false);
                emojiDropper.losingEmojiDrop();
            }
        }
    }

    // finishes the game
    public void endGame(boolean playerDisconnected) {
        // ends game without a winner
        if (playerDisconnected && gameVariables.getSecondsLeft() >= 0) {
            Log.d(TAG, "endGame Called for player disconnect!!! ! ! ! ");
            gameVariables.setGameEndedInDisconnect(true);
            disconnectFromPlayRoomAndTwilio();
            return;
        }
        disconnectFromPlayRoomAndTwilio();
        gameVariables.setGamestate(GameVariables.Gamestate.NotPlaying);
        switchToMainScreen();
    }
    /*
    END OF GAME LOGIC
    */




    // disconnect from google play room and twilio room
    private void disconnectFromPlayRoomAndTwilio() {
        if (gameVariables.getTwilioRoom() != null) {
            gameVariables.getTwilioRoom().disconnect();
        }
        gameVariables.resetGameVariables(false);
        leaveRoom();
        mInvitationsClient = null;
        accessToken = "";
        safeReleaseAudioVideo();
    }

    // if true generates a random room name, sends the room name, then retrieves the access token from the server which then starts video chat upon receiving the token
    private void makeVideoRoomTokenAndJoin(Boolean iMakeRoom) {
        if (iMakeRoom) {
            Log.d(TAG, "I MAKE ROOM CALLED");
            gameVariables.setRoomToJoin(UUID.randomUUID().toString());
            logAndSendNewGame(gameVariables.getRoomToJoin());
            retrieveAccessTokenfromServer(myIdentity, gameVariables.getRoomToJoin());
        } else {
            retrieveAccessTokenfromServer(myIdentity, gameVariables.getRoomToJoin());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "resultcode = " + Integer.toString(resultCode));
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                onDisconnected();
                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(R.string.login_fail, null)
                        .show();
            }
            if (resultCode == RESULT_OK) {
          //     FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Log.d(TAG, "FIREBASE FAILED RESULT = " + resultCode);
            }
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // we got the result from the "select players" UI -- ready to create the room
            handleSelectPlayersResult(resultCode, intent);

        } else if (requestCode == RC_INVITATION_INBOX) {
            // we got the result from the "select invitation" UI (invitation inbox). We're
            // ready to accept the selected invitation:
            handleInvitationInboxResult(resultCode, intent);

        } else if (requestCode == RC_WAITING_ROOM) {
            // we got the result from the "waiting room" UI.
            if (resultCode == Activity.RESULT_OK) {
                // ready to start playing
                Log.d(TAG, "Starting game (waiting room returned OK).");
                configureGame();
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player indicated that they want to leave the room
                leaveRoom();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Dialog was cancelled (user pressed back key, for instance). In our game,
                // this means leaving the room too. In more elaborate games, this could mean
                // something else (like minimizing the waiting room UI).
                leaveRoom();
            }
        }


        super.onActivityResult(requestCode, resultCode, intent);
    }
    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.

    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Log.d(TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
        }

        // create the room
        Log.d(TAG, "Creating room...");
        //   switchToScreen(R.id.screen_wait);
        keepScreenOn();

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .addPlayersToInvite(invitees)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria).build();
        mRealTimeMultiplayerClient.create(mRoomConfig);
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        if (invitation != null) {
            acceptInviteToRoom(invitation.getInvitationId());
        }
    }

    // compares player IDs to determine who goes first
    private boolean pickWhoGoesFirst() {
        for (Participant participant : mParticipants) {
            if (participant.getParticipantId().compareTo(mMyId) < 0)
                return false;
        }
        return true;
    }

    // Broadcast the input message to other participants
    public void broadcastMessage(byte[] message) {
        // safeguard incase we disconnected to prevent crashes
        if (mRoomId == null) {
            return;
        }
        // Send to every other participant.
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            Log.d(TAG, "BROADCASTING A MESSAGE, STILL WITH ME!?!");
            // sending the color
            mRealTimeMultiplayerClient.sendReliableMessage(message,
                    mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                        @Override
                        public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                            Log.d(TAG, "RealTime message sent");
                            Log.d(TAG, "  statusCode: " + statusCode);
                            Log.d(TAG, "  tokenId: " + tokenId);
                            Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer tokenId) {
                            Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                        }
                    });

        }
    }


    // a string array of all the required permissions
    private static final String[] appPermissions = {
            CAMERA, RECORD_AUDIO, MODIFY_AUDIO_SETTINGS, READ_PHONE_STATE, ACCESS_NETWORK_STATE
    };

    // returns a string of all the permissions that still need to be granted
    private String[] allPermissions(String[] needed) {
        ArrayList<String> results = new ArrayList<>();
        for (String permission : needed) {
            if (!permissionGranted(permission)) {
                results.add(permission);
            }
        }
        return (results.toArray(new String[results.size()]));
    }

    // returns true if the single permission is granted
    private boolean permissionGranted(String permission) {
        return (ContextCompat.checkSelfPermission(this, permission)) == PackageManager.PERMISSION_GRANTED;
    }

    // returns true if all permissions are granted
    private boolean allPermissionsGranted() {
        return (permissionGranted(RECORD_AUDIO) && permissionGranted(MODIFY_AUDIO_SETTINGS) &&
                permissionGranted(READ_PHONE_STATE) && permissionGranted(ACCESS_NETWORK_STATE) && permissionGranted(CAMERA));
    }

    // checks if every necessary permission is granted & asks for any that are not yet granted
    private void checkAllPermissions() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, allPermissions(appPermissions), MY_PERMISSIONS_GRANTED);
        }
    }

    /*
     * Twilio Section
     */

    private void configureAudio(boolean enable) {
        if (enable) {
            previousAudioMode = audioManager.getMode();
            // Request audio focus before making any device switch
            requestAudioFocus();
            /*
             * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
             * to be in this mode when playout and/or recording starts for the best
             * possible VoIP performance. Some devices have difficulties with
             * speaker mode if this is not set.
             */
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            /*
             * Always disable microphone mute during a WebRTC call.
             */
            previousMicrophoneMute = audioManager.isMicrophoneMute();
            audioManager.setMicrophoneMute(false);
        } else {
            audioManager.setMode(previousAudioMode);
            audioManager.abandonAudioFocus(null);
            audioManager.setMicrophoneMute(previousMicrophoneMute);
        }
    }

    // retreives available camera sources from device
    private CameraCapturer.CameraSource getAvailableCameraSource() {
        return (CameraCapturer.isSourceAvailable(CameraCapturer.CameraSource.FRONT_CAMERA)) ?
                (CameraCapturer.CameraSource.FRONT_CAMERA) :
                (CameraCapturer.CameraSource.BACK_CAMERA);
    }

    // creates local audio & video track
    private void createAudioAndVideoTracks() {
        if (localVideoView == null) {
            localVideoView = new VideoRenderer() {
                @Override
                public void renderFrame(I420Frame frame) {

                }
            };
        }

        recoverView(primaryVideoView);
        recoverView(thumbnailVideoView);
        // Share your microphone
        localAudioTrack = LocalAudioTrack.create(this, true, LOCAL_AUDIO_TRACK_NAME);
        Log.d(TAG, "localAudioTrack, is null???" + Boolean.toString(localAudioTrack == null));

        // Share your camera
        cameraCapturerCompat = new CameraCapturerCompat(this, getAvailableCameraSource());
        localVideoTrack = LocalVideoTrack.create(this,
                true,
                cameraCapturerCompat.getVideoCapturer(),
                LOCAL_VIDEO_TRACK_NAME);
        primaryVideoView.setMirror(true);
        localVideoTrack.addRenderer(primaryVideoView);
        localVideoView = primaryVideoView;
    }

    // makes a call to server for an access token
    private void retrieveAccessTokenfromServer(String myIdentity, final String roomToJoin) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.twilio_function_api));
        stringBuilder.append("?identity=");
        stringBuilder.append(myIdentity);
        stringBuilder.append("?room=");
        stringBuilder.append(roomToJoin);
        String finalurl = stringBuilder.toString();
        Log.d(TAG, finalurl);

        Ion.with(this)
                .load(String.format(finalurl,
                        UUID.randomUUID().toString()))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String token) {
                        if (e == null) {
                            MainActivity.this.accessToken = token;
                            Log.d(TAG, "ACCESS TOKEN RECEIVED = " + accessToken);
                            try {
                                // turn response into token via json object
                                JSONObject jObject = new JSONObject(token);
                                accessToken = jObject.getString("token");
                                // connect to the twilio room now that we have an access token and room
                                connectToRoom(roomToJoin);

                            } catch (JSONException exception) {
                                Log.d(TAG, "JSON EXCEPTION: " + exception);
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    R.string.error_retrieving_access_token, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    // connect to twilio room
    private void connectToRoom(String roomName) {
        configureAudio(true);
        ConnectOptions.Builder connectOptionsBuilder = new ConnectOptions.Builder(accessToken)
                .roomName(roomName);

        /*
         * Add local audio track to connect options to share with participants.
         */
        if (localAudioTrack != null) {
            connectOptionsBuilder.audioTracks(Collections.singletonList(localAudioTrack));
            Log.d(TAG, "localAudioTrack is NOT NULL");
        } else {
            Log.d(TAG, "localAudioTrack is coming up null in connectToRoom");
        }

        /*
         * Add local video track to connect options to share with participants.
         */
        if (localVideoTrack != null) {
            connectOptionsBuilder.videoTracks(Collections.singletonList(localVideoTrack));
        }

        if (audioCodec == null){
            audioCodec = getAudioCodecPreference(PREF_AUDIO_CODEC,
                    PREF_AUDIO_CODEC_DEFAULT);
            Log.d(TAG, "audioCodec coming up as null");
        }
        if (videoCodec == null){
            videoCodec = getVideoCodecPreference(PREF_VIDEO_CODEC,
                    PREF_VIDEO_CODEC_DEFAULT);
            Log.d(TAG, "videoCodec coming up as null");
        }

        /*
         * Set the preferred audio and video codec for media.
         */
        connectOptionsBuilder.preferAudioCodecs(Collections.singletonList(audioCodec));
        connectOptionsBuilder.preferVideoCodecs(Collections.singletonList(videoCodec));

        /*
         * Set the sender side encoding parameters.
         */
        if (encodingParameters == null){
            encodingParameters = getEncodingParameters();
            Log.d(TAG, "encodingParameters coming up as null");
        }
        connectOptionsBuilder.encodingParameters(encodingParameters);

        Log.d(TAG, "is gameVariables null??? " + Boolean.toString(gameVariables == null));
        Log.d(TAG, "is gamevariables room null? " + Boolean.toString(gameVariables.getTwilioRoom() == null));


    gameVariables.setTwilioRoom(Video.connect(this, connectOptionsBuilder.build(), mRoomListener()));

    }

    // moves your video view from the primary full screen view to the thumbnail video view
    private void moveLocalVideoToThumbnailView() {
        recoverView(thumbnailVideoView);
        localVideoTrack.removeRenderer(primaryVideoView);
        Log.d(TAG, "REMOVE RENDERER CALLED ON LOCAL VID VIEW");
        localVideoTrack.addRenderer(thumbnailVideoView);
        localVideoView = thumbnailVideoView;
        thumbnailVideoView.setMirror(cameraCapturerCompat.getCameraSource() ==
                CameraCapturer.CameraSource.FRONT_CAMERA);

    }

    // add other person's video, this also moves the user's video stream to the thumbnail video
    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
        if (videoTrack != null) {
            moveLocalVideoToThumbnailView();
            primaryVideoView.setMirror(false);
            videoTrack.addRenderer(primaryVideoView);
            startGame();
        } else {
            // something went wrong with the incoming video track, disconnect
            disconnectFromPlayRoomAndTwilio();
        }
    }

    /*
     * Called when remote participant joins the room
     */
    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        Log.d(TAG, "ADDREMOTEPARTICIPANT CALLED");
        remoteParticipantIdentity = remoteParticipant.getIdentity();

        /*
         * Add remote participant renderer
         */
        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Only render video tracks that are subscribed to
             */
            Log.d(TAG, "REMOTE PARTICIPANT HAS VIDEO = " + Boolean.toString(remoteParticipant.getRemoteVideoTracks().size() > 0));
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                addRemoteParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }

        } else {
            Log.d(TAG, "REMOTE PARTICIANT HAS NO VIDEO");
        }

        /*
         * Start listening for participant events
         */
        remoteParticipant.setListener(remoteParticipantListener());
    }


    // the remote participant listener callbacks
    private RemoteParticipant.Listener remoteParticipantListener() {
        return new RemoteParticipant.Listener() {
            @Override
            public void onAudioTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i(TAG, String.format("onAudioTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));

            }

            @Override
            public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i(TAG, String.format("onAudioTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));

            }

            @Override
            public void onDataTrackPublished(RemoteParticipant remoteParticipant,
                                             RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i(TAG, String.format("onDataTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));

            }

            @Override
            public void onDataTrackUnpublished(RemoteParticipant remoteParticipant,
                                               RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i(TAG, String.format("onDataTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));

            }

            @Override
            public void onVideoTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i(TAG, String.format("onVideoTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));

            }

            @Override
            public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i(TAG, String.format("onVideoTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
            }

            @Override
            public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               RemoteAudioTrack remoteAudioTrack) {
                Log.i(TAG, String.format("onAudioTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));

            }

            @Override
            public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                 RemoteAudioTrack remoteAudioTrack) {
                Log.i(TAG, String.format("onAudioTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));

            }

            @Override
            public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                       TwilioException twilioException) {
                Log.i(TAG, String.format("onAudioTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));

            }

            @Override
            public void onDataTrackSubscribed(RemoteParticipant remoteParticipant,
                                              RemoteDataTrackPublication remoteDataTrackPublication,
                                              RemoteDataTrack remoteDataTrack) {
                Log.i(TAG, String.format("onDataTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));

            }

            @Override
            public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                RemoteDataTrackPublication remoteDataTrackPublication,
                                                RemoteDataTrack remoteDataTrack) {
                Log.i(TAG, String.format("onDataTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));

            }

            @Override
            public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                      RemoteDataTrackPublication remoteDataTrackPublication,
                                                      TwilioException twilioException) {
                Log.i(TAG, String.format("onDataTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));

            }

            @Override
            public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               RemoteVideoTrack remoteVideoTrack) {
                Log.i(TAG, String.format("onVideoTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
                addRemoteParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                 RemoteVideoTrack remoteVideoTrack) {
                Log.i(TAG, String.format("onVideoTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));

                remoteVideoTrack.removeRenderer(primaryVideoView);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                       TwilioException twilioException) {
                Log.i(TAG, String.format("onVideoTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));

            }

            @Override
            public void onAudioTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }
        };
    }

    private com.twilio.video.Room.Listener mRoomListener() {
        return new com.twilio.video.Room.Listener() {
            @Override
            public void onConnected(com.twilio.video.Room room) {
                localParticipant = room.getLocalParticipant();
                setTitle(room.getName());
                Log.d(TAG, "local participant: " + localParticipant);
                Log.d(TAG, "room name: " + room.getName());
                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                    break;
                }
            }

            @Override
            public void onConnectFailure(com.twilio.video.Room room, TwilioException e) {
                Log.d(TAG, "TWILIO EXCEPTION = " + e);
                Log.d(TAG, "Connection Failure");
                configureAudio(false);

            }

            @Override
            public void onDisconnected(com.twilio.video.Room room, TwilioException e) {
                localParticipant = null;
                if (gameVariables.getGamestate() != GameVariables.Gamestate.GameEnding && gameVariables.getGamestate() != GameVariables.Gamestate.GameContinued) {
                    endGame(true);
                }
            }

            @Override
            public void onParticipantConnected(com.twilio.video.Room room, RemoteParticipant remoteParticipant) {
                addRemoteParticipant(remoteParticipant);
            }

            @Override
            public void onParticipantDisconnected(com.twilio.video.Room room, RemoteParticipant remoteParticipant) {
            Log.d(TAG, "onParticipantDisconnected Called!");
                gameVariables.setPlayerDisconnected(true);
                endGame(true);
            }

            @Override
            public void onRecordingStarted(com.twilio.video.Room room) {
                /*
                 * Indicates when media shared to a Room is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d(TAG, "onRecordingStarted");
            }

            @Override
            public void onRecordingStopped(com.twilio.video.Room room) {
                /*
                 * Indicates when media shared to a Room is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d(TAG, "onRecordingStopped");
            }
        };
    }


    // initilization for audio and video
    private void audioVideoSetup() {
// Get AudioManager
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        // Route audio through speaker or headset - true = speaker false = headset
        requestAudioFocus();
        // Render camera to a view
    }

    // request audio focus for the app
    private void requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            AudioFocusRequest focusRequest =
                    new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(
                                    new AudioManager.OnAudioFocusChangeListener() {
                                        @Override
                                        public void onAudioFocusChange(int i) {
                                        }
                                    })
                            .build();
            audioManager.requestAudioFocus(focusRequest);
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    private EncodingParameters getEncodingParameters() {
        final int maxAudioBitrate = Integer.parseInt(
                preferences.getString(PREF_SENDER_MAX_AUDIO_BITRATE,
                        PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT));
        final int maxVideoBitrate = Integer.parseInt(
                preferences.getString(PREF_SENDER_MAX_VIDEO_BITRATE,
                        PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT));

        return new EncodingParameters(maxAudioBitrate, maxVideoBitrate);
    }

    /*
     * Get the preferred video codec from shared preferences
     */
    private VideoCodec getVideoCodecPreference(String key, String defaultValue) {
        final String videoCodecName = preferences.getString(key, defaultValue);

        switch (videoCodecName) {
            case Vp8Codec.NAME:
                return new Vp8Codec();
            case H264Codec.NAME:
                return new H264Codec();
            case Vp9Codec.NAME:
                return new Vp9Codec();
            default:
                return new Vp8Codec();
        }
    }

    /*
     * Get the preferred audio codec from shared preferences
     */
    private com.twilio.video.AudioCodec getAudioCodecPreference(String key, String defaultValue) {
        final String audioCodecName = preferences.getString(key, defaultValue);

        switch (audioCodecName) {
            case IsacCodec.NAME:
                return new IsacCodec();
            case OpusCodec.NAME:
                return new OpusCodec();
            case PcmaCodec.NAME:
                return new PcmaCodec();
            case PcmuCodec.NAME:
                return new PcmuCodec();
            case G722Codec.NAME:
                return new G722Codec();
            default:
                return new OpusCodec();
        }
    }

    /*
     * UTILITY SECTION
     */



    // keep the screen on
    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // stop keeping the screen on
    private void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // method to make a toast with the string input as the toast
    private void makeToast(String toastInput) {
        Toast.makeText(this, toastInput, Toast.LENGTH_SHORT).show();
    }

    // log a new game then send the room to the other player
    private void logAndSendNewGame(final String roomId) {
        serverTime.getTime(new ServerTime.OnTimeRetrievedListener() {
            @Override
            public void onTimeRetrieved(final Long timestamp) {
                String userId = myFBId;
                Game game = new Game(userId, timestamp);
                DatabaseReference newGameReference = myRef.child(GAMES_REFERENCE).push();
                Log.d(TAG, "newGameReference to String = " + newGameReference.getKey());
                newGameReference.setValue(game);
                gameVariables.setLastGameNumber(newGameReference.getKey());
                myRef.child(USER_GAMES_REFERENCE).child(userId).child(gameVariables.getLastGameNumber()).setValue(timestamp);
                broadcastMessage(sendMyRoom(roomId, newGameReference.getKey(), timestamp, CHANGED_GAME_INFO, gameVariables));
            }
        });
    }

    // gameinfo received from realtimemessagelistener: unpack it, add it to firebase, and join the room
    private void receiveGameInfoAndJoinRoom(final GameInfo gameInfo) {
        Log.d(TAG, "receiveGameInfoAndJoin room, room to join = " + gameInfo.getRoomId());
        String userId = myFBId;
        myRef.child(GAMES_REFERENCE).child(gameInfo.getGameReference()).child(PLAYER_TWO_REFERENCE).setValue(userId);
        myRef.child(USER_GAMES_REFERENCE).child(userId).child(gameInfo.getGameReference()).setValue(gameInfo.getTime());
        gameVariables.setLastGameNumber(gameInfo.getGameReference());
        gameVariables.setRoomToJoin(gameInfo.getRoomId());
        makeVideoRoomTokenAndJoin(false);
    }

    /*
    // firebase listeners
     */
    ValueEventListener freeTokenListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null && mUser != null) {
                int tickets = Utility.safeObjectToInt(dataSnapshot.getValue());
                mUser.freeTokens = tickets;
                ticketManager.setCurrentFreeTokensInternal(tickets);
                updateUserUi();
            } else {
                Log.d(TAG, "Error updating free tokens");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ValueEventListener paidTokenListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null && mUser != null) {
                int tickets = Utility.safeObjectToInt(dataSnapshot.getValue());
                mUser.paidTokens = tickets;
                ticketManager.setCurrentPaidTokensInternal(tickets);
                updateUserUi();
            } else {
                Log.d(TAG, "Error updating paid tokens");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ValueEventListener timeTokenUsedListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null && mUser != null) {
                mUser.timeTokenUsed = dataSnapshot.getValue();
                ticketManager.setTimeTokenUsedInternal(dataSnapshot.getValue());
            } else {
                Log.d(TAG, "Error updating free token timestamp");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };


    private boolean getAudioState() {
        AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
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

    public boolean isAcquireFragmentShown() {
        return acquireFragment != null && acquireFragment.isVisible();
    }


    private void startBillingManager(final Activity activity){
        dataRetrieval = new DataRetrieval(myRef);
        dataRetrieval.retrieve(new DataRetrieval.OnRetrievedListener() {
            @Override
            public void onRetrieved(String payload) {

                if (mBillingHelperRedorBlack == null){
                    mBillingHelperRedorBlack = new BillingHelperRedorBlack(MainActivity.this, myRef, myFBId);
                    Log.d(TAG, "billinghelperredorblack coming up null");
                } else if (mBillingHelperRedorBlack.getUpdateListener() == null){
                    Log.d(TAG, "billinghelperredorblack GETUPDATELISTENER coming up null");
                } else if (getResources().getString(R.string.base_sixty_key) == null){
                    Log.d(TAG, "getResources.getString coming up null");
                } else if (payload == null){
                    Log.d(TAG, "PAYLOAD coming up null");
                } else if (activity == null){
                    Log.d(TAG, "ACTIVITY coming up null");
                }else {
                    Log.d(TAG, "nothing coming up null for creating billing manager...");
                    mBillingManager = new BillingManager(activity, mBillingHelperRedorBlack.getUpdateListener(), getResources().getString(R.string.base_sixty_key), payload);
                }



            }
        });
    }

    MessagedReceivedListener messagedReceivedListener = new MessagedReceivedListener() {
        @Override
        public void onColorReceived(int color) {

            Log.d(TAG, "onColorReceived, opponent color = " + Integer.toString(color) + " RED = 0, BLACK = 1, NONE = -1");
        gameVariables.setOpponentChoice(color);
        }

        @Override
        public void onWhoPicksFirstReceived(boolean iPickFirst) {
        gameVariables.setFirstToPick(!iPickFirst);
        Log.d(TAG, "onWhoPicksFirstReceived: i pick = " + Boolean.toString(!iPickFirst));
        if (!iPickFirst){
            makeToast(getString(R.string.i_pick_first));
            recoverButtons();
        }
        }
        @Override
        public void onOpponentWantsToContinueReceived(boolean opponentWantsToContinue, int paying) {
        gameVariables.setOpponentWantsToContinue(opponentWantsToContinue);
        Log.d(TAG, "opponent wants to continue? = " + Boolean.toString(opponentWantsToContinue));
        if (paying == I_AM_PAYING){
            gameVariables.setPayingForGameContinue(OPPONENT_PAYING);
            setContinueCallTextFree();
        }
        }

        @Override
        public void onGameInfoReceived(GameInfo gameInfo) {
        receiveGameInfoAndJoinRoom(gameInfo);
        Log.d(TAG, "onGameInfoReceived");
        }

        /*
        @Override
        public void onWhoIsPayingReceived(int paying) {
            if (paying == I_AM_PAYING){
                setContinueCallTextFree();
                 gameVariables.setPayingForGameContinue(OPPONENT_PAYING);
            } else if (paying == NO_ONE_PAYING){
                Log.d(TAG, "paying for game = " + Integer.toString(paying));
            }

        Log.d(TAG, "onWhoisPayingReceived: paying = " + (paying == OPPONENT_PAYING ? "opponent" : "me"));
        }*/
    };

    public void showRefreshedUi() {
        setWaitScreen(false);
        if (acquireFragment != null) {
            acquireFragment.refreshUI();
        }
    }
    private void setWaitScreen(boolean set) {
        mainScreen.setVisibility(set ? View.GONE : View.VISIBLE);
        mScreenWait.setVisibility(set ? View.VISIBLE : View.GONE);
    }

    private void setContinueCallTextFree(){
        if (continueCallText != null){
            continueCallText.setText(getResources().getString(R.string.continue_call_free));
        }
    }
    private void setContinueCallText(){
        if (continueCallText != null){
            continueCallText.setText(getResources().getString(R.string.continue_call));
        }
    }
    public void onBillingManagerSetupFinished() {
        if (acquireFragment != null) {
            acquireFragment.onManagerReady(this);
        }
    }

}

