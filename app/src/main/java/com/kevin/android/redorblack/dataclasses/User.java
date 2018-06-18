package com.kevin.android.redorblack.dataclasses;

import android.support.annotation.Keep;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

// user class to keep track of Users of the app on Firebase
@Keep
public class User {
    String username;
    public int freeTokens;
    public int paidTokens;
    public int timesReportedForNudity;
    public int timesReportedForAbuse;
    public int timesMadeReport;
    public int gamesPlayed;
    public Object timeTokenUsed;

    public User() {
    }

    public User(String username, int startingTokens) {
        this.username = username;
        freeTokens = startingTokens;
        paidTokens = 0;
        gamesPlayed = 0;
        timeTokenUsed = -1;
        timesMadeReport = 0;
        timesReportedForAbuse = 0;
        timesReportedForNudity = 0;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> userResult = new HashMap<>();
        userResult.put("freeTokens", freeTokens);
        userResult.put("paidTokens", paidTokens);
        userResult.put("timeTokenUsed", timeTokenUsed);
        userResult.put("username", username);
        userResult.put("timesReportedForNudity", timesReportedForNudity);
        userResult.put("timesReportedForAbuse", timesReportedForAbuse);
        userResult.put("timesMadeReport", timesMadeReport);
        return userResult;
    }

    public void setTimeTokenUsed(Object timeTokenUsed) {
        this.timeTokenUsed = timeTokenUsed;
    }

    Object getTimestamp() {
        return timeTokenUsed;
    }

    @Exclude
    public long timestamp() {
        return (long) timeTokenUsed;
    }

    public int getTimesReportedForNudity() {
        return timesReportedForNudity;
    }

    public void setTimesReportedForNudity(int timesReportedForNudity) {
        this.timesReportedForNudity = timesReportedForNudity;
    }

    public int getTimesReportedForAbuse() {
        return timesReportedForAbuse;
    }

    public void setTimesReportedForAbuse(int timesReportedForAbuse) {
        this.timesReportedForAbuse = timesReportedForAbuse;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFreeTokens() {
        return freeTokens;
    }

    public void setFreeTokens(int freeTokens) {
        this.freeTokens = freeTokens;
    }

    public int getPaidTokens() {
        return paidTokens;
    }

    public void setPaidTokens(int paidTokens) {
        this.paidTokens = paidTokens;
    }

    public int getTimesMadeReport() {
        return timesMadeReport;
    }

    public void setTimesMadeReport(int timesMadeReport) {
        this.timesMadeReport = timesMadeReport;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Object getTimeTokenUsed() {
        return timeTokenUsed;
    }
}