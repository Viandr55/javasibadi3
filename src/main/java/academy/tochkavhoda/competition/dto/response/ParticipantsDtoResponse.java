package academy.tochkavhoda.competition.dto.response;

import java.util.List;

public class ParticipantsDtoResponse {
    private List<ParticipantDtoResponse> participants;

    public ParticipantsDtoResponse() {
    }

    public ParticipantsDtoResponse(List<ParticipantDtoResponse> participants) {
        this.participants = participants;
    }

    public List<ParticipantDtoResponse> getParticipants() {
        return participants;
    }
}
