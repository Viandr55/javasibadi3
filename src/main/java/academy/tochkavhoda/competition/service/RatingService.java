package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.RatingDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.RatingDtoRequest;
import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ApplicationsDtoResponse;
import academy.tochkavhoda.competition.dto.response.EmptyDtoResponse;
import academy.tochkavhoda.competition.dto.response.ServerResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RatingService extends ServiceBase {
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();
    private final RatingDao ratingDao = new RatingDaoImpl();

    public ServerResponse rateApplication(String token, String requestJsonString) {
        try {
            Expert expert = asExpert(userDao.getByToken(token));
            RatingDtoRequest request = fromJson(requestJsonString, RatingDtoRequest.class);
            validate(request);
            ratingDao.insert(getApplicationForExpert(request.getApplicationId(), expert), expert, request.getRating());
            return success(new EmptyDtoResponse());
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse changeRating(String token, String requestJsonString) {
        try {
            Expert expert = asExpert(userDao.getByToken(token));
            RatingDtoRequest request = fromJson(requestJsonString, RatingDtoRequest.class);
            validate(request);
            ratingDao.update(getApplicationForExpert(request.getApplicationId(), expert), expert, request.getRating());
            return success(new EmptyDtoResponse());
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse deleteRating(String token, String requestJsonString) {
        try {
            Expert expert = asExpert(userDao.getByToken(token));
            ApplicationIdDtoRequest request = fromJson(requestJsonString, ApplicationIdDtoRequest.class);
            ratingDao.delete(applicationDao.getById(request.getApplicationId()), expert);
            return success(new EmptyDtoResponse());
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse getRatedApplications(String token) {
        try {
            Expert expert = asExpert(userDao.getByToken(token));
            List<ApplicationDtoResponse> result = new ArrayList<>();
            for (Map.Entry<Application, Integer> entry : ratingDao.getByExpert(expert).entrySet()) {
                result.add(toDto(entry.getKey(), entry.getValue()));
            }
            return success(new ApplicationsDtoResponse(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    // эксперт оценивает только заявки, имеющие хотя бы одно его направление
    private Application getApplicationForExpert(int applicationId, Expert expert) throws ServerException {
        Application application = applicationDao.getById(applicationId);
        if (!CollectionUtils.containsAny(expert.getDirections(), application.getDirections())) {
            throw new ServerException(ErrorCode.DIRECTION_NOT_OF_EXPERT);
        }
        return application;
    }

    private static void validate(RatingDtoRequest request) throws ServerException {
        if (request.getRating() < MIN_RATING || request.getRating() > MAX_RATING) {
            throw new ServerException(ErrorCode.WRONG_RATING);
        }
    }
}
