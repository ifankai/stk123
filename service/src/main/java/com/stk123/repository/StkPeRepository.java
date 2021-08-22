package com.stk123.repository;

import com.stk123.entity.StkPeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkPeRepository extends JpaRepository<StkPeEntity, String> {

    default StkPeEntity findOrCreateById(String date) {
        StkPeEntity stkPeEntity = this.findById(date).orElse(null);
        if(stkPeEntity == null){
            stkPeEntity = new StkPeEntity();
        }
        return stkPeEntity;
    }

    StkPeEntity findFirstByReportDate(String reportDate);

    StkPeEntity findTopByOrderByReportDateDesc();
}
