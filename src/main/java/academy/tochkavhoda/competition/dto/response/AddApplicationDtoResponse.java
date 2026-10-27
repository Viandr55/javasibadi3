package academy.tochkavhoda.competition.dto.response;

public class AddApplicationDtoResponse {
    private int applicationId;

    public AddApplicationDtoResponse() {
    }

    public AddApplicationDtoResponse(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getApplicationId() {
        return applicationId;
    }
}
