package com.stk123.service.core;

import com.stk123.entity.StkKeywordEntity;
import com.stk123.entity.StkKeywordLinkEntity;
import com.stk123.repository.BaseRepository;
import com.stk123.repository.StkKeywordLinkRepository;
import com.stk123.repository.StkKeywordRepository;
import com.stk123.service.StkConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class KeywordService {

    @Autowired
    private StkKeywordRepository stkKeywordRepository;
    @Autowired
    private StkKeywordLinkRepository stkKeywordLinkRepository;

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

    public int addKeywordAndLink(String keyword, String code, Integer codeType, Integer linkType){
        StkKeywordEntity stkKeywordEntity = stkKeywordRepository.findByName(keyword);
        if(stkKeywordEntity != null){
            if(stkKeywordEntity.getStatus() == StkConstant.KEYWORD_STATUS__1){
                return -1;
            }
        }else{
            stkKeywordEntity = new StkKeywordEntity();
            stkKeywordEntity.setName(keyword);
            stkKeywordEntity.setStatus(StkConstant.KEYWORD_STATUS_1);
            stkKeywordEntity.setInsertTime(new Date());
            stkKeywordRepository.save(stkKeywordEntity);
        }
        StkKeywordLinkEntity stkKeywordLinkEntity = stkKeywordLinkRepository.findByKeywordIdAndCodeAndCodeTypeAndLinkType(stkKeywordEntity.getId(), code, codeType, linkType);
        if(stkKeywordLinkEntity == null){
            stkKeywordLinkEntity = new StkKeywordLinkEntity();
            stkKeywordLinkEntity.setKeywordId(stkKeywordEntity.getId());
            stkKeywordLinkEntity.setCode(code);
            stkKeywordLinkEntity.setCodeType(codeType);
            stkKeywordLinkEntity.setLinkType(linkType);
            stkKeywordLinkEntity.setInsertTime(new Date());
            stkKeywordLinkRepository.save(stkKeywordLinkEntity);
            return 1;
        }
        return 0;
    }
}
