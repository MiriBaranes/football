package com.ashcollege.model;

import com.ashcollege.entities.Player;
import com.ashcollege.entities.Team;

import java.util.List;

public class TeamData {
    private Team team;
    private List<PlayModel> playModelList;
    private List<Player> players;

    public TeamData(Team team, List<PlayModel> playModelList, List<Player> players) {
        this.team=team;
        this.playModelList = playModelList;
        this.players = players;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<PlayModel> getPlayModelList() {
        return playModelList;
    }

    public void setPlayModelList(List<PlayModel> playModelList) {
        this.playModelList = playModelList;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
