package hello;

public class HttpResult {

    private final String status;

    public HttpResult(String status) {
        this.status = status;
    };

    public String getStatus() {
        return status;
    }   // this is the string that gets returned to UI.
}
