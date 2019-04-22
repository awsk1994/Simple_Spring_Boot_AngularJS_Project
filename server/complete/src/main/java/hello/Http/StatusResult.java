package hello.Http;

public class StatusResult extends HttpResult {
    private final String details;

    public StatusResult(String status, String details){
        super(status);
        this.details = details;
    }

    public String getStatus(){
        return super.getStatus();
    }

    public String getDetails() {
        return details;
    }
}
