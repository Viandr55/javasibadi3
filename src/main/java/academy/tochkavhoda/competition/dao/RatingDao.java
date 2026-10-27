package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;

import java.util.List;
import java.util.Map;

public interface RatingDao {
    void insert(Application application, Expert expert, int rating) throws ServerException;

    void update(Application application, Expert expert, int rating) throws ServerException;

    void delete(Application application, Expert expert) throws ServerException;

    List<Integer> getRatings(Application application) throws ServerException;

    Map<Application, Integer> getByExpert(Expert expert);
}
