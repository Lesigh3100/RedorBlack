package com.kevin.android.redorblack.dataclasses;

import android.support.annotation.Keep;

import java.io.Serializable;
@Keep
public class GameInfo implements Serializable{

private String roomId;
private String gameReference;
private Object time;

    public GameInfo(String roomId, String gameReference, Object time) {
        this.roomId = roomId;
        this.gameReference = gameReference;
        this.time = time;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Object getTime() {
        return time;
    }

    public String getGameReference() {
        return gameReference;
    }
}
