package com.stk123.common.util;

import java.io.IOException;

public class JWhich {

    public static String which(String resourceName) {
        if(resourceName==null)return "";
        resourceName = resourceName.replace('.', '/');
        resourceName = resourceName + ".class";


        ClassLoader cld = new JWhich().getClass().getClassLoader();
        java.util.Enumeration en = null;
        try {
            en = cld.getResources(resourceName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer buf = new StringBuffer();
        buf.append(resourceName);
        buf.append(": ");

        if (en == null || (!en.hasMoreElements()))
        {
            buf.append("not found");
        }
        else
        {
            boolean firstLoc = true;
            while (en.hasMoreElements())
            {
                if (!firstLoc)
                {
                    buf.append(", ");
                }

                java.net.URL url = (java.net.URL) (en.nextElement());
                buf.append(url.toString());
                firstLoc = false;
            }
        }
        System.out.println(buf.toString());
        return buf.toString();

    }

}
