package com.ashcollege.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Goal {
    private int id;
    private String time;
    @JsonIgnore
    private Play play;
    private boolean homeGoal;
    private Player owner;

    public Goal(int id, String time, Play play, boolean homeGoal, Player owner) {
        this(time,play,homeGoal,owner);
        this.id = id;
    }
    public Goal(String time, Play play, boolean homeGoal, Player owner) {
        this.time = time;
        this.play = play;
        this.homeGoal = homeGoal;
        this.owner = owner;
    }
    public Goal(){

    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Play getPlay() {
        return play;
    }

    public void setPlay(Play play) {
        this.play = play;
    }

    public boolean isHomeGoal() {
        return homeGoal;
    }

    public void setHomeGoal(boolean isHomeGoal) {
        homeGoal = isHomeGoal;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
