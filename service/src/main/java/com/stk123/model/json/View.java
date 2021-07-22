package com.stk123.model.json;

public class View {

    public static class Default { }

    static class Public { }
    static class ExtendedPublic extends Public { }
    public static class Internal extends ExtendedPublic { }

    public static class Score { }

    public static class All extends Default { }
}
