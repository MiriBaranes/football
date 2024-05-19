package com.ashcollege.entities;

import com.ashcollege.utils.PublicFunction;

import java.util.Date;
import java.util.List;

public class User {
    private int id;
    private String email;
    private String token;
    private double balance;
//    private List<Bet> bets;
    private Date lastActivity;

    public User(int id, String username,String password) {
        this(username, password);
        this.id = id;
        this.lastActivity=new Date();
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    public User(String email, String password) {
        this.email = email;
        this.token = PublicFunction.createHash(email,password);
        this.lastActivity= new Date();
    }
    public User (User other){
        this.email= other.email;
        this.token= other.token;
        this.balance= other.balance;
        if (other.lastActivity!=null){
            this.lastActivity=other.lastActivity;
        }
        else {
            this.lastActivity=new Date();
        }
        this.id= other.id;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

//    public List<Bet> getBets() {
//        return bets;
//    }
//
//    public void setBets(List<Bet> bets) {
//        this.bets = bets;
//    }


}

