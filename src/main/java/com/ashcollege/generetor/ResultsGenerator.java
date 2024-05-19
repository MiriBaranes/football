package com.ashcollege.generetor;
import com.ashcollege.entities.Play;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.TeamStatistic;


public class ResultsGenerator {
    public static final double WEATHER_FACTOR = 0.05;
    public static final double RELIABILITY_LEVEL_FACTOR = 0.35;
    public static final double HISTORY_FACTOR = 0.4;
    public static final double HOME_FACTOR = 0.1;
    public static final double INJURED_PLAYERS_FACTOR = 0.1;
    public static final double INJURED_PLAYERS_CONST = 0.09;///100/11 -> 100% and 11 players in group


    public static double getWeatherFactor(boolean bad, boolean winn) {
        if (winn) {
            return bad ? 0 : WEATHER_FACTOR;
        } else {
            return bad ? WEATHER_FACTOR : 0;
        }
    }
    public static double getReliabilityLevel(double getReliabilityLevel, boolean winn) {
        if (winn) {
            return getReliabilityLevel * RELIABILITY_LEVEL_FACTOR;
        } else {
            return (1 - getReliabilityLevel) * RELIABILITY_LEVEL_FACTOR;
        }
    }

    public static double getHomeFactor(boolean home, boolean winn) {
        if (winn) {
            return home ? HOME_FACTOR : 0;
        } else {
            return home ? 0 : HOME_FACTOR;
        }

    }

    public static double getInjuredPlayersFactor(int injured, boolean winn) {
        if (winn) {
            return (1 - injured * INJURED_PLAYERS_CONST) * INJURED_PLAYERS_FACTOR;
        } else return (injured * INJURED_PLAYERS_CONST) * INJURED_PLAYERS_FACTOR;
    }


    public static double getHistoryFactor(int history, TeamStatistic teamStatistic) {
        return (history> 0) ? (double) HISTORY_FACTOR*((double) history /
                (teamStatistic.getDraws()+teamStatistic.getWins()+teamStatistic.getLosses())):0;
    }
    public static double probability(TeamStatistic statistic,Team team, Play play) {
        double homeFactor = ResultsGenerator.getHomeFactor(play.getHomeTeam().getId() == team.getId(), true);
        double injuredPlayersFactor = ResultsGenerator.getInjuredPlayersFactor(team.calcNumOfInjuredPlayers(), true);
        double getReliabilityLevel = ResultsGenerator.getReliabilityLevel(team.getScore(), true);
        double getHistoryFactor = ResultsGenerator.getHistoryFactor(statistic.getWins(), statistic);
        double getWeatherFactor = ResultsGenerator.getWeatherFactor(play.isBadTemperature(), true);
        return homeFactor + injuredPlayersFactor + getReliabilityLevel + getHistoryFactor + getWeatherFactor;
    }




}
