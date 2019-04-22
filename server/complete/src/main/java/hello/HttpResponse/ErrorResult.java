package hello.HttpResponse;

public class ErrorResult extends HttpResult {
    private final String error;

    public ErrorResult(String error){
        super("ERROR");
        this.error = error;
    }

    public String getStatus(){
        return super.getStatus();
    }

    public String getError() {
        return error;
    }
}
