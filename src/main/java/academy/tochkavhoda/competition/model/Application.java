package academy.tochkavhoda.competition.model;

import java.util.Set;

public class Application {
    private int id;
    private String title;
    private String description;
    private Set<String> directions;
    private long requestedAmount;
    private Participant participant;

    public Application(String title, String description, Set<String> directions,
                       long requestedAmount, Participant participant) {
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.requestedAmount = requestedAmount;
        this.participant = participant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getDirections() {
        return directions;
    }

    public void setDirections(Set<String> directions) {
        this.directions = directions;
    }

    public long getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(long requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}
