package com.example.basketballapp.Classes;

import java.io.Serializable;

public class PlayByPlay implements Serializable {

    private String[] playerName;
    private String[] assistPlayerName;

    private int[] playerTeam;             // 1->home, 2->away
    private int[] action;                 // 1->point, 2->rebound

    private int[] pointValue;

    private int[] timeMinute;
    private int[] timeSecond;
    private int[] quarter;

    public PlayByPlay(String[] playerName, String[] assistPlayerName, int[] playerTeam, int[] action,
                      int[] pointValue, int[] timeMinute, int[] timeSecond, int[] quarter){

        this.playerName = playerName;
        this.assistPlayerName = assistPlayerName;

        this.playerTeam = playerTeam;
        this.action = action;

        this.pointValue = pointValue;

        this.timeMinute = timeMinute;
        this.timeSecond = timeSecond;
        this.quarter = quarter;
    }

    public String[] getPlayerName() {
        return playerName;
    }

    public String[] getAssistPlayerName() {
        return assistPlayerName;
    }

    public int[] getPlayerTeam() {
        return playerTeam;
    }

    public int[] getAction() {
        return action;
    }

    public int[] getPointValue() {
        return pointValue;
    }

    public int[] getTimeMinute() {
        return timeMinute;
    }

    public int[] getTimeSecond() {
        return timeSecond;
    }

    public int[] getQuarter() {
        return quarter;
    }
}
