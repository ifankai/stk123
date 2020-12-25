package com.stk123.repository;

import com.stk123.entity.StkKlineUsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Repository
public interface StkKlineUsRepository extends JpaRepository<StkKlineUsEntity, StkKlineUsEntity.CompositeKey> {

    StkKlineUsEntity findTop1ByCodeOrderByKlineDateDesc(String code);

    @Transactional
    default StkKlineUsEntity saveIfNotExisting(StkKlineUsEntity stkKlineUsEntity){
        Optional<StkKlineUsEntity> entity = this.findById(new StkKlineUsEntity.CompositeKey(stkKlineUsEntity.getCode(),stkKlineUsEntity.getKlineDate()));
        if(!entity.isPresent()){
            return save(stkKlineUsEntity);
        }
        return entity.get();
    }


}
