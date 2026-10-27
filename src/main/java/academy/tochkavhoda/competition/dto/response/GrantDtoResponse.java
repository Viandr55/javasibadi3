package academy.tochkavhoda.competition.dto.response;

public class GrantDtoResponse {
    private int applicationId;
    private String title;
    private String companyName;
    private long amount;
    private double averageRating;

    public GrantDtoResponse() {
    }

    public GrantDtoResponse(int applicationId, String title, String companyName, long amount, double averageRating) {
        this.applicationId = applicationId;
        this.title = title;
        this.companyName = companyName;
        this.amount = amount;
        this.averageRating = averageRating;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public String getTitle() {
        return title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public long getAmount() {
        return amount;
    }

    public double getAverageRating() {
        return averageRating;
    }
}
