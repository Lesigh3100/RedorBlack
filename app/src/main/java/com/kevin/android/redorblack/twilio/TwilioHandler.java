package com.kevin.android.redorblack.twilio;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.kevin.android.redorblack.MainActivity;
import com.kevin.android.redorblack.R;
import com.kevin.android.redorblack.uicontrols.ViewController;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.EncodingParameters;
import com.twilio.video.G722Codec;
import com.twilio.video.H264Codec;
import com.twilio.video.IsacCodec;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
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
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoCodec;
import com.twilio.video.VideoTrack;
import com.twilio.video.Vp8Codec;
import com.twilio.video.Vp9Codec;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.Collections;
import java.util.UUID;

import static android.media.AudioManager.MODE_IN_COMMUNICATION;
import static com.kevin.android.redorblack.constants.GameConstants.LOCAL_AUDIO_TRACK_NAME;
import static com.kevin.android.redorblack.twilio.TwilioConstants.PREF_AUDIO_CODEC;
import static com.kevin.android.redorblack.twilio.TwilioConstants.PREF_AUDIO_CODEC_DEFAULT;
import static com.kevin.android.redorblack.twilio.TwilioConstants.PREF_SENDER_MAX_AUDIO_BITRATE;
import static com.kevin.android.redorblack.twilio.TwilioConstants.PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT;
import static com.kevin.android.redorblack.twilio.TwilioConstants.PREF_SENDER_MAX_VIDEO_BITRATE;
import static com.kevin.android.redorblack.twilio.TwilioConstants.PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT;
import static com.kevin.android.redorblack.twilio.TwilioConstants.PREF_VIDEO_CODEC;
import static com.kevin.android.redorblack.twilio.TwilioConstants.PREF_VIDEO_CODEC_DEFAULT;

public class TwilioHandler {
    private final String TAG = "TwilioHandler";
    private com.twilio.video.AudioCodec audioCodec;
    private VideoCodec videoCodec;


    public EncodingParameters encodingParameters;
    private CameraCapturerCompat cameraCapturerCompat;
    private String remoteParticipantIdentity;
    CameraCapturer cameraCapturer;

    private LocalAudioTrack localAudioTrack;
    private AudioManager audioManager;
    private int previousAudioMode;
    private boolean previousMicrophoneMute;

    // room needs

    private String accessToken;

    private Room room;

    private LocalParticipant localParticipant;

    private ViewController viewController;

    private MainActivity mainActivity;

    private TwilioListener twilioListener;

    private SharedPreferences preferences;

    public TwilioHandler(ViewController viewController, MainActivity mainActivity, TwilioListener twilioListener) {
        this.viewController = viewController;
        this.mainActivity = mainActivity;
        preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        this.twilioListener = twilioListener;
        initilizeCodecAndEncoding();
        audioSetup();
    }

    private void initilizeCodecAndEncoding() {
        audioCodec = getAudioCodecPreference(PREF_AUDIO_CODEC,
                PREF_AUDIO_CODEC_DEFAULT);
        videoCodec = getVideoCodecPreference(PREF_VIDEO_CODEC,
                PREF_VIDEO_CODEC_DEFAULT);

        encodingParameters = getEncodingParameters();
    }

    public void checkIfInRoom(){

        final EncodingParameters newEncodingParameters = getEncodingParameters();
        encodingParameters = newEncodingParameters;
        if (viewController.getLocalVideoTrack() == null) {
            /*
             * If connected to a Room then share the local video track.
             */
            if (localParticipant != null) {
                if (audioManager == null){
                audioSetup();
                }
                viewController.recreateLocalVideoTrack();

                localParticipant.publishTrack(viewController.getLocalVideoTrack());
                /*
                 * Update encoding parameters if they have changed.
                 */
                if (!newEncodingParameters.equals(encodingParameters)) {
                    localParticipant.setEncodingParameters(newEncodingParameters);
                }
            }
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

    // set up audio for video chat
    private void audioSetup() {
        audioManager = (AudioManager) (mainActivity).getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null){
            audioManager.setSpeakerphoneOn(true);
            // possible error just added
            audioManager.setMode(MODE_IN_COMMUNICATION);
        }
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
            audioManager.setMode(MODE_IN_COMMUNICATION);
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
    public void createAudioAndVideoTracks() {
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);
        viewController.onDisplayVideoViews(new CameraCapturerCompat(mainActivity, getAvailableCameraSource()));
        localAudioTrack = LocalAudioTrack.create(mainActivity, true, LOCAL_AUDIO_TRACK_NAME);
        Log.d(TAG, "localAudioTrack, is null???" + Boolean.toString(localAudioTrack == null));
    }

    // makes a call to server for an access token
    public void retrieveAccessTokenfromServer(String myIdentity, final String roomToJoin) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mainActivity.getString(R.string.twilio_function_api));
        stringBuilder.append("?identity=");
        stringBuilder.append(myIdentity);
        stringBuilder.append("?room=");
        stringBuilder.append(roomToJoin);
        String finalurl = stringBuilder.toString();
        Log.d(TAG, finalurl);

        Ion.with(mainActivity)
                .load(String.format(finalurl,
                        UUID.randomUUID().toString()))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String token) {
                        if (e == null) {
                            accessToken = token;
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
                            Toast.makeText(mainActivity,
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
            twilioListener.onErrorConnecting();
            return;
        }

        /*
         * Add local video track to connect options to share with participants.
         */
        if (viewController.getLocalVideoTrack() != null) {
            connectOptionsBuilder.videoTracks(Collections.singletonList(viewController.getLocalVideoTrack()));
        } else {
            Log.d(TAG, "localVideoTrack is coming up null in connectToRoom");
            twilioListener.onErrorConnecting();
            return;
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

        room = Video.connect(mainActivity, connectOptionsBuilder.build(), roomListener());
    // twilioListener.onCreatedVideoChatRoom(Video.connect(mainActivity, connectOptionsBuilder.build(), roomListener()));
     //   gameVariables.setTwilioRoom(Video.connect(this, connectOptionsBuilder.build(), mRoomListener()));

    }


    // add other person's video, this also moves the user's video stream to the thumbnail video
    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
        if (videoTrack != null) {
            viewController.onViewControllerAddRemoteParticipantVideo(videoTrack);
            twilioListener.onRemoteParticipantConnected();
        } else {
            // something went wrong with the incoming video track, disconnect
            twilioListener.onErrorConnecting();
        }
    }

    /*
     * Called when remote participant joins the room
     */
    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        Log.d(TAG, "ADD REMOTE PARTICIPANT CALLED");
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

                viewController.onRemoveVideoTrack(remoteVideoTrack);
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

    private com.twilio.video.Room.Listener roomListener() {
        return new com.twilio.video.Room.Listener() {
            @Override
            public void onConnected(com.twilio.video.Room room) {
                localParticipant = room.getLocalParticipant();
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
                twilioListener.onIWasDisconnected();
            }

            @Override
            public void onParticipantConnected(com.twilio.video.Room room, RemoteParticipant remoteParticipant) {
                addRemoteParticipant(remoteParticipant);
            }

            @Override
            public void onParticipantDisconnected(com.twilio.video.Room room, RemoteParticipant remoteParticipant) {
                Log.d(TAG, "onParticipantDisconnected Called!");
                twilioListener.onOtherPersonDisconnected();
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

    public void disconnectFromTwilioRoom(){
        if (room != null){
            room.disconnect();
        }
    }
    public void releaseAudioTrack(){
        if (localAudioTrack != null){
            localAudioTrack.release();
        }
    }

}
