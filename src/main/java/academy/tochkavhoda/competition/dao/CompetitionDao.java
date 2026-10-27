package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;

public interface CompetitionDao {
    void finish() throws ServerException;

    void clear();
}
