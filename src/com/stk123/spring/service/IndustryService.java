package com.stk123.spring.service;

import com.stk123.spring.SpringUtils;
import com.stk123.spring.jpa.entity.StkDataIndustryPeEntity;
import com.stk123.spring.jpa.repository.StkDataIndustryPeRepository;
import org.springframework.beans.annotation.AnnotationBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class IndustryService extends BaseService {

    @Autowired
    private static StkDataIndustryPeRepository stkDataIndustryPeRepository;

    public static StkDataIndustryPeEntity insertOrUpdateIndustryData(Long industryId, String peDate, Double pe, Double peTtm, Double pb, Double adr){
        if(industryId == null || peDate == null){
            throw new IllegalArgumentException("IndustryId and PeDate cannot be null");
        }
        StkDataIndustryPeEntity entity = stkDataIndustryPeRepository.findOne(new StkDataIndustryPeEntity.CompositeKey(industryId, peDate));
        if(entity == null){
            entity = new StkDataIndustryPeEntity();
            entity.setIndustryId(industryId);
            entity.setPeDate(peDate);
            entity.setPe(pe);
            entity.setPeTtm(peTtm);
            entity.setPb(pb);
            entity.setAdr(adr);
            entity.setInsertTime(new java.sql.Time(Calendar.getInstance().getTime().getTime()));
        }else {
            if(pe != null) entity.setPe(pe);
            if(peTtm != null) entity.setPeTtm(peTtm);
            if(pb != null) entity.setPb(pb);
            if(adr != null) entity.setAdr(adr);
        }
        return stkDataIndustryPeRepository.save(entity);
    }

    public static StkDataIndustryPeRepository getStkDataIndustryPeRepository() {
        return stkDataIndustryPeRepository;
    }

}
