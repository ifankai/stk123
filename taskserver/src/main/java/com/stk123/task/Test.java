package com.stk123.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.Collections;

public class Test {

    public static void main(String[] args) {

        A a = new A();
        a.setCode("5000");

        B b = new B();
        //BeanUtil.copyProperties(a, b, CopyOptions.create().setFieldMapping(Collections.singletonMap("code","codeName")));
        //BeanUtils.

        System.out.println(b.getCodeName());
    }
}
@Data
class A {
    private String code;
}

@Data
class B {
    private String codeName;
}
