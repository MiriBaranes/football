package com.ashcollege.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

public class Player {
    private int id;
    private String firstName;
    private String lastName;
    private boolean injured;
    //    @JsonIgnore
//    private Set<Team> teams;
    @JsonIgnore
    private Set<PlayerMapTeam> playerMapTeams;

    public Player(int id, String firstName, String lastName, Team team) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
//        this.teams = new HashSet<>();
//        teams.add(team);
    }

    public boolean isInjured() {
        return injured;
    }

    public void setInjured(boolean injured) {
        this.injured = injured;
    }

    public Set<PlayerMapTeam> getPlayerMapTeams() {
        return playerMapTeams;
    }

    public void setPlayerMapTeams(Set<PlayerMapTeam> playerMapTeams) {
        this.playerMapTeams = playerMapTeams;
    }

    public Player() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    //    public Set<Team> getTeams() {
//        return teams;
//    }
//
//    public void setTeams(Set<Team> teams) {
//        this.teams = teams;
//    }
//
//    public void addTeam(Team team) {
//        if (teams == null || teams.isEmpty()) {
//            teams = new HashSet<>();
//        }
//        teams.add(team);
//    }
    public void addTeamMap(PlayerMapTeam playerMapTeam) {
        if (playerMapTeams == null || playerMapTeams.isEmpty()) {
            playerMapTeams = new HashSet<>();
        }
        playerMapTeams.add(playerMapTeam);
    }
}
