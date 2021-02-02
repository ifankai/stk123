package com.stk123.service.core;

import com.stk123.entity.StkTaskLogEntity;
import com.stk123.repository.StkTaskLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private StkTaskLogRepository stkTaskLogRepository;

    public void insertIfNotExisting(StkTaskLogEntity stkTaskLogEntity){
        if(stkTaskLogEntity.getCode() != null && stkTaskLogEntity.getCode().length() > 2000){
            stkTaskLogEntity.setCode(stkTaskLogEntity.getCode().substring(0, 2000));
        }
        List<StkTaskLogEntity> list = stkTaskLogRepository.findAllByTaskCodeAndTaskDateAndStrategyCodeAndCodeOrderByIdDesc(
                stkTaskLogEntity.getTaskCode(), stkTaskLogEntity.getTaskDate(),stkTaskLogEntity.getStrategyCode(), stkTaskLogEntity.getCode());
        if(list.isEmpty()){
            stkTaskLogRepository.save(stkTaskLogEntity);
        }
    }
}
