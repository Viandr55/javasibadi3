package academy.tochkavhoda.competition.database;

import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Database {
    private static final Database INSTANCE = new Database();

    // login -> пользователь
    private final Map<String, User> users = new LinkedHashMap<>();
    // token <-> пользователь (у пользователя не более одного действующего токена)
    private final BidiMap<String, User> sessions = new DualHashBidiMap<>();
    // id -> заявка
    private final Map<Integer, Application> applications = new LinkedHashMap<>();
    // направление -> заявки
    private final MultiValuedMap<String, Application> applicationsByDirection = new HashSetValuedHashMap<>();
    // заявка -> (эксперт -> оценка)
    private final Map<Application, Map<Expert, Integer>> ratings = new LinkedHashMap<>();

    private int nextApplicationId = 1;
    private boolean competitionFinished;

    private Database() {
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public void clear() {
        users.clear();
        sessions.clear();
        applications.clear();
        applicationsByDirection.clear();
        ratings.clear();
        nextApplicationId = 1;
        competitionFinished = false;
    }

    // ---------- пользователи ----------

    public void insertUser(User user) throws ServerException {
        if (users.putIfAbsent(user.getLogin(), user) != null) {
            throw new ServerException(ErrorCode.LOGIN_ALREADY_USED);
        }
    }

    public void login(String login, String password, String token) throws ServerException {
        User user = users.get(login);
        if (user == null || !user.getPassword().equals(password)) {
            throw new ServerException(ErrorCode.WRONG_LOGIN_OR_PASSWORD);
        }
        sessions.removeValue(user);
        sessions.put(token, user);
    }

    public void logout(String token) throws ServerException {
        if (sessions.remove(token) == null) {
            throw new ServerException(ErrorCode.WRONG_TOKEN);
        }
    }

    public User getUserByToken(String token) throws ServerException {
        User user = sessions.get(token);
        if (user == null) {
            throw new ServerException(ErrorCode.WRONG_TOKEN);
        }
        return user;
    }

    public void deleteUser(User user) {
        users.remove(user.getLogin());
        sessions.removeValue(user);
        if (user instanceof Participant participant) {
            List<Application> own = applications.values().stream()
                    .filter(application -> application.getParticipant() == participant)
                    .toList();
            own.forEach(this::removeApplication);
        } else if (user instanceof Expert expert) {
            ratings.values().forEach(expertRatings -> expertRatings.remove(expert));
        }
    }

    public List<Participant> getParticipants() {
        List<Participant> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user instanceof Participant participant) {
                result.add(participant);
            }
        }
        return result;
    }

    // ---------- заявки ----------

    public void insertApplication(Application application) throws ServerException {
        checkNotFinished();
        application.setId(nextApplicationId++);
        applications.put(application.getId(), application);
        for (String direction : application.getDirections()) {
            applicationsByDirection.put(direction, application);
        }
        ratings.put(application, new LinkedHashMap<>());
    }

    public Application getApplication(int id) throws ServerException {
        Application application = applications.get(id);
        if (application == null) {
            throw new ServerException(ErrorCode.APPLICATION_NOT_FOUND);
        }
        return application;
    }

    public void deleteApplication(Application application) throws ServerException {
        checkNotFinished();
        removeApplication(application);
    }

    public List<Application> getApplications() {
        return new ArrayList<>(applications.values());
    }

    public List<Application> getApplicationsByDirections(Collection<String> directions) {
        Set<Application> result = new HashSet<>();
        for (String direction : directions) {
            result.addAll(applicationsByDirection.get(direction));
        }
        List<Application> list = new ArrayList<>(result);
        list.sort(Comparator.comparingInt(Application::getId));
        return list;
    }

    private void removeApplication(Application application) {
        applications.remove(application.getId());
        for (String direction : application.getDirections()) {
            applicationsByDirection.removeMapping(direction, application);
        }
        ratings.remove(application);
    }

    // ---------- оценки ----------

    public void insertRating(Application application, Expert expert, int rating) throws ServerException {
        checkNotFinished();
        Map<Expert, Integer> expertRatings = getRatingsMap(application);
        if (expertRatings.containsKey(expert)) {
            throw new ServerException(ErrorCode.RATING_ALREADY_EXISTS);
        }
        expertRatings.put(expert, rating);
    }

    public void updateRating(Application application, Expert expert, int rating) throws ServerException {
        checkNotFinished();
        Map<Expert, Integer> expertRatings = getRatingsMap(application);
        if (!expertRatings.containsKey(expert)) {
            throw new ServerException(ErrorCode.RATING_NOT_FOUND);
        }
        expertRatings.put(expert, rating);
    }

    public void deleteRating(Application application, Expert expert) throws ServerException {
        checkNotFinished();
        if (getRatingsMap(application).remove(expert) == null) {
            throw new ServerException(ErrorCode.RATING_NOT_FOUND);
        }
    }

    public List<Integer> getRatings(Application application) throws ServerException {
        return new ArrayList<>(getRatingsMap(application).values());
    }

    public Map<Application, Integer> getRatingsByExpert(Expert expert) {
        Map<Application, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Application, Map<Expert, Integer>> entry : ratings.entrySet()) {
            Integer rating = entry.getValue().get(expert);
            if (rating != null) {
                result.put(entry.getKey(), rating);
            }
        }
        return result;
    }

    private Map<Expert, Integer> getRatingsMap(Application application) throws ServerException {
        Map<Expert, Integer> expertRatings = ratings.get(application);
        if (expertRatings == null) {
            throw new ServerException(ErrorCode.APPLICATION_NOT_FOUND);
        }
        return expertRatings;
    }

    // ---------- состояние конкурса ----------

    public void finishCompetition() throws ServerException {
        checkNotFinished();
        competitionFinished = true;
    }

    private void checkNotFinished() throws ServerException {
        if (competitionFinished) {
            throw new ServerException(ErrorCode.COMPETITION_FINISHED);
        }
    }
}
