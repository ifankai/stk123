/*
 * Pearson皮尔森相关系数计算
 * (∑XY-∑X*∑Y/N)/(Math.sqrt((∑X^2-(∑X)^2/N)*((∑Y^2-(∑Y)^2/N)))
 */
pearsonManager=(function(){
    var compare,calcCov,calcDenominator;

    /*
     * 协方差计算
     * ∑XY-∑X*∑Y/N
     * @param {array} source 源K线数据
     * @param {array} data 对比的K线数据,data.length=source.length
     * @param {string} field 参数
     */
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

    /*
     * 皮尔森分母计算
     * Math.sqrt((∑X^2-(∑X)^2/N)*((∑Y^2-(∑Y)^2/N))
     * @param {array} source 源K线数据
     * @param {array} data 对比的K线数据,data.length=source.length
     * @param {string} field 参数
     */
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

    /*
     * 对比两组输入数据的相似度
     * @param {array} source 源K线数据
     * @param {array} data 对比的K线数据,data.length=source.length
     * @param {string} field 参数
     */
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

    return {
        compare:compare
    };
})();