package com.stk123.service;

import com.stk123.entity.StkDataIndustryPeEntity;
import com.stk123.entity.StkIndustryTypeEntity;
import com.stk123.repository.StkDataIndustryPeRepository;
import com.stk123.repository.StkIndustryTypRepository;
import com.stk123.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Service
public class IndustryService extends BaseService {

    public IndustryService(){}

    @Autowired
    private StkDataIndustryPeRepository stkDataIndustryPeRepository;

    @Autowired
    private StkIndustryTypRepository stkIndustryTypRepository;

    public StkDataIndustryPeEntity insertOrUpdateIndustryData(Integer industryId, String peDate, Double pe, Double peTtm, Double pb, Double adr){
        if(industryId == null || peDate == null){
            throw new IllegalArgumentException("IndustryId and PeDate cannot be null");
        }
        StkDataIndustryPeEntity entity = stkDataIndustryPeRepository.getOne(new StkDataIndustryPeEntity.CompositeKey(industryId, peDate));
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

    public StkDataIndustryPeEntity findStkDataIndustryPe(Integer industryId, String peDate){
        //StkDataIndustryPeRepository stkDataIndustryPeRepository = BaseService.getRepository(StkDataIndustryPeEntity.class);
        return stkDataIndustryPeRepository.findByIndustryIdAndPeDate(industryId, peDate);
    }

    public StkIndustryTypeEntity findStkIndustryType(Integer id) {
        return stkIndustryTypRepository.getOne(id);
    }

    @Transactional
    public StkDataIndustryPeEntity updatePe(Integer industryId, String peDate, Double pe, Double peTtm, Double pb) {
        StkDataIndustryPeEntity entity = findStkDataIndustryPe(industryId, peDate);
        if(entity == null){
            entity = new StkDataIndustryPeEntity();
            entity.setIndustryId(industryId);
            entity.setPeDate(peDate);
            entity.setInsertTime(ServiceUtils.getTime());
        }
        if(pe != null) entity.setPe(pe);
        if(peTtm != null) entity.setPeTtm(peTtm);
        if(pb != null) entity.setPb(pb);
        return save(entity);
    }

}
