package com.ashcollege.entities;

import com.ashcollege.service.LeagueService;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Team {
    private int id;
    private String name;
    private double score;
    @JsonIgnore
    private Set<PlayerMapTeam> playerMapTeams;
    @JsonIgnore
    private Set<League> leagues= new HashSet<>();

    public Set<League> getLeagues() {
        return leagues;
    }

    public void setLeagues(Set<League> leagues) {
        this.leagues = leagues;
    }

    public Team(Team team) {
        this.id= team.getId();
        this.name= team.getName();
        this.score= team.getScore();
        this.playerMapTeams= team.playerMapTeams;
    }
    public Team() {
    }

    public int getId() {
        return id;
    }


    public Set<PlayerMapTeam> getPlayerMapTeams() {
        return playerMapTeams;
    }

    public int calcNumOfInjuredPlayers(){
        int injured = 0;
        List<Player> players= this.getActivePlayers();
        for (Player player:players) {
            if (player.isInjured()){
                injured++;
            }
        }
        return injured;
    }

    public void setPlayerMapTeams(Set<PlayerMapTeam> playerMapTeams) {
        this.playerMapTeams = playerMapTeams;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    @JsonIgnore
    public List<Player> getActivePlayers(){
        List<Player> players= new ArrayList<>();
        for (PlayerMapTeam mapTeam: playerMapTeams){
            if(mapTeam.getDeleted()==null){
                players.add(mapTeam.getPlayer());
            }
        }
        return players;
    }
//    public List<Player> getActivePlayersModel(){
//        List<Player> players= new ArrayList<>();
//        for (PlayerMapTeam mapTeam: playerMapTeams){
//            if(mapTeam.getDeleted()==null){
//                players.add(new Player());
//            }
//        }
//        return players;
//    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
    public void addPlayerMapTeam(PlayerMapTeam playerMapTeam){
        if (playerMapTeams == null || playerMapTeams.isEmpty()) {
            playerMapTeams = new HashSet<>();
        }
        playerMapTeams.add(playerMapTeam);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
