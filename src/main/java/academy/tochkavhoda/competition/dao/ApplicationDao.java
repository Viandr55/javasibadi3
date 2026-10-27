package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;

import java.util.Collection;
import java.util.List;

public interface ApplicationDao {
    void insert(Application application) throws ServerException;

    Application getById(int id) throws ServerException;

    void delete(Application application) throws ServerException;

    List<Application> getAll();

    List<Application> getByDirections(Collection<String> directions);
}
