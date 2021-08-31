package com.stk123.service.core;

import com.stk123.repository.BaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class KeywordService {

    private static String sql_getKeywordByLinkType = "select distinct b.name name from stk_keyword_link a, stk_keyword b " +
            "where b.status=0 and a.link_type=:1 and a.keyword_id=b.id";
    @Transactional
    public List<String> getKeywordByLinkType(int linkType) {
        return BaseRepository.getInstance().list2String(sql_getKeywordByLinkType, linkType);
    }

    private static String sql_getKeywordByStatus = "select name from stk_keyword where status=:1";
    @Transactional
    public List<String> getKeywordByStatus(int status) {
        return BaseRepository.getInstance().list2String(sql_getKeywordByStatus, status);
    }

}
