package hello;

public class Wave {
    private final long id;
    private String content;

    public Wave(long id, String content) {
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
}
