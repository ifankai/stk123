package com.stk123.repository;

import com.stk123.entity.StkImportInfoEntity;
import com.stk123.entity.StkKeywordLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface StkKeywordLinkRepository extends JpaRepository<StkKeywordLinkEntity, Long> {

    StkKeywordLinkEntity findByKeywordIdAndCodeAndCodeTypeAndLinkType(Long keywordId, String code, Integer codeType, Integer linkType);

    List<StkKeywordLinkEntity> findAllByCodeAndLinkType(String code, Integer linkType);

    List<StkKeywordLinkEntity> findAllByCodeIn(List<String> codes);

    default Map<String, List<StkKeywordLinkEntity>> getAllByCodeIn(List<String> codes){
        List<StkKeywordLinkEntity> links = findAllByCodeIn(codes);
        Map<String, List<StkKeywordLinkEntity>> result = new LinkedHashMap<>(codes.size());
        for(StkKeywordLinkEntity n : links){
            List<StkKeywordLinkEntity> list = result.computeIfAbsent(n.getCode(), k -> new ArrayList<>());
            list.add(n);
        }
        return result;
    }

    List<StkKeywordLinkEntity> findAllByKeywordId(Long keywordId);

}
