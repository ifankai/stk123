package com.stk123.repository;

import com.stk123.common.util.BeanUtils;
import com.stk123.entity.StkKlineHkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface StkKlineHkRepository extends JpaRepository<StkKlineHkEntity, StkKlineHkEntity.CompositeKey> {

    StkKlineHkEntity findTop1ByCodeOrderByKlineDateDesc(String code);

    @Query(value = "select t from StkKlineHkEntity t where t.code=:code and t.klineDate=:klineDate")
    StkKlineHkEntity findById(@Param("code")String code, @Param("klineDate")String klineDate);

    @Transactional
    default StkKlineHkEntity saveIfNotExisting(StkKlineHkEntity stkKlineUsEntity){
        Optional<StkKlineHkEntity> entity = this.findById(new StkKlineHkEntity.CompositeKey(stkKlineUsEntity.getCode(),stkKlineUsEntity.getKlineDate()));
        if(!entity.isPresent()){
            return save(stkKlineUsEntity);
        }
        return entity.get();
    }

    @Transactional
    default StkKlineHkEntity saveOrUpdate(StkKlineHkEntity stkKlineEntity){
        Optional<StkKlineHkEntity> entity = this.findById(new StkKlineHkEntity.CompositeKey(stkKlineEntity.getCode(),stkKlineEntity.getKlineDate()));
        if(entity.isPresent()){
            BeanUtils.mapIgnoreNull(stkKlineEntity, entity.get());
            return save(entity.get());
        }else {
            return save(stkKlineEntity);
        }
    }
}