package com.example.basketballapp.Classes;

import java.io.Serializable;

//TODO: add fouls and additional team infos for teams
public class Match implements Serializable {

    private String homeTeam;
    private String awayTeam;

    private String arena;

    private Player[] homePlayers;
    private Player[] awayPlayers;

    private int[] homePlayersPoints;
    private int[] awayPlayerPoints;

    private int[] homePlayersRebounds;
    private int[] awayPlayersRebounds;

    private int[] homePlayersAssists;
    private int[] awayPlayersAssists;

    private int homeTeamScore;
    private int awayTeamScore;

    private int timeMinute;
    private int timeSecond;

    private int quarter;

    public Match(String homeTeam, String awayTeam, String arena, Player[] homePlayers, Player[] awayPlayers, int[] homePlayersPoints,
                 int[] awayPlayerPoints, int[] homePlayersRebounds, int[] awayPlayersRebounds, int[] homePlayersAssists,
                 int[] awayPlayersAssists, int homeTeamScore, int awayTeamScore, int timeMinute, int timeSecond, int quarter){

        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.arena = arena;

        this.homePlayers = homePlayers;
        this.awayPlayers = awayPlayers;

        this.homePlayersPoints = homePlayersPoints;
        this.awayPlayerPoints = awayPlayerPoints;
        this.homePlayersRebounds = homePlayersRebounds;
        this.awayPlayersRebounds = awayPlayersRebounds;
        this.homePlayersAssists = homePlayersAssists;
        this.awayPlayersAssists = awayPlayersAssists;

        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
        this.timeMinute = timeMinute;
        this.timeSecond = timeSecond;
        this.quarter = quarter;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getArena() {
        return arena;
    }

    public Player[] getHomePlayers() {
        return homePlayers;
    }

    public Player[] getAwayPlayers() {
        return awayPlayers;
    }

    public int[] getHomePlayersPoints() {
        return homePlayersPoints;
    }

    public int[] getAwayPlayerPoints() {
        return awayPlayerPoints;
    }

    public int[] getHomePlayersRebounds() {
        return homePlayersRebounds;
    }

    public int[] getAwayPlayersRebounds() {
        return awayPlayersRebounds;
    }

    public int[] getHomePlayersAssists() {
        return homePlayersAssists;
    }

    public int[] getAwayPlayersAssists() {
        return awayPlayersAssists;
    }

    public int getHomeTeamScore() {
        return homeTeamScore;
    }

    public int getAwayTeamScore() {
        return awayTeamScore;
    }

    public int getTimeMinute() {
        return timeMinute;
    }

    public int getTimeSecond() {
        return timeSecond;
    }

    public int getQuarter() {
        return quarter;
    }



    public void setHomePlayersPoints(int[] homePlayersPoints) {
        this.homePlayersPoints = homePlayersPoints;
    }

    public void setAwayPlayerPoints(int[] awayPlayerPoints) {
        this.awayPlayerPoints = awayPlayerPoints;
    }

    public void setHomePlayersRebounds(int[] homePlayersRebounds) {
        this.homePlayersRebounds = homePlayersRebounds;
    }

    public void setAwayPlayersRebounds(int[] awayPlayersRebounds) {
        this.awayPlayersRebounds = awayPlayersRebounds;
    }

    public void setHomePlayersAssists(int[] homePlayersAssists) {
        this.homePlayersAssists = homePlayersAssists;
    }

    public void setAwayPlayersAssists(int[] awayPlayersAssists) {
        this.awayPlayersAssists = awayPlayersAssists;
    }

    public void setHomeTeamScore(int homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public void setAwayTeamScore(int awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public void setTimeMinute(int timeMinute) {
        this.timeMinute = timeMinute;
    }

    public void setTimeSecond(int timeSecond) {
        this.timeSecond = timeSecond;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }
}
