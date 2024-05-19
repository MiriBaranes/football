package com.ashcollege.controllers;

import com.ashcollege.entities.*;
import com.ashcollege.model.TeamData;
import com.ashcollege.model.TeamModel;
import com.ashcollege.model.PlayModel;
import com.ashcollege.service.LeagueService;
import com.ashcollege.service.Persist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api-leagues")
public class LeagueController {
    @Autowired
    private Persist persist;
    @Autowired
    private LeagueService leagueService;
    @RequestMapping(value = "/teams", method = {RequestMethod.GET, RequestMethod.POST})
    public List<TeamModel> getTeams() {
        return leagueService.getTeamModels();
    }
    @RequestMapping(value = "/team-id", method = {RequestMethod.GET, RequestMethod.POST})
    public TeamModel getTeamById(int id) {
        Team team= persist.loadObject(Team.class, id);
        return new TeamModel(team,leagueService.getTeamStatistic(team,true));
    }
    @RequestMapping(value="/plays" , method = {RequestMethod.GET, RequestMethod.POST})
    public List<PlayModel> getPlays() {
        return leagueService.getPlayModelList();
    }
    @RequestMapping(value="/plays-1" , method = {RequestMethod.GET, RequestMethod.POST})
    public List<Play> getPlays1() {
        return persist.loadList(Play.class);
    }
    @RequestMapping(value="/play-id" , method = {RequestMethod.GET, RequestMethod.POST})
    public PlayModel getPlayById(int id) {
        return leagueService.getPlayById(id);
    }
    @RequestMapping(value="/plays-probabilities" , method = {RequestMethod.GET, RequestMethod.POST})
    public List<MatchProbabilities> getPlayMatchProbabilities() {
        return leagueService.getPlayMatchProbabilities();
    }
    @RequestMapping(value="/leagues" , method = {RequestMethod.GET, RequestMethod.POST})
    public List<League> leagueList() {
        return persist.loadList(League.class).stream().toList();
    }
    @RequestMapping(value="/get-teams-league" , method = {RequestMethod.GET, RequestMethod.POST})
    public List<Team> teamListLeague(int id) {
        return leagueService.getTeamByLeagueId(id);
    }
    @RequestMapping(value="/get-data-by-type" , method = {RequestMethod.GET, RequestMethod.POST})
    public List<?> getDataByType(int type) {
        return leagueService.getDataByType(type);
    }
    @RequestMapping(value="/get-next-round" , method = {RequestMethod.GET, RequestMethod.POST})
    public Round getNextRound () {
        return leagueService.getNextRound();
    }
    @RequestMapping(value="/get-next-round-plays" , method = {RequestMethod.GET, RequestMethod.POST})
    public List<PlayModel> getNextRoundPlay () {
        return leagueService.getNextRoundPlay();
    }
    @RequestMapping(value="/get-all-teams" , method = {RequestMethod.GET, RequestMethod.POST})
    public List<Team> getNextRoundPlay1 () {
        return leagueService.getAllTeam();
    }

    @RequestMapping(value="/get-team-data" , method = {RequestMethod.GET, RequestMethod.POST})
    public TeamData getTeamData (int id) {
        return leagueService.getTeamDataById(id);
    }
    @RequestMapping(value="/get-goals-play" , method = {RequestMethod.GET, RequestMethod.POST})
    public List<Goal> getGoalPlay (int id) {
        return leagueService.getGoalList(id);
    }
    @RequestMapping(value="/get-player-goals" , method = {RequestMethod.GET, RequestMethod.POST})
    public List<Goal> getPlayerGoals (int id) {
        return leagueService.getGoalPlayer(id);
    }


}
