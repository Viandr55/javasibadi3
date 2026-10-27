package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dto.response.ServerResponse;
import academy.tochkavhoda.competition.service.ApplicationService;
import academy.tochkavhoda.competition.service.CompetitionService;
import academy.tochkavhoda.competition.service.RatingService;
import academy.tochkavhoda.competition.service.UserService;

public class Server {
    private final UserService userService = new UserService();
    private final ApplicationService applicationService = new ApplicationService();
    private final RatingService ratingService = new RatingService();
    private final CompetitionService competitionService = new CompetitionService();

    // запуск сервера с пустой базой данных
    public void startServer() {
        competitionService.startServer();
    }

    // ---------- регистрация и вход ----------

    public ServerResponse registerParticipant(String requestJsonString) {
        return userService.registerParticipant(requestJsonString);
    }

    public ServerResponse registerExpert(String requestJsonString) {
        return userService.registerExpert(requestJsonString);
    }

    public ServerResponse login(String requestJsonString) {
        return userService.login(requestJsonString);
    }

    public ServerResponse logout(String token) {
        return userService.logout(token);
    }

    public ServerResponse leaveServer(String token) {
        return userService.leaveServer(token);
    }

    public ServerResponse getParticipants(String token, String requestJsonString) {
        return userService.getParticipants(token, requestJsonString);
    }

    // ---------- заявки (участник) ----------

    public ServerResponse addApplication(String token, String requestJsonString) {
        return applicationService.addApplication(token, requestJsonString);
    }

    public ServerResponse cancelApplication(String token, String requestJsonString) {
        return applicationService.cancelApplication(token, requestJsonString);
    }

    // ---------- оценки (эксперт) ----------

    public ServerResponse getApplicationsByDirections(String token, String requestJsonString) {
        return applicationService.getApplicationsByDirections(token, requestJsonString);
    }

    public ServerResponse rateApplication(String token, String requestJsonString) {
        return ratingService.rateApplication(token, requestJsonString);
    }

    public ServerResponse changeRating(String token, String requestJsonString) {
        return ratingService.changeRating(token, requestJsonString);
    }

    public ServerResponse deleteRating(String token, String requestJsonString) {
        return ratingService.deleteRating(token, requestJsonString);
    }

    public ServerResponse getRatedApplications(String token) {
        return ratingService.getRatedApplications(token);
    }

    // ---------- подведение итогов ----------

    public ServerResponse summarizeResults(String requestJsonString) {
        return competitionService.summarizeResults(requestJsonString);
    }
}
