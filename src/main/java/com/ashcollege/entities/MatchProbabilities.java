package com.ashcollege.entities;

import com.ashcollege.generetor.ResultsGenerator;
import com.ashcollege.service.LeagueService;

public class MatchProbabilities {
    private double homeTeamWin;
    private double awayTeamWin;
    private double draw;
    private Play play;

    public MatchProbabilities(double homeTeamWin, double awayTeamWin, double draw) {
        this.homeTeamWin = homeTeamWin;
        this.awayTeamWin = awayTeamWin;
        this.draw = draw;
    }

    public Play getPlay() {
        return play;
    }

    public void setPlay(Play play) {
        this.play = play;
    }

    public MatchProbabilities(LeagueService leagueService, Play play){
        double probabilityToWinnForHomeTeam = ResultsGenerator.probability(leagueService.getTeamStatistic(play.getHomeTeam(), true), play.getHomeTeam(), play);
        double probabilityToWinnForAwayTeam = ResultsGenerator.probability(leagueService.getTeamStatistic(play.getAwayTeam(), true), play.getAwayTeam(), play);
        double draw = (probabilityToWinnForAwayTeam + probabilityToWinnForHomeTeam) / 2;
        double homeWinnAway = probabilityToWinnForHomeTeam / (probabilityToWinnForAwayTeam + probabilityToWinnForHomeTeam);
        double awayWinnHome = 1 - homeWinnAway;
        this.homeTeamWin= (1 - draw) * homeWinnAway;
        this.awayTeamWin = (1 - draw) * awayWinnHome;
        this.play= play;
    }

    public double getHomeTeamWin() {
        return homeTeamWin;
    }

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
}
