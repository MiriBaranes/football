package com.ashcollege.controllers;

import com.ashcollege.entities.Play;
import com.ashcollege.service.LeagueService;
import com.ashcollege.service.Persist;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeneralController {

    @Autowired
    private LeagueService leagueService;

    @Autowired
    private Persist persist;

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object hello() {
        return "Hello From Server";
    }
    @RequestMapping(value = "/get-all", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Play> getAll() {
        return leagueService.getPlayTeam(5);
    }


}
