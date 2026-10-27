package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.DirectionsDtoRequest;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.EmptyDtoResponse;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import academy.tochkavhoda.competition.dto.response.ParticipantDtoResponse;
import academy.tochkavhoda.competition.dto.response.ParticipantsDtoResponse;
import academy.tochkavhoda.competition.dto.response.ServerResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class UserService extends ServiceBase {
    private static final Pattern LOGIN_PATTERN = Pattern.compile("[A-Za-z0-9_]{4,}");
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();

    public ServerResponse registerParticipant(String requestJsonString) {
        try {
            RegisterParticipantDtoRequest request = fromJson(requestJsonString, RegisterParticipantDtoRequest.class);
            validate(request);
            userDao.insert(new Participant(request.getFirstName().trim(), request.getLastName().trim(),
                    request.getCompanyName().trim(), request.getLogin(), request.getPassword()));
            return success(new EmptyDtoResponse());
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse registerExpert(String requestJsonString) {
        try {
            RegisterExpertDtoRequest request = fromJson(requestJsonString, RegisterExpertDtoRequest.class);
            Set<String> directions = validate(request);
            userDao.insert(new Expert(request.getFirstName().trim(), request.getLastName().trim(),
                    directions, request.getLogin(), request.getPassword()));
            return success(new EmptyDtoResponse());
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse login(String requestJsonString) {
        try {
            LoginDtoRequest request = fromJson(requestJsonString, LoginDtoRequest.class);
            if (request.getLogin() == null || request.getPassword() == null) {
                throw new ServerException(ErrorCode.WRONG_LOGIN_OR_PASSWORD);
            }
            String token = UUID.randomUUID().toString();
            userDao.login(request.getLogin(), request.getPassword(), token);
            return success(new LoginDtoResponse(token));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse logout(String token) {
        try {
            userDao.logout(token);
            return success(new EmptyDtoResponse());
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse leaveServer(String token) {
        try {
            userDao.delete(userDao.getByToken(token));
            return success(new EmptyDtoResponse());
        } catch (ServerException e) {
            return error(e);
        }
    }

    // requestJsonString может отсутствовать - тогда возвращаются все участники,
    // иначе - участники, подавшие заявки хотя бы по одному из указанных направлений
    public ServerResponse getParticipants(String token, String requestJsonString) {
        try {
            userDao.getByToken(token);
            Collection<Participant> participants;
            if (requestJsonString == null || requestJsonString.isBlank()) {
                participants = userDao.getParticipants();
            } else {
                DirectionsDtoRequest request = fromJson(requestJsonString, DirectionsDtoRequest.class);
                Set<String> directions = normalizeDirections(request.getDirections());
                participants = new LinkedHashSet<>();
                for (Application application : applicationDao.getByDirections(directions)) {
                    participants.add(application.getParticipant());
                }
            }
            List<ParticipantDtoResponse> result = new ArrayList<>();
            for (Participant participant : participants) {
                result.add(new ParticipantDtoResponse(participant.getFirstName(), participant.getLastName(),
                        participant.getCompanyName()));
            }
            return success(new ParticipantsDtoResponse(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    private static void validate(RegisterParticipantDtoRequest request) throws ServerException {
        checkNotBlank(request.getFirstName(), ErrorCode.EMPTY_FIRST_NAME);
        checkNotBlank(request.getLastName(), ErrorCode.EMPTY_LAST_NAME);
        checkNotBlank(request.getCompanyName(), ErrorCode.EMPTY_COMPANY_NAME);
        validateCredentials(request.getLogin(), request.getPassword());
    }

    private static Set<String> validate(RegisterExpertDtoRequest request) throws ServerException {
        checkNotBlank(request.getFirstName(), ErrorCode.EMPTY_FIRST_NAME);
        checkNotBlank(request.getLastName(), ErrorCode.EMPTY_LAST_NAME);
        Set<String> directions = normalizeDirections(request.getDirections());
        validateCredentials(request.getLogin(), request.getPassword());
        return directions;
    }

    private static void validateCredentials(String login, String password) throws ServerException {
        if (login == null || !LOGIN_PATTERN.matcher(login).matches()) {
            throw new ServerException(ErrorCode.WRONG_LOGIN);
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH
                || password.chars().noneMatch(Character::isDigit)
                || password.chars().allMatch(Character::isLetterOrDigit)) {
            throw new ServerException(ErrorCode.WRONG_PASSWORD);
        }
    }
}
