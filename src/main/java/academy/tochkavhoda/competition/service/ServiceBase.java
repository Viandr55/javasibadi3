package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.dto.response.ServerResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

abstract class ServiceBase {
    protected static final Gson GSON = new Gson();

    protected static <T> T fromJson(String json, Class<T> dtoClass) throws ServerException {
        try {
            T dto = GSON.fromJson(json, dtoClass);
            if (dto == null) {
                throw new ServerException(ErrorCode.WRONG_JSON);
            }
            return dto;
        } catch (JsonParseException e) {
            throw new ServerException(ErrorCode.WRONG_JSON);
        }
    }

    protected static ServerResponse success(Object dto) {
        return new ServerResponse(ServerResponse.OK, GSON.toJson(dto));
    }

    protected static ServerResponse error(ServerException e) {
        return new ServerResponse(ServerResponse.ERROR, GSON.toJson(new ErrorDtoResponse(e.getMessage())));
    }

    protected static void checkNotBlank(String value, ErrorCode errorCode) throws ServerException {
        if (value == null || value.isBlank()) {
            throw new ServerException(errorCode);
        }
    }

    // направления сравниваются без учёта регистра и пробелов по краям
    protected static Set<String> normalizeDirections(List<String> directions) throws ServerException {
        if (directions == null || directions.isEmpty()) {
            throw new ServerException(ErrorCode.EMPTY_DIRECTIONS);
        }
        Set<String> result = new LinkedHashSet<>();
        for (String direction : directions) {
            checkNotBlank(direction, ErrorCode.WRONG_DIRECTION);
            result.add(direction.trim().toLowerCase(Locale.ROOT));
        }
        return result;
    }

    protected static Participant asParticipant(User user) throws ServerException {
        if (user instanceof Participant participant) {
            return participant;
        }
        throw new ServerException(ErrorCode.NOT_PARTICIPANT);
    }

    protected static Expert asExpert(User user) throws ServerException {
        if (user instanceof Expert expert) {
            return expert;
        }
        throw new ServerException(ErrorCode.NOT_EXPERT);
    }

    protected static ApplicationDtoResponse toDto(Application application, Integer rating) {
        return new ApplicationDtoResponse(application.getId(), application.getTitle(), application.getDescription(),
                new ArrayList<>(application.getDirections()), application.getRequestedAmount(),
                application.getParticipant().getCompanyName(), rating);
    }
}
