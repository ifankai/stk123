package com.stk123.repository;

import com.stk123.entity.StkKeywordLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkKeywordLinkRepository extends JpaRepository<StkKeywordLinkEntity, Long> {

    StkKeywordLinkEntity findByKeywordIdAndCodeAndCodeTypeAndLinkType(Long keywordId, String code, Integer codeType, Integer linkType);
}
