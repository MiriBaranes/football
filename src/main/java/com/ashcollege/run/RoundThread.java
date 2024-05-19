package com.ashcollege.run;

import com.ashcollege.utils.PageConst;
import com.ashcollege.entities.Play;
import com.ashcollege.entities.Round;
import com.ashcollege.entities.UserEvent;
import com.ashcollege.generetor.LeagueGenerator;
import com.ashcollege.service.LeagueService;
import com.ashcollege.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RoundThread extends MyRunnable {
    public static final double RANDOM_PROBABILITY_TO_DO_GOAL = 0.85;
    public static final double TIME_FOR_GAME = 30; // IN SEC
    public static final long MILL_IN_SEC = 1000;

    private final Round round;
    private final UserService userService;
    private final List<Play> plays;
    private final LeagueGenerator leagueGenerator;
    private final List<UserEvent> userEvents;
    private final LeagueService leagueService;

    public RoundThread(Round round, List<Play> plays, LeagueGenerator leagueGenerator,
                       List<UserEvent> userEvents, LeagueService leagueService, UserService userService) {
        this.round = round;
        this.plays = plays;
        this.leagueGenerator = leagueGenerator;
        this.userEvents = userEvents;
        this.leagueService = leagueService;
        this.userService = userService;
    }

    @Override
    public void _run() {
        Date date = round.getStartTime();
        long duration = (long) (TIME_FOR_GAME * MILL_IN_SEC);
        Date currentDate = new Date();
        long elapsedTime = currentDate.getTime() - date.getTime();
        if (elapsedTime < duration) {
            processPlays();
        } else {
            endGame();
        }
    }

    private void processPlays() {
        for (Play play : plays) {
            double randomNumber = Math.random();
            if (randomNumber > RANDOM_PROBABILITY_TO_DO_GOAL) {
                leagueGenerator.generateGoalPlay(play);
                updateOnePlay();
            }
        }
    }

    private void endGame() {
        leagueGenerator.updatePlayWinnerScore(plays, round);
        plays.forEach(leagueService::updateBetsOfGame);
        leagueGenerator.updateProbabilities(round.getLeague());
        updateInGameEnding();
        stop();
    }

    private void updateOnePlay() {
        List<UserEvent> toRemove = new ArrayList<>();
        synchronized (userEvents) {
            List<UserEvent> thisEvents = getUserEventsByEndOrUpdate(true);
            synchronized (thisEvents) {
                for (UserEvent userEvent : thisEvents) {
                    processUserEvent(userEvent, toRemove);
                }
                userEvents.removeAll(toRemove);
            }
        }
    }

    public List<UserEvent> getUserEventsByEndOrUpdate(boolean onPlay) {
        if (!onPlay) {
            return userEvents.stream().filter(userEvent -> userEvent.getType().equals(PageConst.ALL_TEAMS_PAGE + "") ||
                    userEvent.getType().equals(PageConst.ACTIVE_TEAM_PAGE + "") ||
                    userEvent.getType().equals(PageConst.PAST_GAMES_PAGE + "") ||
                    userEvent.getType().equals(PageConst.USER_BET_PAGE + "") || userEvent.getType().equals(PageConst.NEXT_ROUND_GAMES_PAGE + "")).toList();
        } else {
            return userEvents.stream().filter(userEvent -> userEvent.getType().equals(PageConst.ALL_TEAMS_PAGE+"") ||
                    userEvent.getType().equals(PageConst.ACTIVE_TEAM_PAGE+"") || userEvent.getType().equals(PageConst.USER_BET_PAGE+"")).toList();
        }
    }

    private void processUserEvent(UserEvent userEvent, List<UserEvent> toRemove) {
        try {
            List<?> list = switch (userEvent.getType()) {
                case PageConst.ALL_TEAMS_PAGE+"" -> leagueService.getTeamModels();
                case PageConst.ACTIVE_TEAM_PAGE+"" -> leagueService.activePlays();
                case PageConst.USER_BET_PAGE+"" -> userService.getUserBets(userEvent.getToken());
                default -> Collections.emptyList();
            };
            userEvent.getSseEmitter().send(list);
        } catch (IllegalStateException | IOException e) {
            toRemove.add(userEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateInGameEnding() {
        List<UserEvent> toRemove = new ArrayList<>();
        synchronized (userEvents) {
            List<UserEvent> thisEvents = getUserEventsByEndOrUpdate(false);
            synchronized (thisEvents) {
                for (UserEvent userEvent : thisEvents) {
                    processEndingUserEvent(userEvent, toRemove);
                }
                userEvents.removeAll(toRemove);
            }
        }
    }

    private void processEndingUserEvent(UserEvent userEvent, List<UserEvent> toRemove) {
        try {
            List<?> list = switch (userEvent.getType()) {
                case PageConst.ALL_TEAMS_PAGE+"" -> leagueService.getTeamModels();
                case PageConst.ACTIVE_TEAM_PAGE+"" -> Collections.emptyList();
                case PageConst.PAST_GAMES_PAGE+"" -> leagueService.pastGames();
                case PageConst.NEXT_ROUND_GAMES_PAGE+"" -> userService.getActiveBet(userEvent.getToken());
                case PageConst.USER_BET_PAGE+"" -> userService.getUserBets(userEvent.getToken());
                default -> throw new IllegalStateException("Unexpected value: " + userEvent.getType());
            };
            userEvent.getSseEmitter().send(list);
        } catch (IllegalStateException e) {
            toRemove.add(userEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}