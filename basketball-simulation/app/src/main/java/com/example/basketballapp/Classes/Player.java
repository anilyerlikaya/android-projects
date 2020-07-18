package com.example.basketballapp.Classes;

import java.io.Serializable;

public class Player implements Serializable {

    private String playerName;
    private String playerPosition;
    private int playerAge;

    private int gamesPlayed;
    private double assist;
    private double points;
    private double rebound;

    private int matchPoints;
    private int matchAssists;
    private int matchRebounds;

    public Player(String playerName, String playerPosition, int playerAge, int gamesPlayed, double assist, double points,
                  double rebound){

        this.playerName = playerName;
        this.playerPosition = playerPosition;
        this.playerAge = playerAge;

        this.gamesPlayed = gamesPlayed;
        this.assist = assist;
        this.points = points;
        this.rebound = rebound;
    }

    public Player(String playerName, int matchAssists, int matchPoints, int matchRebounds){
        this.playerName = playerName;
        this.matchAssists = matchAssists;
        this.matchPoints = matchPoints;
        this.matchRebounds = matchRebounds;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerPosition() {
        return playerPosition;
    }

    public int getPlayerAge() {
        return playerAge;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public double getAssist() {
        return assist;
    }

    public double getPoints() {
        return points;
    }

    public double getRebound() {
        return rebound;
    }

    public int getMatchPoints() {
        return matchPoints;
    }

    public int getMatchAssists() {
        return matchAssists;
    }

    public int getMatchRebounds() {
        return matchRebounds;
    }

    public void setMatchAssists(int matchAssists) {
        this.matchAssists = matchAssists;
    }

    public void setMatchPoints(int matchPoints) {
        this.matchPoints = matchPoints;
    }

    public void setMatchRebounds(int matchRebounds) {
        this.matchRebounds = matchRebounds;
    }
}
