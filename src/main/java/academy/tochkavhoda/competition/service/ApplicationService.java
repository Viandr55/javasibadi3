package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.RatingDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.AddApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.DirectionsDtoRequest;
import academy.tochkavhoda.competition.dto.response.AddApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ApplicationsDtoResponse;
import academy.tochkavhoda.competition.dto.response.EmptyDtoResponse;
import academy.tochkavhoda.competition.dto.response.ServerResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApplicationService extends ServiceBase {
    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();
    private final RatingDao ratingDao = new RatingDaoImpl();

    public ServerResponse addApplication(String token, String requestJsonString) {
        try {
            Participant participant = asParticipant(userDao.getByToken(token));
            AddApplicationDtoRequest request = fromJson(requestJsonString, AddApplicationDtoRequest.class);
            Set<String> directions = validate(request);
            Application application = new Application(request.getTitle().trim(), request.getDescription().trim(),
                    directions, request.getRequestedAmount(), participant);
            applicationDao.insert(application);
            return success(new AddApplicationDtoResponse(application.getId()));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse cancelApplication(String token, String requestJsonString) {
        try {
            Participant participant = asParticipant(userDao.getByToken(token));
            ApplicationIdDtoRequest request = fromJson(requestJsonString, ApplicationIdDtoRequest.class);
            Application application = applicationDao.getById(request.getApplicationId());
            if (application.getParticipant() != participant) {
                throw new ServerException(ErrorCode.NOT_OWN_APPLICATION);
            }
            applicationDao.delete(application);
            return success(new EmptyDtoResponse());
        } catch (ServerException e) {
            return error(e);
        }
    }

    // заявки по направлениям, которые должны входить в число направлений эксперта
    public ServerResponse getApplicationsByDirections(String token, String requestJsonString) {
        try {
            Expert expert = asExpert(userDao.getByToken(token));
            DirectionsDtoRequest request = fromJson(requestJsonString, DirectionsDtoRequest.class);
            Set<String> directions = normalizeDirections(request.getDirections());
            if (!expert.getDirections().containsAll(directions)) {
                throw new ServerException(ErrorCode.DIRECTION_NOT_OF_EXPERT);
            }
            Map<Application, Integer> expertRatings = ratingDao.getByExpert(expert);
            List<ApplicationDtoResponse> result = new ArrayList<>();
            for (Application application : applicationDao.getByDirections(directions)) {
                result.add(toDto(application, expertRatings.get(application)));
            }
            return success(new ApplicationsDtoResponse(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    private static Set<String> validate(AddApplicationDtoRequest request) throws ServerException {
        checkNotBlank(request.getTitle(), ErrorCode.EMPTY_TITLE);
        checkNotBlank(request.getDescription(), ErrorCode.EMPTY_DESCRIPTION);
        Set<String> directions = normalizeDirections(request.getDirections());
        if (request.getRequestedAmount() <= 0) {
            throw new ServerException(ErrorCode.WRONG_AMOUNT);
        }
        return directions;
    }
}
