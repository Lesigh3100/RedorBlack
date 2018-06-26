package com.kevin.android.redorblack.uicontrols;

import com.kevin.android.redorblack.twilio.CameraCapturerCompat;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.VideoTrack;

public interface UIListener {

    void onShowWaitingRoom();

    void onShowWaitingScreen(boolean bool);

    void onSwitchToMainScreen(boolean shouldShowReportButton);

    // switchToScreen(R.id.screen_main);
    void onSwitchToLoginScreen();

    void onShowLoadingScreen();
    //   switchToScreen(R.id.screen_wait);

    void onSwitchToPlayingScreen();

    void onDropEmojis(boolean winning);

    void onPushedRedButton();

    void onPushedBlackButton();

    void onRecoverButtons();

    void onDismissButtons();

    void onDismissReportButton();

    void onDismissContinueCallButton();

    void onInflateReportChoices();

    void onUpdateUserUi(int freetickets, int paidtickets);

    void onRecoverStopCallButtonAndTimeCountDownClock();

    void onUpdateCountDownClock(int currentTime);

    void onRecoverContinueCallButton(String text);

    void onDisplayVideoViews(CameraCapturerCompat cameraCapturerCompat);

    void onViewControllerAddRemoteParticipantVideo(VideoTrack videoTrack);

    void onRemoveVideoTrack(RemoteVideoTrack remoteVideoTrack);

    void onFreeTokenCountDownTextUpdate(int timeLeft);

    void onSignInFailure();

    void onBackPressedAlertDialogue();

    void onMakeToast(String toast);

    void onPurchaseFinished();

}
