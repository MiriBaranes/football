package com.ashcollege.generetor;

import com.ashcollege.utils.PageConst;
import com.ashcollege.entities.*;
import com.ashcollege.run.RoundThread;
import com.ashcollege.service.LeagueService;
import com.ashcollege.service.Persist;
import com.ashcollege.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@RestController
public class LeagueGenerator {
    public static final int NUMBER_OF_TEAMS = 8;
    public static final double FAIR_FACTOR=0.7;
    public static final int NUMBER_OF_ROUNDS = NUMBER_OF_TEAMS - 1;
    public static final int NUMBER_OF_PLAYERS_IN_TEAM = 11;
    public static final long MILL_IN_SEC = 1000;
    public static final double WINN_ADD_FOR_SCORE = 0.25/NUMBER_OF_ROUNDS;
    private final List<UserEvent> users = new ArrayList<>();
    @Autowired
    private UserService userService;
    @Autowired
    private LeagueService leagueService;
    @Autowired
    private Persist persist;
    private List<Round> rounds = new ArrayList<>();

    @PostConstruct
    public void init() {
        generate();
    }

    @GetMapping(value = "start-streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createStreamingSession(String token, String type) {
        try {
            synchronized (users) {
                Optional<UserEvent> optionalUserEvent = users.stream()
                        .filter(userEvent -> userEvent.getToken().equals(token))
                        .findFirst();
                if (optionalUserEvent.isPresent()) {
                    UserEvent userEvent = optionalUserEvent.get();
                    if (!Objects.equals(userEvent.getType(), type)) {
                        if (type == null || type.equals("null")) {
                            users.remove(userEvent);
                        } else {
                            userEvent.setType(type);
                        }
                    }
                    return userEvent.getSseEmitter();
                } else {
                    if (type != null) {
                        SseEmitter sseEmitter = new SseEmitter((long) 10 * 60 * 10000);
                        users.add(new UserEvent(sseEmitter, token, type));
                        User user = userService.getUserByToken(token);
                        user.setLastActivity(new Date());
                        persist.save(user);
                        return sseEmitter;
                    } else return null;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    @EventListener(ApplicationReadyEvent.class)
    public void liveLeague() {
        generateRounds(rounds);
    }



    public void generate() {
        League league = leagueService.createLeague();
        ArrayList<Team> teamList = generateTeamList(league);
        List<Round> rounds = generateRoundList(league);
        this.rounds = rounds;
        List<Play> plays = generateSchedule(teamList, rounds);
    }




    public List<Round> generateRoundList(League league) {
        List<Round> rounds = new ArrayList<>();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        for (int i = 0; i < NUMBER_OF_ROUNDS; i++) {
            cal.add(Calendar.MINUTE, 1);
            rounds.add(addRound(league, cal.getTime()));
        }
        return rounds;
    }

    public List<Play> generateSchedule(List<Team> teams, List<Round> rounds) {
        List<Play> matches = persist.loadList(Play.class);
        Random random = new Random();
        List<Team> teamsCopy = new ArrayList<>(teams);
        for (int i = 0; i < teams.size() - 1; i++) {
            for (int j = 0; j < teams.size() / 2; j++) {
                Team homeTeam = teamsCopy.get(j);
                Team awayTeam = teamsCopy.get(teams.size() - 1 - j);
                Play match = new Play(homeTeam, awayTeam, rounds.get(i), random.nextInt(45));
                match.updatePropobilty(leagueService.getProbabilities(match));
                matches.add(match);
            }
            Collections.rotate(teamsCopy.subList(1, teamsCopy.size()), 1);
        }
        persist.saveAll(matches);
        return matches;
    }


    public Round addRound(League league, Date date) {
        return leagueService.createRound(league, date);
    }


    public ArrayList<Team> generateTeamList(League league) {
        ArrayList<Team> teamList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_TEAMS; i++) {
            Team currentTeam = leagueService.createTeam(league);
            int count = 0;
            for (int j = 0; j < NUMBER_OF_PLAYERS_IN_TEAM; j++) {
                boolean injured = false;
                if (count < 3) {
                    Random random = new Random();
                    injured = random.nextBoolean();
                    count++;
                }
                leagueService.createPlayer(currentTeam, injured);
            }
            teamList.add(currentTeam);
        }
        return teamList;
    }

    public void generateRounds(List<Round> rounds) {
        for (Round round1 : rounds) {
            while (round1.getStartTime().getTime() > System.currentTimeMillis()) {
                try {
                    Thread.sleep(MILL_IN_SEC);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
            playGenerator(round1);
        }

    }

    //    @Scheduled(fixedDelay = 2 * 1000)
    public void updateBeforeRoundStarted() {
        List<UserEvent> toRemove = new ArrayList<UserEvent>();
        if (!users.isEmpty()) {
            synchronized (users) {
                List<UserEvent> thisEvents = users.stream().filter(userEvent -> userEvent.getType().equals(PageConst.FUTURE_GAMES_PAGE + "") ||
                        userEvent.getType().equals(PageConst.ACTIVE_TEAM_PAGE + "") || userEvent.getType().equals(PageConst.NEXT_ROUND_GAMES_PAGE + "")).toList();
                synchronized (thisEvents) {
                    for (UserEvent userEvent : thisEvents) {
                        try {
                            List<?> list = new ArrayList<>();
                            switch (userEvent.getType()) {
                                case PageConst.FUTURE_GAMES_PAGE + "" -> {
                                    list = leagueService.futureGames();
                                }
                                case PageConst.ACTIVE_TEAM_PAGE + "" -> {
                                    list = leagueService.activePlays();// makes the users side be this list
                                }
                                case PageConst.NEXT_ROUND_GAMES_PAGE + "" ->
                                        list = userService.getActiveBet(userEvent.getToken());
                                default -> throw new IllegalStateException("Unexpected value: " + userEvent.getType());
                            }
                            ;
                            try {
                                userEvent.getSseEmitter().send(list);
                            } catch (IllegalStateException | IOException e) {
                                toRemove.add(userEvent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                users.removeAll(toRemove);
            }
        }

    }

    public void playGenerator(Round round) {
        List<Play> plays = leagueService.getRoundPlays(round);
        updateBeforeRoundStarted();
        RoundThread roundThread = new RoundThread(round, plays, this, users, leagueService, userService);
        new Thread(roundThread).start();

    }

    public void updatePlayWinnerScore(List<Play> plays, Round round) {
        round.setEndTime(new Date());
        persist.save(round);
        for (Play play : plays) {
            Team team = leagueService.getWinner(play);
            if (team != null) {
                team.setScore(team.getScore() + WINN_ADD_FOR_SCORE);
                persist.save(team);
                Team lose = play.getHomeTeam().getId() == team.getId() ? play.getAwayTeam() : play.getHomeTeam();
                if (lose.getScore() > WINN_ADD_FOR_SCORE) {
                    lose.setScore(lose.getScore() - WINN_ADD_FOR_SCORE);
                    persist.save(lose);
                }
            }
            play.setDoneDate(new Date());
            persist.save(play);
        }
    }

    public void updateProbabilities(League league) {
        List<Round> rounds = persist.loadList(Round.class).stream().filter(r -> r.getLeague().getId() == league.getId()).toList();
        for (Round round : rounds) {
            List<Play> plays = leagueService.getRoundPlays(round);
            for (Play play : plays) {
                if (play.getDoneDate() == null) {
                    play.updatePropobilty(leagueService.getProbabilities(play));
                    persist.save(play);
                }
            }
        }
    }

    public void generateGoalPlay(Play play) {
        MatchProbabilities matchProbabilities = leagueService.getProbabilities(play);
        double draw = matchProbabilities.getDraw();
        double homeWinnAway = matchProbabilities.getHomeTeamWin();
        double awayWinnHome = matchProbabilities.getAwayTeamWin();
        ////make the goal be fair and not only the better groups make goals
        double goalFair = Math.random();/// if this is less than 0.7 the goal is for the better groups so this is give us 0.3 probability for the less better groups make goals
        if (awayWinnHome > homeWinnAway) {
            if (goalFair < FAIR_FACTOR) {
                leagueService.createGoal(play, false, leagueService.getOwnerGoal(play.getAwayTeam()));
            } else {
                leagueService.createGoal(play, true, leagueService.getOwnerGoal(play.getHomeTeam()));
            }
        } else if (homeWinnAway > draw) {
            if (goalFair < FAIR_FACTOR) {
                leagueService.createGoal(play, true, leagueService.getOwnerGoal(play.getHomeTeam()));
            } else {
                leagueService.createGoal(play, false, leagueService.getOwnerGoal(play.getAwayTeam()));
            }
        }
    }


}
