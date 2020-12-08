package com.stk123.task.schedule;

import org.springframework.stereotype.Service;

@Service
public class BarTask implements Task {

    @Override
    public void run(String... args) {
        System.out.println("run............start"+args);
        try {
            while(true) {
                Thread.sleep(2000);
                System.out.println("running............");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("run............end");
    }

}
