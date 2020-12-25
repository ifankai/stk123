package com.stk123.repository;

import com.stk123.entity.StkKlineHkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface StkKlineHkRepository extends JpaRepository<StkKlineHkEntity, StkKlineHkEntity.CompositeKey> {

    StkKlineHkEntity findTop1ByCodeOrderByKlineDateDesc(String code);

    @Transactional
    default StkKlineHkEntity saveIfNotExisting(StkKlineHkEntity stkKlineUsEntity){
        Optional<StkKlineHkEntity> entity = this.findById(new StkKlineHkEntity.CompositeKey(stkKlineUsEntity.getCode(),stkKlineUsEntity.getKlineDate()));
        if(!entity.isPresent()){
            return save(stkKlineUsEntity);
        }
        return entity.get();
    }
}