package com.stk123.model.core.similar;

public interface Similar {

    boolean similar();

    Similar FALSE = () -> false;

    Similar TRUE = () -> true;

}
