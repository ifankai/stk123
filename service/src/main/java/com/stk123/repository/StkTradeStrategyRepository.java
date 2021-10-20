package com.stk123.repository;

import com.stk123.entity.StkTradeStrategyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkTradeStrategyRepository extends JpaRepository<StkTradeStrategyEntity, Integer> {

    StkTradeStrategyEntity findByCodeAndTradeDateAndStrategyCode(String code, String tradeDate, String strategyCode);

}
