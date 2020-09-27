package com.stk123.spring.support.checkpoints;

import com.stk123.spring.dto.checkpoints.CheckResult;

public class ResultTrueOrFalse implements Result {

    private final static CheckResult CHECKRESULT_TRUE = new CheckResult("");
    private final static CheckResult CHECKRESULT_FALSE = new CheckResult("");

    private CheckResult cr;

    public ResultTrueOrFalse(boolean passed){
        cr = passed ? CHECKRESULT_TRUE : CHECKRESULT_FALSE;
    }

    @Override
    public CheckResult getCheckResult() {
        return cr;
    }
}
