package academy.tochkavhoda.competition.dto.request;

public class RatingDtoRequest {
    private int applicationId;
    private int rating;

    public RatingDtoRequest() {
    }

    public RatingDtoRequest(int applicationId, int rating) {
        this.applicationId = applicationId;
        this.rating = rating;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public int getRating() {
        return rating;
    }
}
