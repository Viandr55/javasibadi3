package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.DirectionsDtoRequest;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.ParticipantsDtoResponse;
import academy.tochkavhoda.competition.dto.response.ServerResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestUserOperations extends TestBase {

    @Test
    public void testRegisterParticipant() {
        ServerResponse response = server.registerParticipant(gson.toJson(
                new RegisterParticipantDtoRequest("Иван", "Иванов", "Рога и копыта", "ivanov", PASSWORD)));
        assertOk(response);
        assertEquals("{}", response.getResponseData());
    }

    @Test
    public void testRegisterParticipantWrongData() {
        assertError(server.registerParticipant(gson.toJson(
                        new RegisterParticipantDtoRequest("", "Иванов", "Фирма", "ivanov", PASSWORD))),
                "Имя не может быть пустым");
        assertError(server.registerParticipant(gson.toJson(
                        new RegisterParticipantDtoRequest("Иван", "Иванов", null, "ivanov", PASSWORD))),
                "Название фирмы не может быть пустым");
        assertError(server.registerParticipant(gson.toJson(
                        new RegisterParticipantDtoRequest("Иван", "Иванов", "Фирма", "iv", PASSWORD))),
                "Логин должен содержать не менее 4 символов: латинские буквы, цифры, '_'");
        assertError(server.registerParticipant(gson.toJson(
                        new RegisterParticipantDtoRequest("Иван", "Иванов", "Фирма", "ivanov", "password1"))),
                "Пароль должен содержать не менее 8 символов, хотя бы одну цифру и один спецсимвол");
        assertError(server.registerParticipant("{не json"), "Некорректный json запроса");
        assertError(server.registerParticipant(null), "Некорректный json запроса");
    }

    @Test
    public void testRegisterExpertWrongDirections() {
        assertError(server.registerExpert(gson.toJson(
                        new RegisterExpertDtoRequest("Пётр", "Петров", List.of(), "petrov", PASSWORD))),
                "Список направлений не может быть пустым");
        assertError(server.registerExpert(gson.toJson(
                        new RegisterExpertDtoRequest("Пётр", "Петров", List.of(" "), "petrov", PASSWORD))),
                "Название направления не может быть пустым");
    }

    @Test
    public void testDuplicateLogin() {
        registerAndLoginParticipant("ivanov", "Фирма");
        assertError(server.registerExpert(gson.toJson(
                        new RegisterExpertDtoRequest("Пётр", "Петров", List.of("математика"), "ivanov", PASSWORD))),
                "Этот логин уже используется");
    }

    @Test
    public void testLoginLogout() {
        String token = registerAndLoginParticipant("ivanov", "Фирма");
        assertError(server.login(gson.toJson(new LoginDtoRequest("ivanov", "wrong_pass1"))),
                "Неверный логин или пароль");

        assertOk(server.logout(token));
        assertError(server.logout(token), "Недействительный токен");
        assertError(server.getParticipants(token, null), "Недействительный токен");

        String newToken = login("ivanov");
        assertNotEquals(token, newToken);
        assertOk(server.getParticipants(newToken, null));
    }

    @Test
    public void testReloginInvalidatesOldToken() {
        String token = registerAndLoginParticipant("ivanov", "Фирма");
        String newToken = login("ivanov");
        assertError(server.getParticipants(token, null), "Недействительный токен");
        assertOk(server.getParticipants(newToken, null));
    }

    @Test
    public void testLeaveServer() {
        String participant = registerAndLoginParticipant("ivanov", "Фирма");
        String expert = registerAndLoginExpert("petrov", List.of("математика"));
        int id = addApplication(participant, "Проект", List.of("математика"), 100);

        assertOk(server.leaveServer(participant));
        assertError(server.login(gson.toJson(new LoginDtoRequest("ivanov", PASSWORD))), "Неверный логин или пароль");
        assertError(server.rateApplication(expert, "{\"applicationId\":" + id + ",\"rating\":5}"), "Заявка не найдена");

        // логин снова свободен
        registerAndLoginParticipant("ivanov", "Другая фирма");
    }

    @Test
    public void testGetParticipants() {
        String first = registerAndLoginParticipant("ivanov", "Фирма 1");
        String second = registerAndLoginParticipant("sidorov", "Фирма 2");
        addApplication(first, "А", List.of("математика"), 100);
        addApplication(second, "Б", List.of("физика"), 100);

        ServerResponse all = server.getParticipants(first, null);
        assertOk(all);
        assertEquals(2, gson.fromJson(all.getResponseData(), ParticipantsDtoResponse.class).getParticipants().size());

        ServerResponse filtered = server.getParticipants(first, gson.toJson(new DirectionsDtoRequest(List.of("Физика"))));
        assertOk(filtered);
        ParticipantsDtoResponse dto = gson.fromJson(filtered.getResponseData(), ParticipantsDtoResponse.class);
        assertEquals(1, dto.getParticipants().size());
        assertEquals("Фирма 2", dto.getParticipants().get(0).getCompanyName());
    }
}
