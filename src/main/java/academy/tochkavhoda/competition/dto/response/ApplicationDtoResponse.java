package academy.tochkavhoda.competition.dto.response;

import java.util.List;

public class ApplicationDtoResponse {
    private int applicationId;
    private String title;
    private String description;
    private List<String> directions;
    private long requestedAmount;
    private String companyName;
    // оценка эксперта, запросившего список; null, если оценки нет
    private Integer rating;

    public ApplicationDtoResponse() {
    }

    public ApplicationDtoResponse(int applicationId, String title, String description, List<String> directions,
                                  long requestedAmount, String companyName, Integer rating) {
        this.applicationId = applicationId;
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.requestedAmount = requestedAmount;
        this.companyName = companyName;
        this.rating = rating;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDirections() {
        return directions;
    }

    public long getRequestedAmount() {
        return requestedAmount;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Integer getRating() {
        return rating;
    }
}
