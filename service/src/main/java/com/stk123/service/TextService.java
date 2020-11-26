package com.stk123.service;

import com.stk123.entity.StkTextEntity;
import com.stk123.repository.BaseRepository;
import com.stk123.repository.StkTextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TextService extends BaseRepository {
    @Autowired
    private StkTextRepository stkTextRepository;

    @Async
    public void updateToRead(List<StkTextEntity> list) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("异步调用：updateToRead");
        list.forEach(item -> {
            item.setReadDate(new Date());
        });
        stkTextRepository.saveAll(list);
    }
}
