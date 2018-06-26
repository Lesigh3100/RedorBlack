package com.kevin.android.redorblack.utility;

import android.support.annotation.Keep;

import com.kevin.android.redorblack.R;
import com.luolc.emojirain.EmojiRainLayout;

import java.util.Random;


public class EmojiDropper {
    @Keep
   private EmojiRainLayout emojiRain;

    // constructor
public EmojiDropper(EmojiRainLayout emojiRainLayout){
emojiRain = emojiRainLayout;
    emojiRain.setPer(15);
    emojiRain.setDuration(1500);
    emojiRain.setDropDuration(1500);
    emojiRain.setDropFrequency(300);
}

public void winningEmojiDrop(){
for (int i = 0; i < 5; i++){
    int resId = WinningEmojis.getRandomEmoji().imageId;
    emojiRain.addEmoji(resId);
}
emojiRain.startDropping();
    emojiRain.clearEmojis();
}
public void losingEmojiDrop(){
    for (int i = 0; i < 5; i++){
        int resId = LosingEmojis.getRandomEmoji().imageId;
        emojiRain.addEmoji(resId);
    }
    emojiRain.startDropping();
    emojiRain.clearEmojis();
}


   public enum WinningEmojis {
       win1(R.drawable.emoji_win1),
       win2(R.drawable.emoji_win2),
       win3(R.drawable.emoji_win3),
       win4(R.drawable.emoji_win4),
       win5(R.drawable.emoji_win5),
       win6(R.drawable.emoji_win6),
       win7(R.drawable.emoji_win7),
       win8(R.drawable.emoji_win8),
       win9(R.drawable.emoji_win9),
       win11(R.drawable.emoji_win11),
       win12(R.drawable.emoji_win12),
       win13(R.drawable.emoji_win13),
       win14(R.drawable.emoji_win14),
       win15(R.drawable.emoji_win15),
       win16(R.drawable.emoji_win16),
       win17(R.drawable.emoji_win17),
       win18(R.drawable.emoji_win18),
       win19(R.drawable.emoji_win19),
       win20(R.drawable.emoji_win20),
       win21(R.drawable.emoji_win21),
       win22(R.drawable.emoji_win22),
       win23(R.drawable.emoji_win23),
       win24(R.drawable.emoji_win24),
       win25(R.drawable.emoji_win25),
       win26(R.drawable.emoji_win26);
       
       private int imageId;

       WinningEmojis (int drawableId){
           this.imageId = drawableId;
       }
       public static WinningEmojis getRandomEmoji(){
           Random random = new Random();
           return values()[random.nextInt(values().length)];
       }
   }

   public enum LosingEmojis{
       lose1(R.drawable.emoji_lose1),
       lose2(R.drawable.emoji_lose2),
       lose3(R.drawable.emoji_lose3),
       lose4(R.drawable.emoji_lose4),
       lose5(R.drawable.emoji_lose5),
       lose6(R.drawable.emoji_lose6),
       lose7(R.drawable.emoji_lose7),
       lose8(R.drawable.emoji_lose8),
       lose9(R.drawable.emoji_lose9),
       lose10(R.drawable.emoji_lose10),
       lose11(R.drawable.emoji_lose11),
       lose12(R.drawable.emoji_lose12),
       lose13(R.drawable.emoji_lose13),
       lose14(R.drawable.emoji_lose14),
       lose15(R.drawable.emoji_lose15),
       lose16(R.drawable.emoji_lose16),
       lose17(R.drawable.emoji_lose17),
       lose18(R.drawable.emoji_lose18),
       lose19(R.drawable.emoji_lose19),
       lose20(R.drawable.emoji_lose20),
       lose21(R.drawable.emoji_lose21);
       private int imageId;
       LosingEmojis (int drawableId){
           this.imageId = drawableId;
       }
       public static LosingEmojis getRandomEmoji(){
           Random random = new Random();
           return values()[random.nextInt(values().length)];
       }
   }


}
