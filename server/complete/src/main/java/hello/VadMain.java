package hello;

public class VadMain {

    private final long id;
    private String content;

    public VadMain(long id, String content) {
        this.id = id;
        this.content = content;
    };

    public void run(){
        try{
            this.content = this.content + "_" + this.content;
            Thread.sleep(5000);
            return;
        } catch(Exception e){
            return;
        }
    };

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }   // this is the string that gets returned to UI.
}
