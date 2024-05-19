package com.ashcollege.controllers;

import com.ashcollege.entities.BetsForm;
import com.ashcollege.entities.User;
import com.ashcollege.model.PlayWithBetUser;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.responses.ObjectResponse;
import com.ashcollege.service.LeagueService;
import com.ashcollege.service.Persist;
import com.ashcollege.service.UserService;
import com.ashcollege.utils.Errors;
import com.ashcollege.utils.PublicFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api-users")
public class UserController {
    @Autowired
    private Persist persist;
    @Autowired
    private UserService userService;
    @Autowired
    private LeagueService leagueService;

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object hello() {
        return "Hello From Server";
    }

    @RequestMapping(value = "/sign-in", method = {RequestMethod.GET, RequestMethod.POST})
    public Object signIn(String email, String password) {
        return userService.signIn(email, password);
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public Object login(String email, String password) {
        return userService.login(email, password);
    }

    @RequestMapping(value = "/get-user-by-token", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getUserByToken(String token) {
        return userService.getUserByToken(token);
    }

    @RequestMapping(value = "/set-user-password", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse setUserPassword(String oldPassword, String newPassword, String email, String token) {
        if (PublicFunction.createHash(email, oldPassword).equals(token)) {
            if (userService.isStrongPassword(newPassword)) {
                String newToken = PublicFunction.createHash(email, newPassword);
                User user = userService.getUserByToken(token);
                user.setToken(newToken);
                user.setLastActivity(new Date());
                persist.save(user);
                return new LoginResponse(true, 200, userService.getUserByToken(newToken));
            }
            return new BasicResponse(false, Errors.ERROR_WEAK_PASSWORD);
        }
        return new BasicResponse(false, Errors.PASSWORD_NOT_RIGHT_FOR_EMAIL);
    }

    @RequestMapping(value = "/set-user-email", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse setUserEmail(String oldEmail, String password, String newEmail, String token) {
        if (PublicFunction.createHash(oldEmail, password).equals(token)) {
            if (!userService.isExistUserEmail(newEmail)) {
                String newToken = PublicFunction.createHash(newEmail, password);
                User user = userService.getUserByToken(token);
                user.setEmail(newEmail);
                user.setToken(newToken);
                user.setLastActivity(new Date());
                persist.save(user);
                return new LoginResponse(true, 200, userService.getUserByToken(newToken));
            }
            return new BasicResponse(false, Errors.THIS_EMAIL_TOKED);
        }
        return new BasicResponse(false, Errors.PASSWORD_NOT_RIGHT_FOR_EMAIL);
    }

    @RequestMapping(value = "/add-bet", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse addBet(int playId, int status, double money, String token) {
        return userService.addBet(playId, status, money, token);
    }
    @RequestMapping(value = "/remove-bet", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse removeBet(int playId, String token) {
        return userService.removeBet(playId, token);
    }

    @RequestMapping(value = "/get-bets", method = {RequestMethod.GET, RequestMethod.POST})
    public List<BetsForm> getBets(String token) {
        return userService.getUserBets(token);
    }

    @RequestMapping(value = "/get-active-bet", method = {RequestMethod.GET, RequestMethod.POST})
    public List<PlayWithBetUser> getActiveBet(String token) {
        return userService.getActiveBet(token);
    }
    @RequestMapping(value = "/get-user-balance", method = {RequestMethod.GET, RequestMethod.POST})
    public double getUserBalance(String token) {
        return userService.getUserByToken(token).getBalance();
    }
    @RequestMapping(value = "/set-user-balance", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse setUserBalance(String token,double balance) {
        if (balance<=0){
            return new BasicResponse(false,Errors.NOT_VALID_BALANCE);
        }
        User user= userService.getUserByToken(token);
        if (user!=null){
            double oldBalance= user.getBalance();
            double newBalance=oldBalance + balance;
            user.setBalance(newBalance);
            persist.save(user);
            return new ObjectResponse<>(true,200,persist.loadObject(User.class,user.getId()));
        }
        else {
          return new BasicResponse(false,Errors.ERROR_NO_SUCH_USER);
        }
    }



}
