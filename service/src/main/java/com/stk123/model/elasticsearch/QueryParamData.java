package com.stk123.model.elasticsearch;

import lombok.*;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.Arrays;


/**
 * ES查询参数类
 *
 * Created by lms on 2019/3/13 14:39
 */
public class QueryParamData {

    // 查询索引类型
    @Getter
    private String[] indices;
    // 查询条件
    @Getter
    private Condition[] conditions;
    // 页码
    @Setter
    @Getter
    private int pageIndex = 1;
    // 每页显示的数
    @Setter
    @Getter
    private int pageSize = 20;

    public void setIndices(String... indices) {
        this.indices = indices;
    }

    public void setConditions(Condition... conditions) {
        this.conditions = conditions;
    }

    public void addCondition(Condition condition){
        ArrayUtils.add(conditions, condition);
    }
}
