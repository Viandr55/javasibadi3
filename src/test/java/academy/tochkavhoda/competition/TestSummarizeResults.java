package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.AddApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.RatingDtoRequest;
import academy.tochkavhoda.competition.dto.request.SummarizeDtoRequest;
import academy.tochkavhoda.competition.dto.response.GrantDtoResponse;
import academy.tochkavhoda.competition.dto.response.ServerResponse;
import academy.tochkavhoda.competition.dto.response.SummaryDtoResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSummarizeResults extends TestBase {

    @Test
    public void testSummarize() {
        String participant = registerAndLoginParticipant("ivanov", "Фирма");
        String first = registerAndLoginExpert("petrov", List.of("математика"));
        String second = registerAndLoginExpert("sidorov", List.of("математика"));
        List<String> math = List.of("математика");

        int a = addApplication(participant, "A", math, 500);
        int b = addApplication(participant, "B", math, 300);
        int c = addApplication(participant, "C", math, 400);
        int d = addApplication(participant, "D", math, 100);
        int e = addApplication(participant, "E", math, 100);
        rate(first, a, 5);
        rate(second, a, 5);
        rate(first, b, 5);
        rate(second, b, 5);
        rate(first, c, 4);
        rate(second, c, 4);
        rate(first, d, 3);
        rate(first, e, 2);

        // B (5, дешевле A) -> A (5) -> C пропускается (не хватает) -> D (3) -> E: оценка <= порога, стоп
        ServerResponse response = server.summarizeResults(gson.toJson(new SummarizeDtoRequest(1000, 2)));
        assertOk(response);
        SummaryDtoResponse summary = gson.fromJson(response.getResponseData(), SummaryDtoResponse.class);
        assertEquals(List.of(b, a, d), summary.getGrants().stream().map(GrantDtoResponse::getApplicationId).toList());
        assertEquals(100, summary.getRemainingFund());
    }

    @Test
    public void testNoRatingsNoGrants() {
        String participant = registerAndLoginParticipant("ivanov", "Фирма");
        addApplication(participant, "A", List.of("математика"), 100);

        ServerResponse response = server.summarizeResults(gson.toJson(new SummarizeDtoRequest(1000, 0)));
        assertOk(response);
        SummaryDtoResponse summary = gson.fromJson(response.getResponseData(), SummaryDtoResponse.class);
        assertTrue(summary.getGrants().isEmpty());
        assertEquals(1000, summary.getRemainingFund());
    }

    @Test
    public void testSummarizeWrongData() {
        assertError(server.summarizeResults(gson.toJson(new SummarizeDtoRequest(0, 2))), "Сумма должна быть положительной");
        assertError(server.summarizeResults(gson.toJson(new SummarizeDtoRequest(1000, 6))),
                "Пороговое значение должно быть в диапазоне 0..5");
    }

    @Test
    public void testCompetitionFinished() {
        String participant = registerAndLoginParticipant("ivanov", "Фирма");
        String expert = registerAndLoginExpert("petrov", List.of("математика"));
        int id = addApplication(participant, "A", List.of("математика"), 100);
        assertOk(server.summarizeResults(gson.toJson(new SummarizeDtoRequest(1000, 2))));

        String finished = "Конкурс завершён, операция невозможна";
        assertError(server.addApplication(participant, gson.toJson(
                new AddApplicationDtoRequest("B", "Описание", List.of("математика"), 100))), finished);
        assertError(server.rateApplication(expert, gson.toJson(new RatingDtoRequest(id, 5))), finished);
        assertError(server.summarizeResults(gson.toJson(new SummarizeDtoRequest(1000, 2))), finished);
    }
}
