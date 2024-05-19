package com.ashcollege.model;

import com.ashcollege.entities.Play;

public class PlayOdds {
    private double homeOdds;
    private double drawOdds;
    private double awayOdds;

    public PlayOdds(Play play) {
        this.homeOdds = 1/play.getHomeTeamWin();
        this.drawOdds =  1/play.getDraw();
        this.awayOdds =  1/play.getAwayTeamWin();
    }

    public double getHomeOdds() {
        return homeOdds;
    }

    public void setHomeOdds(double homeOdds) {
        this.homeOdds = homeOdds;
    }

    public double getDrawOdds() {
        return drawOdds;
    }

    public void setDrawOdds(double drawOdds) {
        this.drawOdds = drawOdds;
    }

    public double getAwayOdds() {
        return awayOdds;
    }

    public void setAwayOdds(double awayOdds) {
        this.awayOdds = awayOdds;
    }
}
