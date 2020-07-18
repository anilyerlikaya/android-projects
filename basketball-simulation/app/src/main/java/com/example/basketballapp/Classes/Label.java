package com.example.basketballapp.Classes;

import java.io.Serializable;

public class Label implements Serializable {

    private int imageX;
    private int imageY;

    private int minute;
    private int second;
    private int quarter;

    private String playerName;
    private String action;
    private String point;

    public Label(int imageX, int imageY, int minute, int second, int quarter, String playerName, String action, String point){

        this.imageX = imageX;
        this.imageY = imageY;

        this.minute = minute;
        this.second = second;
        this.quarter = quarter;

        this.playerName = playerName;
        this.action = action;
        this.point = point;
    }

    public int getImageX() {
        return imageX;
    }

    public int getImageY() {
        return imageY;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getQuarter() {
        return quarter;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getAction() {
        return action;
    }

    public String getPoint() {
        return point;
    }
}
