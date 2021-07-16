#!/bin/sh

STK_HOME=/home/income_gs_main_tst/dist/oracleclient/stk
JAVA_HOME=/usr/local/income_gs_main_tst/jdk150_11

cd ${STK_HOME}

DIRLIBS=${STK_HOME}/lib/*.jar 
for i in ${DIRLIBS} 
do 
    # if the directory is empty, then it will return the input string
    if [ "$i" != "${DIRLIBS}" ] ; then 
        LOCALCLASSPATH="$i":$LOCALCLASSPATH 
    fi 
done 

CLASSPATH=.:$LOCALCLASSPATH:${STK_HOME}/stk.jar

#echo $CLASSPATH
export CLASSPATH

javaOption="-Xms512m -Xmx1024m"

${JAVA_HOME}/bin/java ${javaOption} -cp ${CLASSPATH} task.DailyReport

#crontab -e
#0 1 * * * sh /home/income_gs_main_tst/dist/export.sh &
#/home/income_gs_main_tst/dist/config_inc/META-INF/deploy/export.properties
