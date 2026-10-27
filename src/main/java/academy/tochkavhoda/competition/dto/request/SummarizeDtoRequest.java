package academy.tochkavhoda.competition.dto.request;

public class SummarizeDtoRequest {
    private long grantFund;
    private double threshold;

    public SummarizeDtoRequest() {
    }

    public SummarizeDtoRequest(long grantFund, double threshold) {
        this.grantFund = grantFund;
        this.threshold = threshold;
    }

    public long getGrantFund() {
        return grantFund;
    }

    public double getThreshold() {
        return threshold;
    }
}
