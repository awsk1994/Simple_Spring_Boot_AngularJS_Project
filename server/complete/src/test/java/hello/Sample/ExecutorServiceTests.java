/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hello.Sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class ExecutorServiceTests {
    @Test
    public void ExecutorServiceTests() {
        Thread unique = new Thread(new WaitThread(), "unique");
        unique.start();
        ExecutorService Executor = Executors.newFixedThreadPool(2);             // TODO: what happens if pool size reached? Should set a super high limit?
        Executor.execute(new WaitThread());
        Executor.execute(new WaitThread());

        int counter = 0;
        while(counter < 10){
            try {
                System.out.println("\t--heartbeat: " + counter);
                Thread.sleep(1000);
                counter++;
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    class WaitThread implements Runnable
    {
        @Override
        public void run(){
            try{
                System.out.println(Thread.currentThread().getName() + " | Running");
                Thread.currentThread().sleep(3 * 1000);
                throw new InterruptedException("test error");
                //System.out.println(Thread.currentThread().getName() + " | Done");
            } catch (InterruptedException e){
                System.out.println("Error at " + Thread.currentThread().getName());
                e.printStackTrace();
            }
        }
    }
}
