package com.stk123.repository;

import com.stk123.entity.StkTradeStrategyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkTradeStrategyRepository extends JpaRepository<StkTradeStrategyEntity, Integer> {

    StkTradeStrategyEntity findByCodeAndTradeDateAndStrategyCode(String code, String tradeDate, String strategyCode);

    List<StkTradeStrategyEntity> findAllByTradeDateOrderByInsertTime(String date);

}
