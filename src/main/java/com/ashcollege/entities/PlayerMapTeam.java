package com.ashcollege.entities;

import java.util.Date;

public class PlayerMapTeam {
    private int id;
    private Player player;
    private Team team;
    private Date created;
    private Date deleted;

    public Player getPlayer() {
        return player;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "PlayerMapTeam{" +
                "id=" + id +
                ", player=" + player.getFirstName() +" "+player.getLastName() +
                ", created="  +
                ", deleted="  +
                '}';
    }
}
