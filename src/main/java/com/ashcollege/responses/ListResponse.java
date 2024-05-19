package com.ashcollege.responses;

import java.util.List;

public class ListResponse extends BasicResponse{
    private List<?> list;

    public ListResponse(boolean success, Integer errorCode,List<?> list) {
        super(success, errorCode);
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
