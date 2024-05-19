package com.ashcollege.model;

import com.ashcollege.entities.BetsForm;

import java.util.List;

public class PlayWithBetUser {
    private PlayModel playModel;
    private BetsForm userBets;

    public PlayWithBetUser(PlayModel playModel, List<BetsForm> betsForm) {
        this.playModel = playModel;
        List<BetsForm> betsForms= betsForm.stream().filter(betsForm1 -> betsForm1.getPlay().getId()== playModel.getId()).toList();
        if (betsForms.isEmpty()){
            this.userBets=null;
        }else{
            this.userBets=betsForms.get(0);
        }
    }

    public PlayModel getPlayModel() {
        return playModel;
    }

    public void setPlayModel(PlayModel playModel) {
        this.playModel = playModel;
    }

    public BetsForm getUserBets() {
        return userBets;
    }

    public void setUserBets(BetsForm userBets) {
        this.userBets = userBets;
    }
//    public boolean isUserBets() {
//        return userBets;
//    }
//
//    public void setUserBets(boolean userBets) {
//        this.userBets = userBets;
//    }
}
