package com.ashcollege.entities;

public class MatchOdds {
    private double homeTeamOdds;
    private double drawOdds;
    private double awayTeamOdds;

    public MatchOdds(MatchProbabilities matchProbabilities) {
        this.homeTeamOdds = 1/matchProbabilities.getHomeTeamWin();
        this.drawOdds = 1/matchProbabilities.getDraw();
        this.awayTeamOdds = 1/matchProbabilities.getAwayTeamWin();
    }

    public double getHomeTeamOdds() {
        return homeTeamOdds;
    }

    public void setHomeTeamOdds(double homeTeamOdds) {
        this.homeTeamOdds = homeTeamOdds;
    }

    public double getDrawOdds() {
        return drawOdds;
    }

    public void setDrawOdds(double drawOdds) {
        this.drawOdds = drawOdds;
    }

    public double getAwayTeamOdds() {
        return awayTeamOdds;
    }

    public void setAwayTeamOdds(double awayTeamOdds) {
        this.awayTeamOdds = awayTeamOdds;
    }
}
