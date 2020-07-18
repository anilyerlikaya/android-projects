package com.example.basketballapp.Classes;

import java.io.Serializable;

public class League implements Serializable {
    private String leagueName;

    private Team[] teams;

    public League(String leagueName, Team[] teams){

        this.leagueName = leagueName;
        this.teams = teams;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public Team[] getTeams() {
        return teams;
    }
}
