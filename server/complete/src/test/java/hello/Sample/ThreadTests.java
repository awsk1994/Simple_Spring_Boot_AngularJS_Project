package hello.Sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class ThreadTests {
    @Test
    public void BasicTest() {
        System.out.println("Basic Test | Start.");
        new MainController().start();
        startHeartbeat();
        System.out.println("Basic Test | End.");
    }

    void startHeartbeat() {
        int counter = 0;
        while (true) {
            counter++;
            try {
                System.out.println("\t---Heartbeat " + counter);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            if (counter == 10) {
                break;
            }
        }
    }

    class MainController extends Thread {
        public void run() {
            System.out.println("Main Controller | Started.");
            System.out.println("Main Controller | Starting Work Thread.");
            new WorkThread(this).start();
            System.out.println("Main Controller | Finished.");
        }

        void print() {
            System.out.println("Main Controller | Printing...");
        }
    }

    class WorkThread extends Thread {
        private MainController controller;

        public WorkThread(MainController controller) {
            this.controller = controller;
        }

        public void run() {
            try {
                System.out.println("Work Thread | Started. Will pause for 3 seconds, then call controller's print method.");
                Thread.sleep(3000);
                controller.print();
                System.out.println("Work Thread | Finished.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

/*    @Test
    public void TestInterrupt() {
        System.out.println("TestInterrupt | Start.");
        ForeverThread foreverThread = new ForeverThread();
        foreverThread.start();

        int counter = 0;
        while (true) {
            counter++;
            try {
                System.out.println("\t---Heartbeat " + counter);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            if (counter == 5) {
                try {
                    foreverThread.interrupt();
                } catch (Exception e){
                    e.printStackTrace();
                }
                // break;
            }
        }
        System.out.println("TestInterrupt | End.");
    }

    class ForeverThread extends Thread {
        public void run() {
            System.out.println("ForeverThread | Started. ");

            try {
                while(true) {
                    Thread.sleep(1000);
                    System.out.println("ForeverThread | Ping. ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/
}