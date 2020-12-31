package com.stk123.service.core;

import com.stk123.entity.StkErrorLogEntity;
import com.stk123.repository.StkErrorLogRepository;
import com.stk123.util.ExceptionUtils;
import com.stk123.util.ik.StringSimilarUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErrorService {

    public final static String ERROR_CODE_999999 = "999999";

    @Autowired
    private StkErrorLogRepository stkErrorLogRepository;

    public StkErrorLogEntity logError(String code, Exception e){
        String sException = ExceptionUtils.getExceptionAsString(e);
        StkErrorLogEntity stkErrorLogEntity = new StkErrorLogEntity();
        stkErrorLogEntity.setCode(code);
        stkErrorLogEntity.setError(sException);
        return stkErrorLogRepository.save(stkErrorLogEntity);
    }

    public boolean logErrorIfNoSimilarError(String code, Exception e) {
        List<StkErrorLogEntity> errors = stkErrorLogRepository.findAllByCodeOrderByInsertTimeDesc(code);
        String sException = ExceptionUtils.getExceptionAsString(e);
        boolean hasSimilar = false;
        for(StkErrorLogEntity error : errors){
            if(StringSimilarUtils.getSimilarRatio(sException, error.getError()) >= 0.95){
                hasSimilar = true;
                break;
            }
        }
        if(!hasSimilar) {
            StkErrorLogEntity stkErrorLogEntity = new StkErrorLogEntity();
            stkErrorLogEntity.setCode(code);
            stkErrorLogEntity.setError(sException);
            stkErrorLogRepository.save(stkErrorLogEntity);
            return true;
        }
        return false;
    }

    public boolean logErrorIfNoSimilarText(String code, String text, String errorMessage) {
        List<StkErrorLogEntity> errors = stkErrorLogRepository.findAllByCodeOrderByInsertTimeDesc(code);
        boolean hasSimilar = false;
        for(StkErrorLogEntity error : errors){
            if(StringSimilarUtils.getSimilarRatio(text, error.getText()) >= 0.95){
                hasSimilar = true;
                break;
            }
        }
        if(!hasSimilar) {
            StkErrorLogEntity stkErrorLogEntity = new StkErrorLogEntity();
            stkErrorLogEntity.setCode(code);
            stkErrorLogEntity.setText(text);
            stkErrorLogEntity.setError(errorMessage);
            stkErrorLogRepository.save(stkErrorLogEntity);
            return true;
        }
        return false;
    }
}
