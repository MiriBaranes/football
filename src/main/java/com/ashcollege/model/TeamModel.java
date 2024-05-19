package com.ashcollege.model;

import com.ashcollege.entities.Player;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.TeamStatistic;

import java.util.List;

public class TeamModel {
    private int id;
    private String name;
    private double score;
    private TeamStatistic teamStatistic;
    public TeamModel(Team team, TeamStatistic teamStatistic) {
        this.id= team.getId();
        this.name= team.getName();
        this.score= team.getScore();
        this.teamStatistic = teamStatistic;
    }
    public TeamStatistic getTeamStatistic() {
        return teamStatistic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setTeamStatistic(TeamStatistic teamStatistic) {
        this.teamStatistic = teamStatistic;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                ", score:" + score +
                ", teamStatistic:"+ teamStatistic +
                '}';
    }
}
