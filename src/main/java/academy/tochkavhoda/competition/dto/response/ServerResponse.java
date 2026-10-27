package academy.tochkavhoda.competition.dto.response;

public class ServerResponse {
    public static final int OK = 200;
    public static final int ERROR = 400;

    private int responseCode;
    private String responseData;

    public ServerResponse() {
    }

    public ServerResponse(int responseCode, String responseData) {
        this.responseCode = responseCode;
        this.responseData = responseData;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
}
