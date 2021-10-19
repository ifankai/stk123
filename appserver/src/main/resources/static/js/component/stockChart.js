const _stockChartTemplate = `
    <div id="stockChart" style="height: 300px;"></div>
`;

const _stockChart = {
    template: _stockChartTemplate,
    props: {
    },
    data: function () {
        return {
            text: {}
        }
    },
    methods:{

    },
    computed: {

    },
    mounted() {
        var myChart = echarts.init(document.getElementById('stockChart'));
        window.onresize = function() {
            myChart.resize();
        };

        var option;

        const upColor = '#ec0000';
        const downColor = '#00da3c';
        function splitData(rawData) {
            let categoryData = [];
            let values = [];
            let volumes = [];
            for (let i = 0; i < rawData.length; i++) {
                categoryData.push(rawData[i].splice(0, 1)[0]);
                values.push(rawData[i]);
                volumes.push([i, rawData[i][4], rawData[i][0] > rawData[i][1] ? 1 : -1]);
            }
            return {
                categoryData: categoryData,
                values: values,
                volumes: volumes
            };
        }
        function calculateMA(dayCount, data) {
            var result = [];
            for (var i = 0, len = data.values.length; i < len; i++) {
                if (i < dayCount) {
                    result.push('-');
                    continue;
                }
                var sum = 0;
                for (var j = 0; j < dayCount; j++) {
                    sum += data.values[i - j][1];
                }
                result.push(+(sum / dayCount).toFixed(3));
            }
            return result;
        }

        /**
        [
         ["2004-01-02",10452.74,10409.85,10367.41,10554.96,168890000],
         ["2004-01-05",10411.85,10544.07,10411.85,10575.92,221290000],
         ["2004-01-06",10543.85,10538.66,10454.37,10584.07,191460000],
         ["2004-01-07",10535.46,10529.03,10432,10587.55,225490000],
         ...
         ]
        **/

        var ROOT_PATH =
            'https://cdn.jsdelivr.net/gh/apache/echarts-website@asf-site/examples';

        $.get(ROOT_PATH + '/data/asset/data/stock-DJI.json', function (rawData) {
            var data = splitData(rawData);
            myChart.setOption(
                (option = {
                    animation: false,
                    legend: {
                        bottom: 10,
                        left: 'center',
                        data: ['Dow-Jones index', 'MA5', 'MA10', 'MA20', 'MA30']
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross'
                        },
                        borderWidth: 1,
                        borderColor: '#ccc',
                        padding: 10,
                        textStyle: {
                            color: '#000'
                        },
                        position: function (pos, params, el, elRect, size) {
                            const obj = {
                                top: 10
                            };
                            obj[['left', 'right'][+(pos[0] < size.viewSize[0] / 2)]] = 30;
                            return obj;
                        }
                        // extraCssText: 'width: 170px'
                    },
                    axisPointer: {
                        link: [
                            {
                                xAxisIndex: 'all'
                            }
                        ],
                        label: {
                            backgroundColor: '#777'
                        }
                    },
                    toolbox: {
                        show:false,
                        /*feature: {
                            dataZoom: {
                                yAxisIndex: false
                            },
                            brush: {
                                type: ['lineX', 'clear']
                            }
                        }*/
                    },
                    brush: {
                        xAxisIndex: 'all',
                        brushLink: 'all',
                        outOfBrush: {
                            colorAlpha: 0.1
                        }
                    },
                    visualMap: {
                        show: false,
                        seriesIndex: [5,6],
                        dimension: 2,
                        pieces: [
                            {
                                value: 1,
                                color: downColor
                            },
                            {
                                value: -1,
                                color: upColor
                            }
                        ]
                    },
                    grid: [
                        {
                            left: '5%',
                            right: '3%',
                            top: '3%',
                            height: '50%'
                        },
                        {
                            left: '5%',
                            right: '3%',
                            top: '63%',
                            height: '16%'
                        },
                        {
                            left: '5%',
                            right: '3%',
                            top: '74%',
                            height: '16%'
                        }
                    ],
                    xAxis: [
                        {
                            type: 'category',
                            data: data.categoryData,
                            scale: true,
                            boundaryGap: false,
                            axisLine: { onZero: false },
                            splitLine: { show: false },
                            min: 'dataMin',
                            max: 'dataMax',
                            axisPointer: {
                                z: 100
                            }
                        },
                        {
                            type: 'category',
                            gridIndex: 1,
                            data: data.categoryData,
                            scale: true,
                            boundaryGap: false,
                            axisLine: { onZero: false },
                            axisTick: { show: false },
                            splitLine: { show: false },
                            axisLabel: { show: false },
                            min: 'dataMin',
                            max: 'dataMax'
                        },
                        {
                            type: 'category',
                            gridIndex: 2,
                            data: data.categoryData,
                            scale: true,
                            boundaryGap: false,
                            axisLine: { onZero: false },
                            axisTick: { show: false },
                            splitLine: { show: false },
                            axisLabel: { show: false },
                            min: 'dataMin',
                            max: 'dataMax'
                        }
                    ],
                    yAxis: [
                        {
                            scale: true,
                            splitArea: {
                                show: true
                            }
                        },
                        {
                            scale: true,
                            gridIndex: 1,
                            splitNumber: 2,
                            axisLabel: { show: false },
                            axisLine: { show: false },
                            axisTick: { show: false },
                            splitLine: { show: false }
                        },
                        {
                            scale: true,
                            gridIndex: 2,
                            splitNumber: 2,
                            axisLabel: { show: false },
                            axisLine: { show: false },
                            axisTick: { show: false },
                            splitLine: { show: false }
                        }
                    ],
                    dataZoom: [
                        {
                            type: 'inside',
                            xAxisIndex: [0, 1, 2],
                            start: 98,
                            end: 100
                        },
                        {
                            show: true,
                            xAxisIndex: [0, 1, 2],
                            type: 'slider',
                            top: '85%',
                            start: 98,
                            end: 100
                        }
                    ],
                    series: [
                        {
                            name: 'Dow-Jones index',
                            type: 'candlestick',
                            data: data.values,
                            itemStyle: {
                                color: upColor,
                                color0: downColor,
                                borderColor: undefined,
                                borderColor0: undefined
                            },
                            tooltip: {
                                formatter: function (param) {
                                    param = param[0];
                                    return [
                                        'Date: ' + param.name + '<hr size=1 style="margin: 3px 0">',
                                        'Open: ' + param.data[0] + '<br/>',
                                        'Close: ' + param.data[1] + '<br/>',
                                        'Lowest: ' + param.data[2] + '<br/>',
                                        'Highest: ' + param.data[3] + '<br/>'
                                    ].join('');
                                }
                            }
                        },
                        {
                            name: 'MA5',
                            type: 'line',
                            symbol: 'none',
                            data: calculateMA(5, data),
                            smooth: true,
                            lineStyle: {
                                width:0.3
                            }
                        },
                        {
                            name: 'MA10',
                            type: 'line',
                            symbol: 'none',
                            data: calculateMA(10, data),
                            smooth: true,
                            lineStyle: {
                                width:0.3
                            }
                        },
                        {
                            name: 'MA20',
                            type: 'line',
                            symbol: 'none',
                            data: calculateMA(20, data),
                            smooth: true,
                            lineStyle: {
                                width:0.3
                            }
                        },
                        {
                            name: 'MA30',
                            type: 'line',
                            symbol: 'none',
                            data: calculateMA(30, data),
                            smooth: true,
                            lineStyle: {
                                width:0.3
                            }
                        },
                        {
                            name: 'Volume',
                            type: 'bar',
                            xAxisIndex: 1,
                            yAxisIndex: 1,
                            data: data.volumes
                        },
                        {
                            name: 'Flow', //资金流
                            type: 'bar',
                            xAxisIndex: 2,
                            yAxisIndex: 2,
                            data: data.volumes
                        }
                    ]
                }),
                true
            );
            myChart.dispatchAction({
                type: 'brush',
                areas: [
                    {
                        brushType: 'lineX',
                        coordRange: ['2016-06-02', '2016-06-20'],
                        xAxisIndex: 0
                    }
                ]
            });
        });

        option && myChart.setOption(option);
    }

};