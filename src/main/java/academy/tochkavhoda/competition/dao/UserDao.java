package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;

import java.util.List;

public interface UserDao {
    void insert(User user) throws ServerException;

    void login(String login, String password, String token) throws ServerException;

    void logout(String token) throws ServerException;

    User getByToken(String token) throws ServerException;

    void delete(User user);

    List<Participant> getParticipants();
}
