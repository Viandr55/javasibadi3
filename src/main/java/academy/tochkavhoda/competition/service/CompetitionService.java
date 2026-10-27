package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.CompetitionDao;
import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.CompetitionDaoImpl;
import academy.tochkavhoda.competition.daoimpl.RatingDaoImpl;
import academy.tochkavhoda.competition.dto.request.SummarizeDtoRequest;
import academy.tochkavhoda.competition.dto.response.GrantDtoResponse;
import academy.tochkavhoda.competition.dto.response.ServerResponse;
import academy.tochkavhoda.competition.dto.response.SummaryDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompetitionService extends ServiceBase {
    private static final double MAX_THRESHOLD = 5;

    private final ApplicationDao applicationDao = new ApplicationDaoImpl();
    private final RatingDao ratingDao = new RatingDaoImpl();
    private final CompetitionDao competitionDao = new CompetitionDaoImpl();

    public void startServer() {
        competitionDao.clear();
    }

    // момент X: подача заявок и оценок прекращается, распределяется фонд
    public ServerResponse summarizeResults(String requestJsonString) {
        try {
            SummarizeDtoRequest request = fromJson(requestJsonString, SummarizeDtoRequest.class);
            validate(request);
            competitionDao.finish();

            List<Application> applications = applicationDao.getAll();
            Map<Application, Double> averages = new HashMap<>();
            for (Application application : applications) {
                averages.put(application, average(ratingDao.getRatings(application)));
            }
            // перемешивание + устойчивая сортировка = случайный порядок при равных оценке и сумме
            Collections.shuffle(applications);
            applications.sort(Comparator.<Application>comparingDouble(averages::get).reversed()
                    .thenComparingLong(Application::getRequestedAmount));

            long fund = request.getGrantFund();
            List<GrantDtoResponse> grants = new ArrayList<>();
            for (Application application : applications) {
                double average = averages.get(application);
                if (average <= request.getThreshold()) {
                    break;
                }
                if (application.getRequestedAmount() <= fund) {
                    fund -= application.getRequestedAmount();
                    grants.add(new GrantDtoResponse(application.getId(), application.getTitle(),
                            application.getParticipant().getCompanyName(), application.getRequestedAmount(), average));
                }
            }
            return success(new SummaryDtoResponse(grants, fund));
        } catch (ServerException e) {
            return error(e);
        }
    }

    private static double average(List<Integer> ratings) {
        return ratings.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    private static void validate(SummarizeDtoRequest request) throws ServerException {
        if (request.getGrantFund() <= 0) {
            throw new ServerException(ErrorCode.WRONG_AMOUNT);
        }
        if (request.getThreshold() < 0 || request.getThreshold() > MAX_THRESHOLD) {
            throw new ServerException(ErrorCode.WRONG_THRESHOLD);
        }
    }
}
