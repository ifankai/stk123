package com.stk123.controller;

import com.stk123.entity.StkOrganizationEntity;
import com.stk123.entity.StkOwnershipEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Stock;
import com.stk123.repository.StkOrganizationRepository;
import com.stk123.repository.StkOwnershipRepository;
import com.stk123.service.core.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/holder")
@Slf4j
public class HolderController {

    @Autowired
    private StkOrganizationRepository stkOrganizationRepository;
    @Autowired
    private StkOwnershipRepository stkOwnershipRepository;
    @Autowired
    private StockService stockService;


    @GetMapping("/query/{name}")
    public RequestResult query(@PathVariable(value = "name")String name) {
        List<StkOrganizationEntity> orgs = stkOrganizationRepository.findAllByNameLike('%'+name+'%');
        return RequestResult.success(orgs);
    }

    @GetMapping("/diff/{quarter1}/{quarter2}/{orgIds}")
    public RequestResult diff(@PathVariable(value = "quarter1")String quarter1,
                              @PathVariable(value = "quarter2")String quarter2,
                              @PathVariable(value = "orgIds")String orgIds) {
        List<Long> ids = Stream.of(StringUtils.split(orgIds, ",")).map(Long::parseLong).collect(Collectors.toList());
        List<StkOwnershipEntity> holder1 = stkOwnershipRepository.findAllByFnDateAndOrgIdIn(StringUtils.replace(quarter1,"-", ""), ids);
        List<StkOwnershipEntity> holder2 = stkOwnershipRepository.findAllByFnDateAndOrgIdIn(StringUtils.replace(quarter2,"-", ""), ids);
        List<String> codes1 = holder1.stream().map(StkOwnershipEntity::getCode).collect(Collectors.toList());
        List<String> codes2 = holder2.stream().map(StkOwnershipEntity::getCode).collect(Collectors.toList());
        codes1.addAll(codes2);
        List<Stock> stocks = stockService.getStocks(codes1.stream().distinct().collect(Collectors.toList()));
        Map<String, Stock> stockMap = stocks.stream().collect(Collectors.toMap(Stock::getCode, Function.identity()));

        holder1.forEach(stkOwnershipEntity -> {
            stkOwnershipEntity.setNameAndCode(stockMap.get(stkOwnershipEntity.getCode()).getNameAndCodeWithLink());
        });
        holder2.forEach(stkOwnershipEntity -> {
            stkOwnershipEntity.setNameAndCode(stockMap.get(stkOwnershipEntity.getCode()).getNameAndCodeWithLink());
        });

        Map<String, StkOwnershipEntity> map1 = holder1.stream().collect(Collectors.toMap(StkOwnershipEntity::getCode, Function.identity(), (oldValue, newValue) -> {
            oldValue.setRate(oldValue.getRate()+ newValue.getRate());
            return oldValue;
        }));
        Map<String, StkOwnershipEntity> map2 = holder2.stream().collect(Collectors.toMap(StkOwnershipEntity::getCode, Function.identity(), (oldValue, newValue) -> {
            oldValue.setRate(oldValue.getRate()+ newValue.getRate());
            return oldValue;
        }));

        Map result = new LinkedHashMap();
        //退出个股
        List<StkOwnershipEntity> quits = map1.values().stream().filter(holder -> !map2.containsKey(holder.getCode())).collect(Collectors.toList());
        result.put("quits", quits);
        //新进入个股
        List<StkOwnershipEntity> newEntries = map2.values().stream().filter(holder -> !map1.containsKey(holder.getCode())).collect(Collectors.toList());
        result.put("newEntries", newEntries.stream().sorted(Comparator.comparing(StkOwnershipEntity::getRate, Comparator.reverseOrder())).collect(Collectors.toList()));
        //继续持有个股
        List<StkOwnershipEntity> keeps = map2.values().stream().filter(holder -> map1.containsKey(holder.getCode())).collect(Collectors.toList());
        for(StkOwnershipEntity entity : keeps){
            StkOwnershipEntity prev = map1.get(entity.getCode());
            entity.setDiffRate(entity.getRate() - prev.getRate());
        }
        result.put("keeps", keeps.stream().sorted(Comparator.comparing(StkOwnershipEntity::getDiffRate, Comparator.reverseOrder())).collect(Collectors.toList()));
        return RequestResult.success(result);
    }
}
