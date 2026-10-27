package academy.tochkavhoda.competition.dto.response;

import java.util.List;

public class SummaryDtoResponse {
    private List<GrantDtoResponse> grants;
    private long remainingFund;

    public SummaryDtoResponse() {
    }

    public SummaryDtoResponse(List<GrantDtoResponse> grants, long remainingFund) {
        this.grants = grants;
        this.remainingFund = remainingFund;
    }

    public List<GrantDtoResponse> getGrants() {
        return grants;
    }

    public long getRemainingFund() {
        return remainingFund;
    }
}
