package academy.tochkavhoda.competition.dto.request;

public class ApplicationIdDtoRequest {
    private int applicationId;

    public ApplicationIdDtoRequest() {
    }

    public ApplicationIdDtoRequest(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getApplicationId() {
        return applicationId;
    }
}
