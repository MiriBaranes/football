package com.ashcollege.responses;

import com.ashcollege.entities.BetsForm;

public class BetResponse extends BasicResponse{
    private BetsForm betsForm;

    public BetResponse(boolean success, Integer errorCode, BetsForm betsForm) {
        super(success, errorCode);
        this.betsForm = betsForm;
    }

    public BetsForm getBetsForm() {
        return betsForm;
    }

    public void setBetsForm(BetsForm betsForm) {
        this.betsForm = betsForm;
    }
}
