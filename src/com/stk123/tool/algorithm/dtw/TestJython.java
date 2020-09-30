package com.stk123.tool.algorithm.dtw;

import org.python.core.PyFunction;
import org.python.util.PythonInterpreter;

import java.util.Properties;

/**
 * jython 不能import numpy，暂时没有什么解决办法
 * https://blog.csdn.net/u010898743/article/details/81363432
 */
public class TestJython {

    public static void main(String args[]) {

        Properties props = new Properties();
        props.put("python.home", "C:/Users/KaiFan/AppData/Local/Programs/Python/Python38/Lib");
        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site", "false");
        Properties preprops = System.getProperties();
        PythonInterpreter.initialize(preprops, props, new String[0]);

        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("import sys");
        interpreter.exec("sys.path.append('C:/Users/KaiFan/AppData/Local/Programs/Python/Python38/Lib')");//python自己的
        interpreter.exec("sys.path.append('C:/Users/KaiFan/AppData/Local/Programs/Python/Python38/Lib/site-packages/numpy')");//jython自己的
        //interpreter.exec("sys.path.append('F:/workspace/wxserver/WebContent/py')");//我们自己写的

        interpreter.exec("days=('mod','Tue','Wed','Thu','Fri','Sat','Sun'); ");
        interpreter.exec("print days[1];");

        interpreter.execfile("./src/com/stk123/tool/algorithm/dtw/dtw.py");
        PyFunction func = (PyFunction) interpreter.get("test", PyFunction.class);
    }
}
