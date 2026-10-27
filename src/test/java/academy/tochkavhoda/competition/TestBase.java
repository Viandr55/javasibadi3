package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.AddApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RatingDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.AddApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import academy.tochkavhoda.competition.dto.response.ServerResponse;
import academy.tochkavhoda.competition.server.Server;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TestBase {
    protected static final String PASSWORD = "secret_123";

    protected final Gson gson = new Gson();
    protected Server server;

    @BeforeEach
    public void startServer() {
        server = new Server();
        server.startServer();
    }

    protected String registerAndLoginParticipant(String login, String companyName) {
        assertOk(server.registerParticipant(gson.toJson(
                new RegisterParticipantDtoRequest("Иван", "Иванов", companyName, login, PASSWORD))));
        return login(login);
    }

    protected String registerAndLoginExpert(String login, List<String> directions) {
        assertOk(server.registerExpert(gson.toJson(
                new RegisterExpertDtoRequest("Пётр", "Петров", directions, login, PASSWORD))));
        return login(login);
    }

    protected String login(String login) {
        ServerResponse response = server.login(gson.toJson(new LoginDtoRequest(login, PASSWORD)));
        assertOk(response);
        return gson.fromJson(response.getResponseData(), LoginDtoResponse.class).getToken();
    }

    protected int addApplication(String token, String title, List<String> directions, long amount) {
        ServerResponse response = server.addApplication(token, gson.toJson(
                new AddApplicationDtoRequest(title, "Описание " + title, directions, amount)));
        assertOk(response);
        return gson.fromJson(response.getResponseData(), AddApplicationDtoResponse.class).getApplicationId();
    }

    protected void rate(String expertToken, int applicationId, int rating) {
        assertOk(server.rateApplication(expertToken, gson.toJson(new RatingDtoRequest(applicationId, rating))));
    }

    protected static void assertOk(ServerResponse response) {
        assertEquals(200, response.getResponseCode(), response.getResponseData());
    }

    protected void assertError(ServerResponse response, String expectedError) {
        assertEquals(400, response.getResponseCode());
        assertEquals(expectedError, gson.fromJson(response.getResponseData(), ErrorDtoResponse.class).getError());
    }
}
