package com.stk123.repository;

import com.stk123.entity.StkHolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public interface StkHolderRepository extends JpaRepository<StkHolderEntity, StkHolderEntity.CompositeKey> {

    String sql_findAll = "select s.code, t.fn_date, t.holder, t.holding_amount, t.holder_change, t.ten_owner_change " +
        "from (select code, fn_date, holder, holding_amount,holder_change,ten_owner_change, ROW_NUMBER() over(PARTITION by code order by fn_date desc) as num from stk_holder) t, stk s "+
        "where t.code=s.code and t.num = 1";
    default List<StkHolderEntity> findAll() {
        return BaseRepository.getInstance().list(sql_findAll, StkHolderEntity.class);
    }

    default Map<String, StkHolderEntity> findAllToMap() {
        return findAll().stream().collect(Collectors.toMap(StkHolderEntity::getCode, Function.identity()));
    }

    StkHolderEntity findByCodeAndFnDate(String code, String fnDate);

    @Query(value = "select * from stk_holder a, (select code,max(fn_date) fn_date from stk_holder group by code) b where a.code=b.code and a.fn_date=b.fn_date and a.code=:code", nativeQuery = true)
    StkHolderEntity findByCodeAndFnDateIsMax(@Param("code")String code);

}
