package com.ashcollege.entities;

import com.ashcollege.model.PlayModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BetsForm {
    public static final int AWAY_WIN=1;
    public static final int HOME_WIN=2;
    public static final int DRAW=3;


    private int id;
    private User owner;
    private Play play;
    private int betType;
    private double moneyBet;
    private Boolean winn;




    public BetsForm() {

    }

    public BetsForm(Play play, int status, double money, User user) {
        this.play = play;
        this.betType = status;
        this.moneyBet=money;
        this.owner=user;
        winn=null;
    }


    public int getId() {
        return id;
    }

    public Boolean getWinn() {
        return winn;
    }
    public void setWinn(Boolean winn) {
        this.winn = winn;
    }

    public double getOdds() {
        if (betType ==AWAY_WIN){
            return 1/play.getAwayTeamWin();
        }
        else if (betType ==HOME_WIN){
            return 1/play.getHomeTeamWin();
        }
        else if (betType ==DRAW){
            return 1/play.getDraw();
        }
        return 0;
    }
    public double amount(){
        if (getWinn()){
            return (moneyBet*getOdds());
        }else return 0;
    }



    public void setId(int id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
    @JsonIgnore
    public Play getPlay() {
        return play;
    }
    public PlayModel getPlayModel() {
        return new PlayModel(play);
    }
//
//    public int getPlayId() {
//        return play.getId();
//    }

    public void setPlay(Play play) {
        this.play = play;
    }

    public int getBetType() {
        return betType;
    }

    public void setBetType(int betType) {
        this.betType = betType;
    }

    public double getMoneyBet() {
        return moneyBet;
    }

    public void setMoneyBet(double moneyBet) {
        this.moneyBet = moneyBet;
    }

    @Override
    public String toString() {
        return "BetsForm{" +
                "id=" + id +
                ", owner=" + owner +
                ", play=" + play +
                ", betType=" + betType +
                ", moneyBet=" + moneyBet +
                '}';
    }
}