package com.stk123.service;

import com.stk123.entity.StkDataIndustryPeEntity;
import com.stk123.entity.StkIndustryTypeEntity;
import com.stk123.repository.BaseRepository;
import com.stk123.repository.StkDataIndustryPeRepository;
import com.stk123.repository.StkIndustryTypRepository;
import com.stk123.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Service
@Deprecated
public class IndustryServiceDeprecated {

    public IndustryServiceDeprecated(){}

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
    public StkDataIndustryPeEntity update(Integer industryId, String peDate, Double pe, Double peTtm, Double pb, Double adr) {
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
        if(adr != null) entity.setAdr(adr);
        return stkDataIndustryPeRepository.save(entity);
    }

    public StkDataIndustryPeEntity updatePe(Integer industryId, String peDate, Double pe) {
        return update(industryId, peDate, pe, null, null, null);
    }
    public StkDataIndustryPeEntity updatePettm(Integer industryId, String peDate, Double pettm) {
        return update(industryId, peDate, null, pettm, null, null);
    }
    public StkDataIndustryPeEntity updatePb(Integer industryId, String peDate, Double pb) {
        return update(industryId, peDate, null, null, pb, null);
    }
    public StkDataIndustryPeEntity updateAdr(Integer industryId, String peDate, Double adr) {
        return update(industryId, peDate, null, null, null, adr);
    }

}
