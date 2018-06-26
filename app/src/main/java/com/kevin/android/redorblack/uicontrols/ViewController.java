package com.kevin.android.redorblack.uicontrols;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.kevin.android.redorblack.R;
import com.kevin.android.redorblack.twilio.CameraCapturerCompat;
import com.kevin.android.redorblack.utility.Animations;
import com.kevin.android.redorblack.utility.EmojiDropper;
import com.kevin.android.redorblack.utility.ReportChoiceListener;
import com.luolc.emojirain.EmojiRainLayout;
import com.twilio.video.CameraCapturer;
import com.twilio.video.I420Frame;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.VideoRenderer;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;

import static com.kevin.android.redorblack.constants.FirebaseConstants.CODE_ABUSE;
import static com.kevin.android.redorblack.constants.FirebaseConstants.CODE_NUDITY;
import static com.kevin.android.redorblack.constants.GameConstants.LOCAL_VIDEO_TRACK_NAME;

public class ViewController implements UIListener {

    private final String TAG = "ViewController";
    private int currentScreen = -1;
    // string format for tokens
    final String format = String.format("%%0%dd", 2);
    private Context context;
    private View.OnClickListener onClickListener;
    private EmojiDropper emojiDropper;
    private Animations animations;
    private ReportChoiceListener reportChoiceListener;

    // Twilio items
    private com.twilio.video.VideoRenderer localVideoView;
    private CameraCapturerCompat cameraCapturerCompat;
    public LocalVideoTrack localVideoTrack;

    // This array lists all the individual screens our game has.
    final static int[] SCREENS = {
            R.id.main_screen, R.id.sign_in_screen, R.id.invitation_popup,
            R.id.screen_wait
    };
    enum ScreenState{
        screen_login,
        screen_main,
        screen_playing
    }
    ScreenState screenState;

    // My Views
    private EmojiRainLayout emojiRainLayout;
    private ImageView downArrowBlackButton;
    private  ImageView downArrowRedButton;
    private ImageButton stopCallButton;
    private ImageButton continueCallButton;
    private ImageButton reportLastUserButton;
    private ImageButton signOutButton;
    private ImageButton playButton;
    private Button blackButton;
    private Button redButton;
    private SignInButton signInButton;
    private VideoView primaryVideoView;
    private VideoView thumbnailVideoView;
    private TextView reportText;
    private TextView playText;
    private TextView redText;
    private TextView orText;
    private TextView blackText;
    private TextView freeTokenCountdownClock;
    private TextView freeTicketsLeft;
    private TextView paidTicketsLeft;
    private TextView timeCountDownClock;
    private TextView continueCallText;
    private RelativeLayout signInScreen;
    private RelativeLayout mainScreen;
    private LinearLayout redOrBlackTextLayout;

    View mScreenWait, mScreenMain;

    private boolean shouldShowReportButton = false;



    public ViewController(Context context, View.OnClickListener onClickListener, ReportChoiceListener reportChoiceListener) {
    this.context = context;
    this.onClickListener = onClickListener;
    initializeViews();
    emojiDropper = new EmojiDropper(emojiRainLayout);
    animations = new Animations(context, redOrBlackTextLayout, redText, blackText);
    this.reportChoiceListener = reportChoiceListener;
    screenState = ScreenState.screen_login;
    }

    @Override
    public void onSwitchToMainScreen(boolean shouldShowReportButton) {
    this.shouldShowReportButton = shouldShowReportButton;
    switchToMainScreen();
    screenState = ScreenState.screen_main;
    cleanUpTracks();
    }

    @Override
    public void onSwitchToLoginScreen() {
    switchToLoginScreen();
    screenState = ScreenState.screen_login;
    }

    @Override
    public void onSwitchToPlayingScreen() {
    switchToPlayingScreen();
    screenState = ScreenState.screen_playing;
    }
    @Override
    public void onShowWaitingRoom() {

    }

    @Override
    public void onShowWaitingScreen(boolean bool) {
        mainScreen.setVisibility(bool ? View.GONE : View.VISIBLE);
        mScreenWait.setVisibility(bool ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onShowLoadingScreen() {

    }

    @Override
    public void onPushedRedButton() {
        pushedRedButton();
    }

    @Override
    public void onPushedBlackButton() {
        pushedBlackButton();
    }

    @Override
    public void onDismissButtons() {
       dismissButtons();
    }

    @Override
    public void onRecoverButtons() {
        recoverButtons();
    }

    @Override
    public void onMakeToast(String toast) {
        makeToast(toast);
    }

    @Override
    public void onDropEmojis(boolean winning) {
        if (winning){
        emojiDropper.winningEmojiDrop();
        } else {
        emojiDropper.losingEmojiDrop();
        }
    }

    @Override
    public void onDismissReportButton() {
        dismissView(reportLastUserButton);
        dismissView(reportText);
    }

    @Override
    public void onDismissContinueCallButton() {
        dismissView(continueCallButton);
        dismissView(continueCallText);
    }

    @Override
    public void onInflateReportChoices() {
        PopupMenu popupMenu = new PopupMenu(context, reportLastUserButton);
        popupMenu.getMenuInflater().inflate(R.menu.menu_report, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_abusive:
                        reportChoiceListener.onReportResult(CODE_ABUSE);
                        onDismissReportButton();
                        break;
                    case R.id.menu_nudity:
                        reportChoiceListener.onReportResult(CODE_NUDITY);
                        onDismissReportButton();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onUpdateUserUi(int freetickets, int paidtickets) {
        freeTicketsLeft.setText(String.format(format, freetickets));
        paidTicketsLeft.setText(String.format(format, paidtickets));
    }

    @Override
    public void onRecoverStopCallButtonAndTimeCountDownClock() {
        recoverView(stopCallButton);
        recoverView(timeCountDownClock);
    }

    @Override
    public void onRecoverContinueCallButton(String text) {
        recoverView(continueCallButton);
        recoverView(continueCallText);
        continueCallText.setText(text);
    }

    @Override
    public void onUpdateCountDownClock(int currentTime) {
        timeCountDownClock.setText("0:" + (currentTime < 10 ? "0" : "") + Integer.toString(currentTime));
    }

    @Override
    public void onDisplayVideoViews(CameraCapturerCompat cameraCapturerCompatIncoming) {
        this.cameraCapturerCompat = cameraCapturerCompatIncoming;
        createAudioAndVideoTracks(cameraCapturerCompat);
    }

    @Override
    public void onViewControllerAddRemoteParticipantVideo(VideoTrack videoTrack) {
        addRemoteParticipantVideo(videoTrack);
    }

    @Override
    public void onRemoveVideoTrack(RemoteVideoTrack remoteVideoTrack) {
        remoteVideoTrack.removeRenderer(primaryVideoView);
    }

    @Override
    public void onFreeTokenCountDownTextUpdate(int timeLeft) {
       setFreeTokenCountdownClockText(timeLeft);
    }

    @Override
    public void onSignInFailure() {
        Snackbar.make(signInScreen, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressedAlertDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(context.getResources().getString(R.string.do_you_want_to_exit));
        builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity)context).finish();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onPurchaseFinished() {
        onSwitchToMainScreen(false);
    }

    // Initialization for the views
    private void initializeViews() {
        blackButton = ((Activity)context).findViewById(R.id.black_button);
        redButton = ((Activity)context).findViewById(R.id.red_button);
        downArrowBlackButton = ((Activity)context).findViewById(R.id.black_button_arrow);
        downArrowRedButton = ((Activity)context).findViewById(R.id.red_button_arrow);
        playButton = ((Activity)context).findViewById(R.id.play_button);
        playText = ((Activity)context).findViewById(R.id.play_text);
        signInButton = ((Activity)context).findViewById(R.id.sign_in_button);
        mainScreen = ((Activity)context).findViewById(R.id.main_screen);
        signInScreen = ((Activity)context).findViewById(R.id.sign_in_screen);
        redText = ((Activity)context).findViewById(R.id.red_text);
        blackText = ((Activity)context).findViewById(R.id.black_text);
        orText = ((Activity)context).findViewById(R.id.or_text);
        reportText = ((Activity)context).findViewById(R.id.report_user_text);
        freeTicketsLeft = ((Activity)context).findViewById(R.id.ticket_free_count);
        paidTicketsLeft = ((Activity)context).findViewById(R.id.ticket_paid_count);
        freeTokenCountdownClock = ((Activity)context).findViewById(R.id.token_countdown_clock);
        redOrBlackTextLayout = ((Activity)context).findViewById(R.id.red_or_black_text_layout);
        stopCallButton = ((Activity)context).findViewById(R.id.stop_call_button);
        continueCallButton = ((Activity)context).findViewById(R.id.continue_call_button);
        continueCallText = ((Activity)context).findViewById(R.id.continue_call_text);
        emojiRainLayout = ((Activity)context).findViewById(R.id.group_emoji_container);
        timeCountDownClock = ((Activity)context).findViewById(R.id.countdown_clock);
        primaryVideoView = ((Activity)context).findViewById(R.id.videoview);
        thumbnailVideoView = ((Activity)context).findViewById(R.id.thumbnail_video_view);
        reportLastUserButton = ((Activity)context).findViewById(R.id.report_user_button);
        signOutButton = ((Activity)context).findViewById(R.id.sign_out_button);
        mScreenWait = ((Activity)context).findViewById(R.id.screen_wait);
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
        if (shouldShowReportButton) {
            recoverView(reportLastUserButton);
            recoverView(reportText);
        }
        // start color changing animation
        animations.colorChanges();
    }
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

    // make a view invisible
    private void dismissView(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    // make a view visible
    private void recoverView(View view) {
        view.setVisibility(View.VISIBLE);
    }


    private void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            ((Activity)context).findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
        currentScreen = screenId;
    }

    // add this to include invitations
        /*
        // should we show the invitation popup?
        boolean showInvPopup = true;
        if (mIncomingInvitationId == null) {
            // no invitation, so no popup
            showInvPopup = false;
        } else if (mMultiplayer) {
            // if in multiplayer, only show invitation on main screen
            showInvPopup = (currentScreen == R.id.main_screen);
        }
        ((Activity)context).findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
        */

    private void setWaitScreen(boolean set) {
        mainScreen.setVisibility(set ? View.GONE : View.VISIBLE);
        mScreenWait.setVisibility(set ? View.VISIBLE : View.GONE);
    }

        private void pushedRedButton(){
            dismissView(blackButton);
            dismissView(downArrowBlackButton);
            dismissView(downArrowRedButton);
            redButton.setClickable(false);
        }

        private void pushedBlackButton(){
            dismissView(downArrowBlackButton);
            dismissView(downArrowRedButton);
            dismissView(redButton);
            blackButton.setClickable(false);
        }
    public TextView getFreeTokenCountdownClock() {
        return freeTokenCountdownClock;
    }


    private void cleanUpTracks(){
        if (localVideoTrack != null){
            localVideoTrack.release();
        }
    }

    // creates local audio & video track
    private void createAudioAndVideoTracks(CameraCapturerCompat cameraCapturerCompat) {
        if (localVideoView == null) {
            localVideoView = new VideoRenderer() {
                @Override
                public void renderFrame(I420Frame frame) {

                }
            };
        }
        recoverView(primaryVideoView);
        recoverView(thumbnailVideoView);

        localVideoTrack = LocalVideoTrack.create(context,
                true,
                cameraCapturerCompat.getVideoCapturer(),
                LOCAL_VIDEO_TRACK_NAME);
        primaryVideoView.setMirror(true);
        localVideoTrack.addRenderer(primaryVideoView);
        localVideoView = primaryVideoView;
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
        }
    }

    public void recreateLocalVideoTrack(){
        localVideoTrack = LocalVideoTrack.create(context,
                true,
                cameraCapturerCompat.getVideoCapturer(),
                LOCAL_VIDEO_TRACK_NAME);
        localVideoTrack.addRenderer(localVideoView);
    }

    public LocalVideoTrack getLocalVideoTrack(){
        return localVideoTrack;
    }

    private void setFreeTokenCountdownClockText(int timeLeft){
        if (timeLeft <= 0){
            freeTokenCountdownClock.setText("");
        } else {
            freeTokenCountdownClock.setText(Integer.toString(timeLeft));
        }
    }
    private void makeToast(String toastInput) {
        Toast.makeText(context, toastInput, Toast.LENGTH_SHORT).show();
    }

    private ScreenState getScreenState(){
        return screenState;
    }

    public void checkIfWeHaveCorrectScreen(){
        switch (getScreenState()){
            case screen_main:
            switchToMainScreen();
                break;
            case screen_login:
                switchToLoginScreen();
            case screen_playing:
                switchToPlayingScreen();
        }
    }

}
