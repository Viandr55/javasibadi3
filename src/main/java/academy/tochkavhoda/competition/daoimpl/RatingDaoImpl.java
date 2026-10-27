package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;

import java.util.List;
import java.util.Map;

public class RatingDaoImpl implements RatingDao {
    private final Database database = Database.getInstance();

    @Override
    public void insert(Application application, Expert expert, int rating) throws ServerException {
        database.insertRating(application, expert, rating);
    }

    @Override
    public void update(Application application, Expert expert, int rating) throws ServerException {
        database.updateRating(application, expert, rating);
    }

    @Override
    public void delete(Application application, Expert expert) throws ServerException {
        database.deleteRating(application, expert);
    }

    @Override
    public List<Integer> getRatings(Application application) throws ServerException {
        return database.getRatings(application);
    }

    @Override
    public Map<Application, Integer> getByExpert(Expert expert) {
        return database.getRatingsByExpert(expert);
    }
}
