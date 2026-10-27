package academy.tochkavhoda.competition.dto.response;

public class ParticipantDtoResponse {
    private String firstName;
    private String lastName;
    private String companyName;

    public ParticipantDtoResponse() {
    }

    public ParticipantDtoResponse(String firstName, String lastName, String companyName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompanyName() {
        return companyName;
    }
}
