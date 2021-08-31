package com.stk123.service.core;

import com.stk123.entity.StkTextEntity;
import com.stk123.repository.BaseRepository;
import com.stk123.repository.StkTextRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TextService extends BaseRepository {
    @Autowired
    private StkTextRepository stkTextRepository;

    @Async
    @Transactional
    public void updateToRead(List<StkTextEntity> list) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("异步调用：updateToRead");
//        list.forEach(item -> {
//            item.setReadDate(new Date());
//        });
//        stkTextRepository.saveAll(list);
        stkTextRepository.updateAll2Readed(list.stream().map((item) -> item.getId()).collect(Collectors.toList()));
    }
}
