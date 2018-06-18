package com.kevin.android.redorblack.dataclasses;

import android.support.annotation.Keep;

@Keep
public class Game {

    public String player1;
    public String player2;
    public Object time;

    public Game(String player1, Object time) {
        this.player1 = player1;

    }

    public Game(){}

    public Object getTime() {
        return time;
    }
}
