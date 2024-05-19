package com.ashcollege.entities;

import com.ashcollege.service.LeagueService;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Play {
    private int id;
    private Team homeTeam;
    private Team awayTeam;
    private Round round;
    private int temperature;
    private Set<Goal> goals = new HashSet<>();
    private Date doneDate;
    private double homeTeamWin;
    private double awayTeamWin;
    private double draw;

    public Play(int id, Team homeTeam, Team awayTeam, Round round, int temperature) {
        this(homeTeam, awayTeam, round, temperature);
        this.id = id;
    }

    public void updatePropobilty(MatchProbabilities probabilities) {
        this.setDraw(probabilities.getDraw());
        this.setAwayTeamWin(probabilities.getAwayTeamWin());
        this.setHomeTeamWin(probabilities.getHomeTeamWin());
    }

    public double getHomeTeamWin() {
        return homeTeamWin;
    }

//    public Boolean getIsHomeWinn() {
//        if (this.getDoneDate() != null) {
//            if (this.getHomeGoal() > this.getAwayGoal()) {
//                return true;
//            }
//            else if (this.getHomeGoal() < this.getAwayGoal()) {
//                return false;
//            } else return null;
//
//        }
//        return null;
//    }

    public void setHomeTeamWin(double homeTeamWin) {
        this.homeTeamWin = homeTeamWin;
    }

    public double getAwayTeamWin() {
        return awayTeamWin;
    }

    public void setAwayTeamWin(double awayTeamWin) {
        this.awayTeamWin = awayTeamWin;
    }

    public double getDraw() {
        return draw;
    }

    public void setDraw(double draw) {
        this.draw = draw;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public boolean isBadTemperature() {
        return this.getTemperature() < 10 || this.getTemperature() > 30;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public Set<Goal> getGoals() {
        return goals;
    }

    public void setGoals(Set<Goal> goals) {
        this.goals = goals;
    }


    public Play(Team homeTeam, Team awayTeam, Round round, int temperature) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.round = round;
        this.temperature = temperature;
    }

    public Play(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public Play() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public int getHomeGoal() {
        int temp = 0;
        for (Goal goal : goals) {
            if (goal.isHomeGoal()) {
                temp++;
            }
        }
        return temp;
    }

    public int getAwayGoal() {
        int temp = 0;
        for (Goal goal : goals) {
            if (!goal.isHomeGoal()) {
                temp++;
            }
        }
        return temp;
    }

    @Override
    public String toString() {
        return "Play{" +
                "id=" + id +
                ", homeTeam=" + homeTeam +
                ", awayTeam=" + awayTeam +
                ", round=" + round +
                ", temperature=" + temperature +
                ", goals=" + goals +
                ", doneDate=" + doneDate +
                ", homeTeamWin=" + homeTeamWin +
                ", awayTeamWin=" + awayTeamWin +
                ", draw=" + draw +
                '}';
    }
}
