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

    public HttpResult(String status) {
        this.status = status;
    }

    /* Response is based on how many get<Key> methods you have. */
    public String getStatus() {
        return status;
    };
}
