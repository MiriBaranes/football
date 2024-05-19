package com.ashcollege.responses;

public class ObjectResponse <T> extends BasicResponse{
    private T obj;
    public ObjectResponse(boolean success, Integer errorCode,T obj) {
        super(success, errorCode);
        this.obj= obj;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
