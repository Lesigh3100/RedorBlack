package com.kevin.android.redorblack.utility;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.Keep;
import android.util.Log;

import com.kevin.android.redorblack.R;

import static android.content.Context.AUDIO_SERVICE;

@Keep
public class SoundEffects {

   private Context mContext;
   private MediaPlayer mediaPlayer;
   String TAG = "SOUND EFFECTS ";

   // enum for volume levels to be passed into our play method for easy control of volume
   public enum VolumeLevel {
       VOLUME_LOUD,
       VOLUME_MEDIUM,
       VOLUME_QUIET
   }

   public void endGameSound(Boolean win){
       int random = (int)(Math.random()*3) + 1;
       int effectsId = 0;
       Log.d(TAG, "RANDOM =" + Integer.toString(random));
       switch (random){
           case 1:
               if (win){
                   effectsId = R.raw.win1;
               } else {
                   effectsId = R.raw.lose1;
               }
               break;
           case 2:
               if (win){
                   effectsId = R.raw.win2;
               } else {
                   effectsId = R.raw.lose2;
               }
               break;
           case 3:
               if (win){
                   effectsId = R.raw.win3;
               } else {
                   effectsId = R.raw.lose3;
               }
               break;
               default:
                   break;
       }
       if (effectsId != 0){
           Log.d(TAG, "EFFECT ID = " + Integer.toString(effectsId));
           playSound(effectsId);
       }
   }

    public void playSound(int soundId){
       mediaPlayer = MediaPlayer.create(mContext, soundId);
       mediaPlayer.setVolume(3, 3);
       mediaPlayer.start();
       mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
           @Override
           public void onCompletion(MediaPlayer mp) {
              if (mp != null && !mp.isPlaying()){
                  mp.release();
              }
           }
       });
    }

    public void cleanUpSoundEffects(){
       if (mediaPlayer != null){
           mediaPlayer.release();
       }
    }

// sound effect constructor passing context
    public SoundEffects (Context context){
        mContext = context;
    }
}
