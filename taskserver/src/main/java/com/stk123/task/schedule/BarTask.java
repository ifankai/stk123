package com.stk123.task.schedule;

import com.stk123.task.schedule.core.Task;

public class BarTask extends Task {

    @Override
    public void execute(String... args) throws Exception {
        while(true) {
            Thread.sleep(2000);
            System.out.println("running............"+Thread.currentThread().getId());
        }
    }


}
