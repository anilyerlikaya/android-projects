package com.example.basketballapp.Classes;

import com.example.basketballapp.Classes.Player;

import java.io.Serializable;

public class Team implements Serializable {
    private String teamName;
    private String coach;
    private String city;
    private String foundationDate;
    private String arena;

    private int winCount;
    private int loseCount;
    private int winStreak;

    private Player[] players;

    public Team(String teamName, String coach, String city, String foundationDate, String arena, int winStreak,
                int winCount, int loseCount, Player[] players){

        this.teamName = teamName;
        this.coach = coach;
        this.city = city;
        this.foundationDate = foundationDate;
        this.arena = arena;

        this.winStreak = winStreak;
        this.winCount = winCount;
        this.loseCount = loseCount;

        this.players = players;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getCoach() {
        return coach;
    }

    public String getCity() {
        return city;
    }

    public String getFoundationDate() {
        return foundationDate;
    }

    public String getArena() {
        return arena;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public Player[] getPlayers() {
        return players;
    }
}
