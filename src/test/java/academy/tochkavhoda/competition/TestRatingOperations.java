package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.RatingDtoRequest;
import academy.tochkavhoda.competition.dto.response.ApplicationsDtoResponse;
import academy.tochkavhoda.competition.dto.response.ServerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRatingOperations extends TestBase {
    private String participant;
    private String expert;
    private int applicationId;

    @BeforeEach
    public void init() {
        participant = registerAndLoginParticipant("ivanov", "Фирма");
        expert = registerAndLoginExpert("petrov", List.of("математика"));
        applicationId = addApplication(participant, "Проект", List.of("математика", "физика"), 100);
    }

    @Test
    public void testRateWrongData() {
        assertError(server.rateApplication(expert, gson.toJson(new RatingDtoRequest(applicationId, 0))),
                "Оценка должна быть в диапазоне 1..5");
        assertError(server.rateApplication(expert, gson.toJson(new RatingDtoRequest(applicationId, 6))),
                "Оценка должна быть в диапазоне 1..5");
        assertError(server.rateApplication(expert, gson.toJson(new RatingDtoRequest(999, 3))),
                "Заявка не найдена");
        assertError(server.rateApplication(participant, gson.toJson(new RatingDtoRequest(applicationId, 3))),
                "Операция доступна только эксперту");
    }

    @Test
    public void testRateOnlyOwnDirections() {
        int chemistry = addApplication(participant, "Химия", List.of("химия"), 100);
        assertError(server.rateApplication(expert, gson.toJson(new RatingDtoRequest(chemistry, 3))),
                "Эксперт не является специалистом по направлению");
    }

    @Test
    public void testRateChangeDelete() {
        rate(expert, applicationId, 3);
        assertError(server.rateApplication(expert, gson.toJson(new RatingDtoRequest(applicationId, 4))),
                "Оценка этой заявке уже поставлена");

        assertOk(server.changeRating(expert, gson.toJson(new RatingDtoRequest(applicationId, 5))));
        assertEquals(5, getRated(expert).getApplications().get(0).getRating());

        String deleteRequest = gson.toJson(new ApplicationIdDtoRequest(applicationId));
        assertOk(server.deleteRating(expert, deleteRequest));
        assertError(server.deleteRating(expert, deleteRequest), "Оценка не найдена");
        assertError(server.changeRating(expert, gson.toJson(new RatingDtoRequest(applicationId, 2))), "Оценка не найдена");
        assertTrue(getRated(expert).getApplications().isEmpty());
    }

    @Test
    public void testExpertLeaveRemovesRatings() {
        String other = registerAndLoginExpert("sidorov", List.of("физика"));
        rate(expert, applicationId, 5);
        rate(other, applicationId, 1);
        assertOk(server.leaveServer(expert));

        assertEquals(1, getRated(other).getApplications().size());
    }

    @Test
    public void testCancelApplicationRemovesRatings() {
        rate(expert, applicationId, 5);
        assertOk(server.cancelApplication(participant, gson.toJson(new ApplicationIdDtoRequest(applicationId))));
        assertTrue(getRated(expert).getApplications().isEmpty());
    }

    private ApplicationsDtoResponse getRated(String token) {
        ServerResponse response = server.getRatedApplications(token);
        assertOk(response);
        return gson.fromJson(response.getResponseData(), ApplicationsDtoResponse.class);
    }
}
