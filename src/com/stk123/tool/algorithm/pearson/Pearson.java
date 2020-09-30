package com.stk123.tool.algorithm.pearson;

import com.stk123.model.Index;
import com.stk123.model.K;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * https://blog.csdn.net/yuhk231/article/details/80810427
 */
public class Pearson {

    public static List<Factor> factors = new ArrayList<Factor>();
    static {
        factors.add(new Factor("open"));
        factors.add(new Factor("high"));
        factors.add(new Factor("low"));
        factors.add(new Factor("close"));
        factors.add(new Factor("volumn"));

        double weights = 0;
        for(Factor factor : factors){
            weights += factor.getWeight();
        }
        for(Factor factor : factors){
            factor.setWeight(factor.getWeight()/weights);
        }
    }

    public static void main(String[] args) throws Exception {
        Index sIndex = new Index("600600");
        sIndex.getKsRealTimeOnDay();
        List<K> list = sIndex.getKs().subList(0, 30);
        list.get(list.size()-1).setBefore(null);
        /*for(K k : list){
            System.out.println(k);
        }*/

        String[] compares = new String[]{"600600"/*,"600601","600602"*/};
        for(String code : compares) {
            Index cIndex = new Index(code);
            cIndex.getKsRealTimeOnDay();
            System.out.println("["+code+"]:"+Pearson.calcSimilar(sIndex.getK(), cIndex.getK(1)));
        }
    }

    /*
     * 协方差计算 Covariance
     * ∑XY-∑X*∑Y/N
     * @param {array} source 源K线数据
     * @param {array} data 对比的K线数据,data.length=source.length
     * @param {string} field 参数

    calcCov=function(source,data,field){
        var i,l,mulE,sourceE,dataE;
        mulE=0;
        sourceE=0;
        dataE=0;
        for(i=0,l=source.length;i<l;i++){
            mulE+=source[i][field]*data[i][field];
            sourceE+=source[i][field];
            dataE+=data[i][field];
        }
        return mulE-sourceE*dataE/l;
    };
    */
    public static double calcCovariance(K source, K data, String field) throws Exception {
        int size = 0;
        double mulE = 0;
        double sourceE = 0;
        double dataE = 0;
        K sourceBefore = source;
        K dataBefore = data;
        while(sourceBefore != null) {
            mulE += sourceBefore.getValue(field) * dataBefore.getValue(field);
            sourceE += sourceBefore.getValue(field);
            dataE += dataBefore.getValue(field);

            sourceBefore = sourceBefore.getBefore();
            dataBefore = dataBefore.getBefore();
            size++;
        }
        return mulE-sourceE*dataE/size;
    }

    /*
     * 皮尔森分母计算
     * Math.sqrt((∑X^2-(∑X)^2/N)*((∑Y^2-(∑Y)^2/N))
     * @param {array} source 源K线数据
     * @param {array} data 对比的K线数据,data.length=source.length
     * @param {string} field 参数

    calcDenominator=function(source,data,field){
        var i,l,sourceSquareAdd,sourceAdd,dataSquareAdd,dataAdd;
        sourceSquareAdd=0;
        sourceAdd=0;
        dataSquareAdd=0;
        dataAdd=0;
        for(i=0,l=source.length;i<l;i++){
            sourceSquareAdd+=source[i][field]*source[i][field];
            sourceAdd+=source[i][field];
            dataSquareAdd+=data[i][field]*data[i][field];
            dataAdd+=data[i][field];
        }
        return Math.sqrt((sourceSquareAdd-sourceAdd*sourceAdd/l)*(dataSquareAdd-dataAdd*dataAdd/l));
    };
    */
    public static double calcDenominator(K source, K data, String field) throws Exception {
        int size = 0;
        double sourceSquareAdd = 0;
        double sourceAdd = 0;
        double dataSquareAdd = 0;
        double dataAdd = 0;
        K sourceBefore = source;
        K dataBefore = data;
        while(sourceBefore != null) {
            double sourceValue = sourceBefore.getValue(field);
            double dataValue = dataBefore.getValue(field);
            sourceSquareAdd += sourceValue * sourceValue;
            sourceAdd += sourceValue;
            dataSquareAdd += dataValue * dataValue;
            dataAdd += dataValue;

            sourceBefore = sourceBefore.getBefore();
            dataBefore = dataBefore.getBefore();
            size++;
        }
        return Math.sqrt((sourceSquareAdd-sourceAdd*sourceAdd/size)*(dataSquareAdd-dataAdd*dataAdd/size));
    }

    /*
     * 对比两组输入数据的相似度
     * @param {array} source 源K线数据
     * @param {array} data 对比的K线数据,data.length=source.length
     * @param {string} field 参数

    compare=function(source,data,field){
        var numerator,denominator;
        if(source.length!=data.length){
            console.error("length is different!");
            return ;
        }
        numerator=calcCov(source,data,field);
        denominator=calcDenominator(source,data,field);
        return numerator/denominator;
    };
    */
    public static double compare(K source, K data, String field) throws Exception {
        double numerator = calcCovariance(source,data,field);
        System.out.println("calcCovariance:"+numerator);
        double denominator = calcDenominator(source,data,field);
        System.out.println("calcDenominator:"+denominator);
        return numerator/denominator;
    }


    public static double calcSimilar(K source, K data) throws Exception {
        double similar = 0;
        for(Factor factor : factors){
            double tmp = compare(source, data, factor.getField()) * factor.getWeight();
            //TODO 如果tmp有<=0的直接返回以便提高效率
            System.out.println(factor.getField()+":"+tmp);
            similar += tmp;
        }
        return similar;
    }

    /*
     * 遍历计算，取最大值算法优化，无需存储无意义的全部数据
     * 2个属性17ms，最高相似度0.9505
     * 600570:600571{position: 20130415, similar: 0.9505145006910938}

    compareSimilarKViolentOptimize=function(code,period,data){
        var i,l,compareData,startTime,result,similarValue;
        compareData=[];
        for(i=0,l=data.length;i<l;i++){
            compareData[i]={
                    date:data[i][0],
                    open:data[i][1],
                    high:data[i][2],
                    low:data[i][3],
                    close:data[i][4],
                    amount:data[i][5]
            };
        }
        startTime=new Date().getTime();
        i=0;
        result={
                start:data[i][0],
                end:data[i+compareCount][0],
                similar:calcSimilar(sourceData,compareData.slice(i,i+compareCount))
        };
        for(i=1,l=data.length-31;i<l;i++){
            similarValue=calcSimilar(sourceData,compareData.slice(i,i+compareCount));
            if(result.similar<similarValue){
                result={
                        start:data[i][0],
                        end:data[i+compareCount][0],
                        similar:similarValue
                };
            }
        }
        console.log(result);
        console.log("calc cost:",new Date().getTime()-startTime);
    };
    */
}

@Setter
@Getter
class Factor {
    private String field;
    private double weight = 1.0;

    Factor(String field){
        this.field = field;
    }
}
