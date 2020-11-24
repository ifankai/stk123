package com.stk123.service.support;

import java.sql.Types;

public @interface Scalar {

    int type() default Types.JAVA_OBJECT;

}
