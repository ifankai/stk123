package com.stk123.repository;

import com.stk123.entity.StkOwnershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public interface StkOwnershipRepository extends JpaRepository<StkOwnershipEntity, StkOwnershipEntity.CompositeKey> {

    @Query(value = "select * from (select code ,fn_date, stk_num, 100 * (stk_num \\/ sum(stk_num) over (order by code,fn_date desc rows between 1 following and 1 following) - 1) ten_owner_change from " +
            "(select code,fn_date,sum(stk_num) stk_num from stk_ownership group by code,fn_date having code=:code)) where fn_date=:fnDate", nativeQuery = true)
    List<Object> findTenOnwerChangeByCodeAndFnDate(@Param("code")String code, @Param("fnDate")String fnDate);

    //@Query(value = "select o.code,o.fn_date,o.org_id,o.stk_num,o.rate,o.num_change,o.num_change_rate,name as org_name from stk_ownership o, stk_organization g,(select code,max(fn_date) fn_date from stk_ownership group by code) a where o.org_id=g.id and o.code=a.code and o.fn_date=a.fn_date and o.code in (:codes)", nativeQuery = true)
    //List<StkOwnershipEntity> findAllByCodeAndFnDateIsMax(@Param("codes") List<String> codes);

    String sql_findAll = "select o.code,o.fn_date,o.org_id,o.stk_num,o.rate,o.num_change,o.num_change_rate,name as org_name from stk_ownership o, stk_organization g,(select code,max(fn_date) fn_date from stk_ownership group by code) a where o.org_id=g.id and o.code=a.code and o.fn_date=a.fn_date and o.code in (:1)";
    @Transactional
    default List<StkOwnershipEntity> findAllByCodeAndFnDateIsMax(List<String> codes) {
        return BaseRepository.getInstance().list(sql_findAll, StkOwnershipEntity.class, codes);
    }

    @Transactional
    default Map<String, List<StkOwnershipEntity>> getMapByCodeAndFnDateIsMax(List<String> codes){
        List<StkOwnershipEntity> owners = findAllByCodeAndFnDateIsMax(codes);
        Map<String, List<StkOwnershipEntity>> result = new LinkedHashMap<>(codes.size());
        for(StkOwnershipEntity n : owners){
            List<StkOwnershipEntity> list = result.computeIfAbsent(n.getCode(), k -> new ArrayList<>());
            list.add(n);
        }
        return result;
    }

    List<StkOwnershipEntity> findAllByFnDateAndOrgIdIn(String fnDate, List<Long> orgIds);
}
