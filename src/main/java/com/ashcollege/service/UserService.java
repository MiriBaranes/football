package com.ashcollege.service;

import com.ashcollege.entities.BetsForm;
import com.ashcollege.entities.Play;
import com.ashcollege.entities.User;
import com.ashcollege.model.PlayWithBetUser;
import com.ashcollege.model.PlayModel;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.BetResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.utils.Errors;
import com.ashcollege.utils.PublicFunction;
import com.ashcollege.validator.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ashcollege.utils.Errors.THERE_IS_NOT_ENOUGH_MONEY;


@Component
@Transactional
public class UserService {
    public static final int PASSWORD_MAX_LENGTH = 10;
    public static final int PASSWORD_MIN_LENGTH = 5;

    @Autowired
    private Persist persist;
    @Autowired
    private LeagueService leagueService;

    public BasicResponse login(String email, String password) {
        String token = PublicFunction.createHash(email, password);
        User user = getUserByToken(token);
        if (user == null) {
            return new BasicResponse(false, Errors.ERROR_LOGIN_WRONG_CREDS);
        }
        user.setLastActivity(new Date());
        persist.save(user);
        User result =persist.loadObject(User.class,user.getId());
        return new LoginResponse(true, 200, result);
    }

    public User getUserByToken(String token) {
        return (User) persist.getQuerySession().createQuery(
                        "FROM User WHERE token = :token ")
                .setParameter("token", token)
                .setMaxResults(1)
                .uniqueResult();
    }

    public BasicResponse signIn(String email, String password) {
        String token = PublicFunction.createHash(email, password);
        if (isStrongPassword(password)) {
            if (EmailValidator.isValidEmail(email)) {
                if (!isExistUser(email, password)) {
                    User user = new User(email, password);
                    persist.save(user);
                    return new LoginResponse(true, 200, getUserByToken(token));
                }
                return new BasicResponse(false, Errors.ERROR_SIGN_UP_TOKEN_TAKEN);
            }
            return new BasicResponse(false, Errors.ERROR_EMAIL);
        }
        return new BasicResponse(false, Errors.ERROR_WEAK_PASSWORD);
    }

    public boolean isExistUser(String email, String password) {
        String token = PublicFunction.createHash(email, password);
        return persist.loadList(User.class).stream().anyMatch(user -> user.getToken().equals(token));
    }

    public boolean isExistUserEmail(String email) {
        return persist.loadList(User.class).stream().anyMatch(user -> user.getEmail().equals(email));
    }

    public boolean isStrongPassword(String password) {
        if (password.length() >= PASSWORD_MIN_LENGTH && password.length() <= PASSWORD_MAX_LENGTH) {
            boolean isDigit = false;
            boolean isLowerCase = false;
            boolean isUpperCase = false;
            for (int i = 0; i < password.length(); i++) {
                if (Character.isDigit(password.charAt(i))) {
                    isDigit = true;
                } else if (Character.isUpperCase(password.charAt(i))) {
                    isUpperCase = true;
                } else if (Character.isLowerCase(password.charAt(i))) {
                    isLowerCase = true;
                }
            }
            return isDigit && isLowerCase && isUpperCase;
        }
        return false;
    }

    public List<User> getTeamPlays() {
        List<User> list = new ArrayList<>(persist.loadList(User.class));
        return list;
    }

    public List<BetsForm> getUserBets(String token) {
        return persist.loadList(BetsForm.class).stream().filter(betsForm -> betsForm.getOwner().getToken().equals(token)).toList();
    }

    public BasicResponse addBet(int playId, int status, double money, String token) {
        User user = this.getUserByToken(token);
        Play play = persist.loadObject(Play.class, playId);
        List<BetsForm> userBetForm = getUserBets(token);

            if (userBetForm.stream().noneMatch(form -> form.getPlay().getId() == playId)) {
                if (user.getBalance() >= money) {
                    if (play.getRound().getStartTime().after(new Date())) {
                        BetsForm betsForm = new BetsForm(play, status, money, user);
                        user.setLastActivity(new Date());
                        user.setBalance(user.getBalance() - money);
                        persist.save(user);
                        persist.save(betsForm);
                        return new BetResponse(true, 200, betsForm);
                    } else {
                        return new BasicResponse(false, Errors.ROUND_START);
                    }
                }else  return new BasicResponse(false,THERE_IS_NOT_ENOUGH_MONEY);
            }else {
                List<BetsForm> valid = userBetForm.stream().filter(form -> form.getPlay().getId() == playId).toList();
                if (valid.isEmpty()) {
                    return new BasicResponse(false, 19);//NO VALID BET AT ALL
                } else {
                    if (play.getRound().getStartTime().after(new Date())) {
                        BetsForm betsForm = valid.get(0);
                        if (user.getBalance() + betsForm.getMoneyBet() - money>0){
                            user.setLastActivity(new Date());
                            betsForm.setBetType(status);
                            user.setBalance(user.getBalance() + betsForm.getMoneyBet() - money);
                            betsForm.setMoneyBet(money);
                            persist.save(user);
                            persist.save(betsForm);
                            return new BetResponse(true, 200, betsForm);
                        }else  return new BasicResponse(false,THERE_IS_NOT_ENOUGH_MONEY);
                    } else {
                        return new BasicResponse(false, Errors.ROUND_START);
                    }
                }
            }

    }

    public BasicResponse removeBet(int playId, String token) {
        List<BetsForm> userBetForm = this.getUserBets(token);
        List<BetsForm> resultPlay = userBetForm.stream().filter(form -> form.getPlay().getId() == playId).toList();
        if (!resultPlay.isEmpty()) {
            BetsForm betsForm = resultPlay.get(0);
            if (betsForm.getPlay().getRound().getStartTime().before(new Date())) {
                return new BasicResponse(false, Errors.ROUND_START);
            } else {
                User user=persist.loadObject(User.class,betsForm.getId());
                user.setBalance(user.getBalance() + betsForm.getMoneyBet());
                persist.save(user);
                persist.remove(betsForm);
                return new BasicResponse(true, 200);
            }
        } else return new BasicResponse(false, Errors.BET_NOT_FOUND);
    }

    public List<PlayWithBetUser> getActiveBet(String token) {
        List<BetsForm> userBets = getUserBets(token);
        List<PlayModel> playList = leagueService.getNextRoundPlay();
        List<PlayWithBetUser> betActiveForNextRounds = new ArrayList<>();
        for (PlayModel playModel : playList) {
            betActiveForNextRounds.add(new PlayWithBetUser(playModel, userBets));
        }
        return betActiveForNextRounds;
    }

}
