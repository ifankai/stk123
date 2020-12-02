package com.stk123.model.core.similar;

public interface Similar {

    boolean similar();

    Similar FALSE = new Similar() {
        @Override
        public boolean similar() {
            return false;
        }
    };

    Similar TRUE = new Similar() {
        @Override
        public boolean similar() {
            return true;
        }
    };

}
