package com.stk123.model.json;

public class View {

    static class Public { }
    static class ExtendedPublic extends Public { }
    public static class Internal extends ExtendedPublic { }

}
