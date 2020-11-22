package com.stk123.app.service;

import com.stk123.model.app.XqPost;
import com.stk123.app.repository.XqPostRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@CommonsLog
public class XqService {

    @Autowired
    private XqPostRepository xqPostRepository;

    @Async
    public void updateToRead(List<XqPost> list) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("异步调用：updateToRead");
        list.forEach(item -> {
            item.setIsRead(true);
            item.setReadDate(new Date());
        });
        xqPostRepository.saveAll(list);
    }

}
