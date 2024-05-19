package com.ashcollege.service;

import com.ashcollege.entities.*;

import com.ashcollege.generetor.ResultsGenerator;
import com.ashcollege.model.TeamData;
import com.ashcollege.model.TeamModel;
import com.ashcollege.model.PlayModel;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.ashcollege.utils.PageConst.*;
import static com.ashcollege.run.RoundThread.MILL_IN_SEC;
import static com.ashcollege.run.RoundThread.TIME_FOR_GAME;


@Component
@Transactional
public class LeagueService {

    @Autowired
    private Persist persist;
    private final Faker faker = new Faker();

    /////////LEAGUE//////
    public League createLeague() {
        League league = new League();
        league.setLeagueName(faker.leagueOfLegends().location());
        persist.save(league);
        return league;
    }

    /////////ROUND//////
    public Round createRound(League league, Date date) {
        Round round = new Round();
        round.setLeague(league);
        round.setStartTime(date);
        persist.save(round);
        return round;
    }
    /////////TEAM//////

    public Team createTeam(League league) {///unique name for team
        Team team = new Team();
        String teamName = faker.team().name();
        String finalTeamName = teamName;
        team.getLeagues().add(league);
        while (persist.loadList(Team.class).stream().anyMatch(team1 -> team1.getName().equals(finalTeamName))) {
            teamName = faker.team().name();
        }
        team.setName(teamName);
        Random randomNum = new Random();
        team.setScore(randomNum.nextDouble() * (0.5));
        ////score
        persist.save(team);
        return team;
    }
    public List<TeamModel> getTeamListGame(Play play){
        List<TeamModel> teamList = new ArrayList<>();
        teamList.add(this.getTeamModelActive(play.getHomeTeam()));
        teamList.add(this.getTeamModelActive(play.getAwayTeam()));
        return teamList;

    }

    /////////PLAYER//////
    public Player createPlayer(Team team, boolean isInjured) {
        Player player = new Player();
        player.setInjured(isInjured);
        player.setFirstName(faker.name().firstName());
        player.setLastName(faker.name().lastName());
        PlayerMapTeam playerMapTeam = new PlayerMapTeam();
        player.addTeamMap(playerMapTeam);
        playerMapTeam.setCreated(new Date());
        playerMapTeam.setPlayer(player);
        team.addPlayerMapTeam(playerMapTeam);
        playerMapTeam.setTeam(team);
        persist.save(team);
        persist.save(playerMapTeam);
        persist.save(player);
        return player;
    }

    /////////PLAY//////
    public Play createPlay(Team home, Team away, Round round) {
        Random random = new Random();
        Play play = new Play(home, away, round, random.nextInt(45));
        persist.save(play);
        return play;
    }
    public List<Goal> getGoalList(int playId) {
        return persist.loadList(Goal.class).stream().filter(goal -> goal.getPlay().getId() == playId).collect(Collectors.toList());
    }
    public List<Goal> getGoalPlayer(int playerId) {
        return persist.loadList(Goal.class).stream().filter(goal -> goal.getOwner().getId()==playerId).collect(Collectors.toList());
    }

    /////////GOAL//////
    public void createGoal(Play play, boolean isHome, Player owner) {
        long time = (System.currentTimeMillis() - play.getRound().getStartTime().getTime()) / 1000;
        Goal goal = new Goal(time + "", play, isHome, owner);
        persist.save(goal);
    }

    public List<Team> getAllTeam() {
        return new ArrayList<>(persist.loadList(Team.class));
    }

    public List<Play> getPlayTeam(int id) {
        return persist.loadList(Play.class).stream().filter(play -> play.getAwayTeam().getId() == id || play.getHomeTeam().getId() == id).collect(Collectors.toList());
    }

    public List<PlayModel> getPlayModelListForTeam(int id) {
        List<Play> list = getPlayTeam(id);
        List<PlayModel> result = new ArrayList<>();
        for (Play play : list) {
            result.add(new PlayModel(play));
        }
        return result;
    }


    public List<MatchProbabilities> getMatchProbabilitiesList(List<Play> plays) {
        List<MatchProbabilities> matchProbabilities = new ArrayList<>();
        for (Play play : plays) {
            Date now = new Date();
            if (play.getDoneDate() == null && now.before(play.getRound().getStartTime())) {
                MatchProbabilities probabilities = getProbabilities(play);
                matchProbabilities.add(probabilities);
                play.updatePropobilty(probabilities);
                persist.save(play);
            } else {
                matchProbabilities.add(new MatchProbabilities(play.getHomeTeamWin(), play.getAwayTeamWin(), play.getDraw()));
            }
        }
        return matchProbabilities;
    }

    public MatchProbabilities getProbabilities(Play play) {
        double probabilityToWinnForHomeTeam = ResultsGenerator.probability
                (this.getTeamStatistic(play.getHomeTeam(), true), play.getHomeTeam(), play);
        double probabilityToWinnForAwayTeam = ResultsGenerator.probability(
                this.getTeamStatistic(play.getAwayTeam(), true), play.getAwayTeam(), play);
        return getMatchProbabilities(play, probabilityToWinnForAwayTeam, probabilityToWinnForHomeTeam);
    }

    private static MatchProbabilities getMatchProbabilities(Play play, double probabilityToWinnForAwayTeam, double probabilityToWinnForHomeTeam) {
        double draw = (probabilityToWinnForAwayTeam + probabilityToWinnForHomeTeam) / 2;
        double homeWinnAway = probabilityToWinnForHomeTeam / (probabilityToWinnForAwayTeam + probabilityToWinnForHomeTeam);
        double awayWinnHome = 1 - homeWinnAway;
        homeWinnAway = (1 - draw) * homeWinnAway;
        awayWinnHome = (1 - draw) * awayWinnHome;
        MatchProbabilities matchProbabilities = new MatchProbabilities(homeWinnAway, awayWinnHome, draw);
        matchProbabilities.setPlay(play);
        return matchProbabilities;
    }

    public TeamStatistic getTeamStatistic(Team team, boolean donePlays) {
        List<PlayModel> playModelList = getPlayModelListForTeam(team.getId());
        int numberOfGoals = 0;
        int numberOfConceded = 0;
        int gamePlayed = 0;
        int win = 0;
        int lose = 0;
        int same = 0;
        for (PlayModel playModel : playModelList) {
            if (!donePlays || playModel.getDone() == null) {
                continue; // Skip if plays are not done
            }
            gamePlayed++;
            int homeGoals = playModel.getNumberOfGoalForHome();
            int awayGoals = playModel.getNumberOfGoalForAway();
            numberOfGoals += (team.getId() == playModel.getAway().getId()) ? awayGoals : homeGoals;
            numberOfConceded += (team.getId() == playModel.getAway().getId()) ? homeGoals : awayGoals;
            if (homeGoals > awayGoals) {
                lose += (team.getId() == playModel.getAway().getId()) ? 1 : 0;
                win += (team.getId() != playModel.getAway().getId()) ? 1 : 0;
            } else if (homeGoals < awayGoals) {
                win += (team.getId() == playModel.getAway().getId()) ? 1 : 0;
                lose += (team.getId() != playModel.getAway().getId()) ? 1 : 0;
            } else {
                same++;
            }
        }

        return new TeamStatistic(team.getId(), gamePlayed, numberOfGoals, numberOfConceded, win, same, lose);
    }


    public Team getWinner(Play play) {
        int home = 0;
        int away = 0;
        for (Goal goal : play.getGoals()) {
            if (goal.isHomeGoal()) {
                home++;
            } else {
                away++;
            }
        }
        return away > home ? play.getAwayTeam() : away < home ? play.getHomeTeam() : null;
    }


    public List<Play> getRoundPlays(Round round) {
        return persist.loadList(Play.class).stream().filter(play -> play.getRound().getId() == round.getId()).collect(Collectors.toList());
    }

    public List<PlayModel> getPlayModelList() {
        List<PlayModel> playModelList = new ArrayList<>();
        List<Play> playList = persist.loadList(Play.class);
        for (Play play : playList) {
            playModelList.add(new PlayModel(play));
        }
        return playModelList;
    }

    public PlayModel getPlayById(int id) {
        Play play = persist.loadObject(Play.class, id);
        return new PlayModel(play);
    }

    public List<MatchProbabilities> getPlayMatchProbabilities() {
        List<Play> play = persist.loadList(Play.class);
        return this.getMatchProbabilitiesList(play);
    }
    public Round getNextRound() {
        List<Round> rounds = persist.loadList(Round.class);
        Round closestDate = null;
        long closestDifference = Long.MAX_VALUE;
        Date currentDate = new Date(); // Get current date/time

        for (Round round:rounds) {
            if (round.getStartTime().before(currentDate)) { // Skip dates after current date/time
                continue;
            }
            long difference = Math.abs(round.getStartTime().getTime() - currentDate.getTime());
            if (difference < closestDifference) {
                closestDifference = difference;
                closestDate = round;
            }
        }

        return closestDate;
    }
    public List<PlayModel> getNextRoundPlay() {
        Round round= getNextRound();
        List<PlayModel> playModelList= new ArrayList<PlayModel>();
        if (round!=null){
            playModelList = getPlayModelListByPlayList(getRoundPlays(round));
        }
        return playModelList;
    }

    public List<TeamModel> getTeamModels() {
        List<TeamModel> teamModels = new ArrayList<>();
        List<Team> teams = persist.loadList(Team.class);
        for (Team team : teams) {
            teamModels.add(new TeamModel(team, this.getTeamStatistic(team, true)));
        }
        return teamModels;
    }
    public TeamModel getTeamModelActive(Team team) {
        Team activeTeam= persist.loadObject(Team.class,team.getId());
        return new TeamModel(activeTeam,this.getTeamStatistic(activeTeam, true));
    }


    public List<Team> getTeamByLeagueId(int id) {
        return persist.loadObject(League.class, id).getTeams().stream().toList();
    }

    public Player getOwnerGoal(Team team) {
        List<Player> players = team.getActivePlayers().stream().filter(player -> !player.isInjured()).toList();
        return players.get(new Random().nextInt(players.size()));
    }

    public List<PlayModel> activePlays() {
        List<Round> rounds = persist.loadList(Round.class).stream().filter(this::isActiveRound).toList();
        if (rounds.isEmpty()) {
            return new ArrayList<PlayModel>();
        } else {
            List<Play> plays = persist.loadList(Play.class).stream().filter(p -> isActiveRound(p.getRound())).toList();
            return getPlayModelListByPlayList(plays);
        }
    }

    public List<PlayModel> futureGames() {
        Date now = new Date();
        List<Play> plays = persist.loadList(Play.class).stream().filter(p -> now.before(p.getRound().getStartTime())).toList();
        return getPlayModelListByPlayList(plays);
    }
    public List<PlayModel> pastGames() {
        Date now = new Date();
        List<Play> plays = persist.loadList(Play.class).stream().filter(p -> p.getDoneDate()!=null && now.after(p.getDoneDate())).toList();
        return getPlayModelListByPlayList(plays);
    }

    public List<PlayModel> getPlayModelListByPlayList(List<Play> playList) {
        List<PlayModel> playModelList = new ArrayList<>();
        for (Play play : playList) {
            playModelList.add(new PlayModel(play));
        }
        return playModelList;
    }

    public boolean isActiveRound(Round round) {
        Date date = round.getStartTime();
        Date now= new Date();
        long duration = (long) (TIME_FOR_GAME * MILL_IN_SEC);
        long temp=now.getTime() - date.getTime();
        return 0<temp && temp < duration;
    }
    public TeamData getTeamDataById(int id){
        Team team= persist.loadObject(Team.class,id);
       List<Play> plays= persist.loadList(Play.class).stream().filter(play -> play.getAwayTeam().getId()==id || play.getHomeTeam().getId()==id).toList();
       List<Player> players= team.getActivePlayers();
        return new TeamData(team,getPlayModelListByPlayList(plays),players);
    }
    public List<?> getDataByType(int type) {
        List<?> list = new ArrayList<>();
        if (type==ALL_TEAMS_PAGE) {///all teams
            list = this.getTeamModels();
        } else if (type==FUTURE_GAMES_PAGE) {/// future games- i need to add option to bat
            list = this.futureGames();
        } else if (type==ACTIVE_TEAM_PAGE) {///active team
            list = this.activePlays();
        } else if (type==PAST_GAMES_PAGE) { /////history game page
            list = this.pastGames();
        }
        else if (type==NEXT_ROUND_GAMES_PAGE) { /////history game page
            list =this.getNextRoundPlay();
        }
        return list;
    }
    public void updateBetsOfGame(Play play) {
        List<BetsForm> betsForms = persist.loadList(BetsForm.class).stream().filter(betsForm -> betsForm.getPlay().getId() == play.getId()).toList();
        int type = play.getAwayGoal() > play.getHomeGoal() ? BetsForm.AWAY_WIN : play.getAwayGoal() < play.getHomeGoal() ? BetsForm.HOME_WIN : BetsForm.DRAW;
        for (BetsForm betsForm : betsForms) {
            betsForm.setWinn(betsForm.getBetType() == type);
            User toUpdate=persist.loadObject(User.class,betsForm.getOwner().getId());
            toUpdate.setBalance(toUpdate.getBalance()+betsForm.amount());
            persist.save(toUpdate);
        }
        persist.saveAll(betsForms);
    }


}
