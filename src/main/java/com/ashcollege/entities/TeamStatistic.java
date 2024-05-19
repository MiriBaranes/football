package com.ashcollege.entities;

public class TeamStatistic {

    private int teamId;
    private int gamesPlayed;
    private int goalsScored;
    private int goalsConceded;
    private int wins;
    private int draws;
    private int losses;

    public TeamStatistic() {
    }

    public TeamStatistic(int teamId, int gamesPlayed, int goalsScored, int goalsConceded, int wins, int draws, int losses) {
        this.teamId = teamId;
        this.gamesPlayed = gamesPlayed;
        this.goalsScored = goalsScored;
        this.goalsConceded = goalsConceded;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
    }

    public int getDifference(){
        return this.goalsScored - this.goalsConceded;
    }
    public int getPoints(){
        return ((this.wins*3) + this.draws);
    }


    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public void setGoalsScored(int goalsScored) {
        this.goalsScored = goalsScored;
    }

    public int getGoalsConceded() {
        return goalsConceded;
    }

    public void setGoalsConceded(int goalsConceded) {
        this.goalsConceded = goalsConceded;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    @Override
    public String toString() {
        return "{"+
                "teamId:" + teamId +
                ", gamesPlayed:" + gamesPlayed +
                ", goalsScored:" + goalsScored +
                ", goalsConceded:" + goalsConceded +
                ", wins:" + wins +
                ", draws:" + draws +
                ", losses:" + losses +
                '}';
    }
}