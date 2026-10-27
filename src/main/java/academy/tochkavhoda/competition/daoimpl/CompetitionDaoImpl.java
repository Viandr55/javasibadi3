package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.CompetitionDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.exception.ServerException;

public class CompetitionDaoImpl implements CompetitionDao {
    private final Database database = Database.getInstance();

    @Override
    public void finish() throws ServerException {
        database.finishCompetition();
    }

    @Override
    public void clear() {
        database.clear();
    }
}
