package com.stk123.repository;

import com.stk123.entity.StkOwnershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkOwnershipRepository extends JpaRepository<StkOwnershipEntity, StkOwnershipEntity.CompositeKey> {

    @Query(value = "select * from (select code ,fn_date, stk_num, 100 * (stk_num \\/ sum(stk_num) over (order by code,fn_date desc rows between 1 following and 1 following) - 1) ten_owner_change from " +
            "(select code,fn_date,sum(stk_num) stk_num from stk_ownership group by code,fn_date having code=:code)) where fn_date=:fnDate", nativeQuery = true)
    List<Object> findTenOnwerChangeByCodeAndFnDate(@Param("code")String code, @Param("fnDate")String fnDate);

}
