package org.gobiiproject.gobiidomain.async;

import org.springframework.scheduling.annotation.Async;

public class SearchExtract {


    @Async
    public void asyncMethod() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("Asynchronous Processing");
    }
}
