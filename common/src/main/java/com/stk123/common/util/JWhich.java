package com.stk123.common.util;

public class JWhich {

    public static String which(String resourceName)  throws java.lang.Exception {
        if(resourceName==null)return "";
        resourceName = resourceName.replace('.', '/');
        resourceName = resourceName + ".class";


        ClassLoader cld = new JWhich().getClass().getClassLoader();
        java.util.Enumeration en = cld.getResources(resourceName);
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

        return buf.toString();

    }

}
