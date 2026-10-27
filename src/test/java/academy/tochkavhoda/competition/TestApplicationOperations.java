package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.AddApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.DirectionsDtoRequest;
import academy.tochkavhoda.competition.dto.response.ApplicationsDtoResponse;
import academy.tochkavhoda.competition.dto.response.ServerResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestApplicationOperations extends TestBase {

    @Test
    public void testAddApplicationWrongData() {
        String token = registerAndLoginParticipant("ivanov", "Фирма");
        assertError(server.addApplication(token, gson.toJson(
                new AddApplicationDtoRequest("", "Описание", List.of("математика"), 100))), "Название заявки не может быть пустым");
        assertError(server.addApplication(token, gson.toJson(
                new AddApplicationDtoRequest("Проект", "Описание", null, 100))), "Список направлений не может быть пустым");
        assertError(server.addApplication(token, gson.toJson(
                new AddApplicationDtoRequest("Проект", "Описание", List.of("математика"), 0))), "Сумма должна быть положительной");
        assertError(server.addApplication("bad-token", gson.toJson(
                new AddApplicationDtoRequest("Проект", "Описание", List.of("математика"), 100))), "Недействительный токен");
    }

    @Test
    public void testExpertCannotAddApplication() {
        String expert = registerAndLoginExpert("petrov", List.of("математика"));
        assertError(server.addApplication(expert, gson.toJson(
                new AddApplicationDtoRequest("Проект", "Описание", List.of("математика"), 100))), "Операция доступна только участнику");
    }

    @Test
    public void testCancelApplication() {
        String owner = registerAndLoginParticipant("ivanov", "Фирма 1");
        String other = registerAndLoginParticipant("sidorov", "Фирма 2");
        int id = addApplication(owner, "Проект", List.of("математика"), 100);

        String request = gson.toJson(new ApplicationIdDtoRequest(id));
        assertError(server.cancelApplication(other, request), "Можно отменить только свою заявку");
        assertOk(server.cancelApplication(owner, request));
        assertError(server.cancelApplication(owner, request), "Заявка не найдена");
    }

    @Test
    public void testGetApplicationsByDirections() {
        String participant = registerAndLoginParticipant("ivanov", "Фирма");
        String expert = registerAndLoginExpert("petrov", List.of("Математика", "информатика"));
        int math = addApplication(participant, "А", List.of("математика"), 100);
        int both = addApplication(participant, "Б", List.of("математика", "информатика"), 200);
        addApplication(participant, "В", List.of("физика"), 300);
        rate(expert, math, 4);

        ServerResponse response = server.getApplicationsByDirections(expert,
                gson.toJson(new DirectionsDtoRequest(List.of("математика", "информатика"))));
        assertOk(response);
        ApplicationsDtoResponse dto = gson.fromJson(response.getResponseData(), ApplicationsDtoResponse.class);
        assertEquals(2, dto.getApplications().size());
        assertEquals(math, dto.getApplications().get(0).getApplicationId());
        assertEquals(4, dto.getApplications().get(0).getRating());
        assertEquals(both, dto.getApplications().get(1).getApplicationId());
        assertNull(dto.getApplications().get(1).getRating());

        assertError(server.getApplicationsByDirections(expert,
                gson.toJson(new DirectionsDtoRequest(List.of("физика")))), "Эксперт не является специалистом по направлению");
        assertError(server.getApplicationsByDirections(participant,
                gson.toJson(new DirectionsDtoRequest(List.of("математика")))), "Операция доступна только эксперту");
    }
}
