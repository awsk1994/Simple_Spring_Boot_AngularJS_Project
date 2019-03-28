package hello.Http;

/*
    When used as return object in Controller, it will return:
    {
        'status': <STATUS>,
        'details': <DETAILS>
    };
 */
public class HttpResult {
    private final String status;
    private final String details;

    public HttpResult(String status, String details) {
        this.status = status;
        this.details = details;
    };

    /* Response is based on how many get<Key> methods you have. */

    public String getDetails() {
        return details;
    };

    public String getStatus() {
        return status;
    };

    public String blahMethod(){
        return status;
    }
}
