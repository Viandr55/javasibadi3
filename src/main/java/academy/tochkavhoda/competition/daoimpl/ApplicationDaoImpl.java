package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;

import java.util.Collection;
import java.util.List;

public class ApplicationDaoImpl implements ApplicationDao {
    private final Database database = Database.getInstance();

    @Override
    public void insert(Application application) throws ServerException {
        database.insertApplication(application);
    }

    @Override
    public Application getById(int id) throws ServerException {
        return database.getApplication(id);
    }

    @Override
    public void delete(Application application) throws ServerException {
        database.deleteApplication(application);
    }

    @Override
    public List<Application> getAll() {
        return database.getApplications();
    }

    @Override
    public List<Application> getByDirections(Collection<String> directions) {
        return database.getApplicationsByDirections(directions);
    }
}
