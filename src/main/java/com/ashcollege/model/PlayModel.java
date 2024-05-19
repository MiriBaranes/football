package com.ashcollege.model;

import com.ashcollege.entities.Goal;
import com.ashcollege.entities.League;
import com.ashcollege.entities.Play;
import com.ashcollege.entities.Team;

import java.util.Date;
import java.util.Set;

public class PlayModel {
    private Team home;
    private Team away;
    private int round;
    private int id;
    private Date start;
    private Date done;
    private int numberOfGoalForHome;
    private int numberOfGoalForAway;
    private PlayOdds playOdds;
    private League league;

    public PlayModel(Play play) {
        this.id =play.getId();
        this.home=play.getHomeTeam();
        this.away=play.getAwayTeam();
        calculateGoal(play.getGoals());
        done=play.getDoneDate();
        start=play.getRound().getStartTime();
        this.round=play.getRound().getId();
        this.league=play.getRound().getLeague();
        playOdds=new PlayOdds(play);
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public PlayOdds getPlayOdds() {
        return playOdds;
    }

    public void setPlayOdds(PlayOdds playOdds) {
        this.playOdds = playOdds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Date getDone() {
        return done;
    }
    public void calculateGoal(Set<Goal> goals){
        for(Goal goal: goals){
            if (goal.isHomeGoal()){
                numberOfGoalForHome++;
            }else {
                numberOfGoalForAway++;
            }
        }

    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setDone(Date done) {
        this.done = done;
    }

    public Team getHome() {
        return home;
    }

    public void setHome(Team home) {
        this.home = home;
    }

    public Team getAway() {
        return away;
    }

    public void setAway(Team away) {
        this.away = away;
    }

    public int getNumberOfGoalForHome() {
        return numberOfGoalForHome;
    }

    public void setNumberOfGoalForHome(int numberOfGoalForHome) {
        this.numberOfGoalForHome = numberOfGoalForHome;
    }

    public int getNumberOfGoalForAway() {
        return numberOfGoalForAway;
    }

    public void setNumberOfGoalForAway(int numberOfGoalForAway) {
        this.numberOfGoalForAway = numberOfGoalForAway;
    }

    @Override
    public String toString() {
        return "PlayModel: {" +
                "playId:"+ id +","+
                "home:" + home +
                ", away:" + away +
                ", round:" + round +
                ", start:" + start.toString() +
                ", done:" +  done+
                ", numberOfGoalForHome:" + numberOfGoalForHome +
                ", numberOfGoalForAway:" + numberOfGoalForAway +
                '}';
    }
}
