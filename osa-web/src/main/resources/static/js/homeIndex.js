var resourceNumChart = null;
var resourceNumOption = "";

var activityAccountChart = null;
var activityAccountOption = "";

var operationChart = null;
var operationOption = "";

var operaNumChart = null;
var operaNumOption = "";

var operaNumChart1 = null;
var operaNumOption1 = "";

var remoteWind = {};

var app = new Vue({
    el: '#contentDiv',
    data: {
        operateRecord: [],
        userNums: 0,
        assetNums: 0,
        userOnlineNums: 0,
        sessionOnlineNums: 0,
        approvedNums: 0,
        unApprovedNums: 0,
        //表格配置
        tableConfig: {
            id: 'conversationTable', //给table一个id,调用gd.tableReload('demoTable');可重新加载表格数据并保持当前页码，gd.tableReload('demoTable'，1)，第二个参数可在加载数据时指定页码
            length: 50, //每页多少条,默认50，可选
            curPage: 1, //当前页码，默认1，可选
            lengthMenu: [10, 30, 50, 100], //可选择每页多少条，默认[10, 30, 50, 100]，可选
            enableJumpPage: false, //启用跳页，默认false，可选
            enableLengthMenu: true, //启用可选择每页多少条，默认true，可选
            enablePaging: false, //启用分页,默认true，可选
            orderColumn: 'ip', //排序列
            orderType: 'desc', //排序规则，desc或asc,默认desc
            columnResize: true, //启用列宽调，默认true，可选
            showFooter: false, //显示footer,默认为true
            //lazy: true,//懒加载数据，调用gd.table('id').reload()对表格数据进行加载,默认为false
            //loading: true,//显示loading,默认为false
            //filterImmediately: true,//高级查询勾选后立即触发，默认为false
            fillBlank: '--',
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + "/session/getRealtimeSession",
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.userUsername,
                            obj.assetName,
                            obj.assetAddr,
                            obj.id
                        ]
                    });
                    return data;
                },
                //请求参数
                data: {}
            },
            columns: [{
                    name: "userUsername",
                    head: '<span style="margin-left:12px">用户名</span>',
                    title: true,
                    width: window.screen.width > 1366 ? 100 : 80,
                    render:function (cell, row, raw){
                        return '<span style="margin-left:12px">'+cell+'</span>';
                    }
                },
                {
                    head: '目标资源',
                    title: true
                },
                {
                    head: '目标资源IP',
                    title: true,
                    width: window.screen.width > 1366 ? 120 : "auto",
                },
                {
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    width: 80,
                    operates: [{
                            icon: 'icon-play',
                            title: '播放',
                            action: function(cell, row, raw) {
                                remoteWind[raw.id] = window.open(
                                    baseUrl + '/granted/remote?' + $.param({ id: raw.id })
                                );
                            }
                        },
                        {
                            icon: 'icon-insulate',
                            title: '阻断',
                            action: function(cell, row, raw) {
                                var dom = gd.showConfirm({
                                    id: 'wind',
                                    content: '确定要阻断吗?',
                                    btn: [{
                                            text: '阻断',
                                            class: 'gd-btn-danger',
                                            enter: true,
                                            action: function(dom) {
                                                gd.post(
                                                    baseUrl + '/session/blockSessionBySessionId', { assetId: raw.assetId, sessionId: raw.id },
                                                    function(msg) {
                                                        if (msg.resultCode == 0) {
                                                            gd.showSuccess('阻断成功！');
                                                            gd.table('conversationTable').reload();
                                                            try {
                                                                remoteWind[raw.id].close();
                                                            } catch (e) {}
                                                        } else {
                                                            gd.showError('阻断失败！' + (msg.resultMsg || ''));
                                                        }
                                                    }
                                                );
                                            }
                                        },
                                        {
                                            text: '取消',
                                            action: function() {
                                                dom.close();
                                            }
                                        }
                                    ]
                                });
                            }
                        }
                    ]
                }
            ]
        }
    },
    mounted: function() {
        initResourceNumOption();
        initOperationNum();
        getNumsAll();
        // 资源数量
        getResourceData();
        getTopData();
        getOperationTimes();
        interval = setInterval('app.autoScroll(".operanote")',1000);
        $(window).resize(function() {
            resourceNumChart.resize();
            activityAccountChart.resize();
            operationChart.resize();
            operaNumChart.resize();
        });
    },
    methods:{
        autoScroll:function(obj){
            this.$nextTick(function() {
                if(($(".operanote ul li").length * ($(".operanote ul li").height() + 20)) > $(".operanote").height() ){
                     if($(".operanote ul li").length > 10){
                        var time = parseInt($(".operanote ul li").length/3) * 5000;
                    }else{
                        var time = 5000;
                    }
                    $(obj).find("ul").animate({
                        marginTop : "-" + (($(".operanote ul li").length * ($(".operanote ul li").height() + 20)) - $(obj).height()) + "px"
                    },time,'linear',function(){
                        $(this).css({marginTop : "0px"}).find("li:first").appendTo(this);
                    })
                }
            });
        }
    }
});


/**
 * 本周运维次数数据获取
 * @return {[type]} [description]
 */
function getOperationTimes() {
    $.get(baseUrl + "/asset/getHomeAssetsWeek", function(msg) {
        var dataX = [];
        var data0 = [],
            data1 = [],
            data2 = [],
            data3 = [];
        if (msg.resultCode == 0) {
            operaNumChart = echarts.init(document.getElementById('opera_num'));
            if (msg.data.length > 0) {
                $.each(msg.data.reverse(), function(index, val) {
                    dataX.push(val.date);
                    data0.push(val.hostNums);
                    data1.push(val.netNums);
                    data2.push(val.dbNums);
                    data3.push(val.applicationNums);
                })
                /*添加多余数据，为了达到展示效果，醉*/
                dataX.push("end");
                dataX.unshift("start");
                data0.push(0);
                data0.unshift(0);
                data1.push(0);
                data1.unshift(0);
                data2.push(0);
                data2.unshift(0);
                data3.push(0);
                data3.unshift(0);

                operaNumOption.xAxis.data = dataX;
                operaNumOption.series[0].data = data0;
                operaNumOption.series[1].data = data1;
                operaNumOption.series[2].data = data2;
                operaNumOption.series[3].data = data3;
            }

            operaNumChart.setOption(operaNumOption);
        }
    })
}

/**
 * 获取各种数量
 * @return {[type]} [description]
 */
function getNumsAll() {
    $.get(baseUrl + "/logSystem/getHomeCounts", function(msg) {
        if (msg.resultCode == 0) {
            app.userNums = msg.data.userNums;
            app.assetNums = msg.data.assetNums;
            app.userOnlineNums = msg.data.userOnlineNums;
            app.sessionOnlineNums = msg.data.sessionOnlineNums;
            app.approvedNums = msg.data.approvedNums;
            app.unApprovedNums = msg.data.unApprovedNums;
        }
    })
}

/**
 * 获取资源数量数据展示
 * @return {[type]} [description]
 */
function getResourceData() {
    $.get(baseUrl + "/asset/infoForHomePage", {}, function(msg) {
        var legendData = [],
            seriesData = [];
        if (msg.resultCode == 0) {
            resourceNumChart = echarts.init(document.getElementById('resource_num'));
            if (msg.data.length > 0) {
                $.each(msg.data, function(index, val) {
                    legendData.push(val.typeName);
                    seriesData.push({
                        value: val.count,
                        name: val.typeName,
                        percent: parseInt(val.count * 100 / msg.total)
                    })
                })
                resourceNumOption.series[0].data[0].value = msg.total;
            } else {
                legendData = ['-- ', '--  ', '--   ', '--    ', '--     ']
                seriesData = [
                    { value: 0, name: '-- ', percent: "0" },
                    { value: 0, name: '--  ', percent: "0" },
                    { value: 0, name: '--   ', percent: "0" },
                    { value: 0, name: '--    ', percent: "0" },
                    { value: 0, name: '--     ', percent: "0" }
                ]
                resourceNumOption.series[0].data[0].value = 0;
            }
            resourceNumOption.series[1].data = seriesData;
            resourceNumOption.legend.data = legendData;
            resourceNumChart.setOption(resourceNumOption);
        }

    })
}

/**
 * top5 和 日志
 * @return {[type]} [description]
 */
function getTopData() {
    $.get(baseUrl + "/logSystem/getLogSystemHome", function(msg) {
        if (msg.resultCode == 0) {
            activityAccountChart = echarts.init(document.getElementById('active_account'));
            operationChart = echarts.init(document.getElementById('resource_operate'));
            /*用户top*/
            var yData = [],
                xData = [],
                eventData = {},
                xDataAll = [];
            var msgData = msg.data.userTop.reverse();
            if (msgData.length > 0) {
                $.each(msgData, function(index, val) {
                    xData.push(val.loginTimes);
                    yData.push(val.userName);
                });
            } else {
                xData = [0, 0, 0, 0, 0];
                yData = ['-- ', '-- ', '-- ', '-- ', '-- ']
            }
            eventData.xData = xData;
            eventData.yData = yData;
            initActiveOption(eventData);
            var data1Max = Math.max.apply(Math, xData);
            data1Max = data1Max == 0 ? 10000 : data1Max;
            for (var i = 0; i < xData.length; i++) {
                xDataAll.push(data1Max * 1.0);
            }
            activityAccountOption.yAxis.data = yData;
            activityAccountOption.series[0].data = xData;
            activityAccountOption.series[1].data = xDataAll;
            activityAccountChart.setOption(activityAccountOption);

            /*资源top*/
            var yDataR = [],
                xDataR = [],
                eventDataR = {},
                xDataAllR = [];
            var msgDataR = msg.data.assetTop.reverse();
            if (msgDataR.length > 0) {
                $.each(msgDataR, function(index, val) {
                    xDataR.push(val.assetOperations);
                    yDataR.push(val.assetName);
                });
            } else {
                xDataR = [0, 0, 0, 0, 0];
                yDataR = ['-- ', '-- ', '-- ', '-- ', '-- ']
            }
            eventDataR.xDataR = xDataR;
            eventDataR.yDataR = yDataR;
            initOperationOption(eventDataR);
            initActiveOption(eventDataR);
            var data1MaxR = Math.max.apply(Math, xDataR);
            data1MaxR = data1MaxR == 0 ? 10000 : data1MaxR;
            for (var i = 0; i < xDataR.length; i++) {
                xDataAllR.push(data1MaxR * 1.0);
            }

            operationOption.yAxis.data = yDataR;
            operationOption.series[0].data = xDataR;
            operationOption.series[1].data = xDataAllR;
            operationChart.setOption(operationOption);

            /*最近运维记录*/
            var noteLine = [];
            if (msg.data.logOperation.length > 0) {
                $(".gd-none-data").addClass("gd-home-hide");
                $.each(msg.data.logOperation, function(index, val) {
                    noteLine.push({
                        name: val.userName,
                        ip: val.assetIp,
                        source: val.assetName,
                        time: val.time
                    })
                });
                app.operateRecord = noteLine;
            } else {
                $(".gd-none-data").removeClass("gd-home-hide");
            }
        }
    })
}

/**
 * 资源数量
 * @return {[type]} [description]
 */
function initResourceNumOption() {
    resourceNumOption = {
        color: ["#CECECE", "#F5A623", "#456CCC", "#6785CF", "#58A5E3", "#81D4FA", "#B3E5FC", "#E062AE", "#E690D1", "#e7bcf3", "#9d96f5", "#8378EA", "#96BFFF"],
        tooltip: {
            trigger: 'item',
            // backgroundColor: 'rgba(255,255,255,0.7)',
            textStyle: {
                color: '#fff'
            },
            formatter: function(params) {
                return '<div style="padding: 16px 20px;color: #fff">' +
                    '<div style="margin-bottom: 16px;text-align: center">' +
                    params.data.percent + '%' +
                    '</div>' +
                    '<div>' +
                    '<span style="margin-right: 20px">' +
                    params.data.name +
                    '</span>' +
                    '<span>' +
                    params.data.value +
                    '</span>' +
                    '</div>' +
                    '</div>'
            }
        },
        legend: {
            type: 'scroll', //legend 过多时显示分页
            orient: 'vertical',
            icon: "circle",
            itemWidth: 10,
            itemHeight: 10,
            orient: 'vertical',
            right: 40,
            top: 'center',
            data: [],
            textStyle: {
                color: '#999'
            },
            formatter: function(data) {
                var legendName = "";
                $.each(resourceNumOption.series[1].data, function(index, val) {
                    if (val.name == data) {
                        legendName = data.length > 8 ? ('{a|' + data.substring(0, 8) + ' ... ' + '}' + '{b|' + val.value + '}') : ('{a|' + data + '}' + '{b|' + val.value + '}');
                    }
                });
                return legendName;
            },
            textStyle: {
                color: '#999',
                rich: {
                    a: {
                        width: 85
                    },
                    b: {
                        width: 10
                    }
                }
            },
            tooltip: {
                show: true,
                textStyle: {
                    color: '#f00'
                },
            }
        },
        series: [{
            name: '',
            type: 'pie',
            clockWise: false, //顺时加载
            hoverAnimation: false, //鼠标移入变大
            radius: ['50%', '70%'],
            center: ['40%', '50%'],
            itemStyle: {
                normal: {
                    color: 'rgba(0,0,0,0)'
                }
            },
            tooltip: {
                show: false
            },
            data: [{
                value: 0,
                name: '',
            }],
            label: {
                normal: {
                    formatter: function(params) {
                        return params.data.value + ' {unit|个}';
                    },
                    position: 'center',
                    textStyle: {
                        fontSize: 28,
                        color: "#232D3B",
                        padding: [0, 0, 0, 20]
                    },
                    rich: {
                        unit: {
                            color: "#999",
                            fontSize: 14,
                            padding: [0, 0, 0, 0],
                            verticalAlign: 'bottom',
                        }
                    }
                }
            }
        }, {
            name: '',
            type: 'pie',
            clockWise: false, //顺时加载
            hoverAnimation: true, //鼠标移入变大
            radius: ['50%', '70%'],
            center: ['40%', '50%'],
            data: [],
            itemStyle: {
                normal: {
                    label: {
                        show: false
                    },
                    labelLine: {
                        show: false
                    },
                }
            }
        }]
    };
}

/**
 * Top5
 * @param  {[type]} data [description]
 * @return {[type]}      [description]
 */
function initActiveOption(data) {
    activityAccountOption = returnLine(data);
}

function initOperationOption(data) {
    operationOption = returnLine(data);
}

function returnLine(data) {
    var option = {
        grid: {
            left: "20",
            top: "20",
            bottom: "0",
            right: "80",
            containLabel: true
        },
        legend: {
            show: true
        },
        tooltip: {
            show: "true",
            trigger: 'axis',
            axisPointer: { // 坐标轴指示器，坐标轴触发有效
                type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
            },
            formatter: function(params) {
                return '<div style="padding: 16px 20px;color: #fff">' +
                    '<div>' +
                    '<span style="margin-right: 20px">' +
                    params[0].name +
                    '</span>' +
                    '<span>' +
                    params[0].data +
                    '</span>' +
                    '</div>' +
                    '</div>'
            }
        },

        xAxis: {
            splitLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLine: {
                show: false
            },
            axisLabel: {
                show: false
            }
        },
        yAxis: {
            splitLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLine: {
                show: false
            },
            axisLabel: {
                color: "#333333",
                formatter: function(params) {
                    return params = params.length > 6 ? (params.substring(0, 5) + "...") : params;
                }
            },
            data: []
        },
        series: [{
                type: "bar",
                barGap: "-100%",
                barWidth: 10,
                itemStyle: {
                    normal: {
                        color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [{
                                offset: 1,
                                color: "#81D4FA"
                            },
                            {
                                offset: 0,
                                color: "#6785CF"
                            }
                        ]),
                        barBorderRadius: 12
                    }
                },
                z: -10,
                // data: data.data1
                data: []
            },
            {
                type: "bar",
                barGap: "-100%",
                barWidth: 10,
                itemStyle: {
                    normal: {
                        color: "#EBF0F2 ",
                        borderColor: "#EBF0F2 ",
                        borderWidth: 0,
                        shadowColor: "#EBF0F2 ",
                        shadowBlur: 0,
                        barBorderRadius: 12
                    }
                },
                label: {
                    normal: {
                        show: true,
                        position: "right",
                        textStyle: {
                            color: "#333",
                            fontSize: 14
                        },
                        offset: [10, 0],
                        formatter: function(params) {
                            if (data.xData) {
                                return data.xData[params.dataIndex];
                            } else {
                                return data.xDataR[params.dataIndex];
                            }

                        }
                    }
                },
                z: -12,
                data: []
            }
        ]
    };
    return option;
}
/**
 * 每周运维数量
 */
function initOperationNum() {
    operaNumOption = {
        color: ["#6289D5", "#7FA8DF", "#97CBEF", "#D8F1FC", "#FFDB5C", "#ff9f7f", "#fb7293", "#E062AE", "#E690D1", "#e7bcf3", "#9d96f5", "#8378EA", "#96BFFF"],
        tooltip: {
            trigger: 'axis',
            axisPointer: { // 坐标轴指示器，坐标轴触发有效
                type: 'line' // 默认为直线，可选为：'line' | 'shadow'
            },
            formatter: function(params) {
                if (params[0].axisValue != "start" && params[0].axisValue != "end") {
                    return '<div style="padding: 16px 20px;color: #fff">' +
                        '<div>' + params[0].axisValue +
                        '</div>' +
                        '<div class="circle-title">' +
                        '<span class="host-bgcolor"></span><span>主机</span><span>' + params[0].data + '</span>' +
                        '</div>' +
                        '<div class="circle-title">' +
                        '<span class="net-bgcolor"></span><span>网路设备' + '</span><span>' + params[1].data + '</span>' +
                        '</div>' +
                        '<div class="circle-title">' +
                        '<span class="sql-bgcolor"></span><span>数据库</span><span>' + params[2].data + '</span>' +
                        '</div>' +
                        '<div class="circle-title">' +
                        '<span class="app-bgcolor"></span><span>应用</span><span>' + params[3].data + '</span>' +
                        '</div>' +
                        '</div>'
                }
            }
        },
        grid: {
            top: 30,
            right: 1,
            bottom: 3,
            left: 0,
        },
        xAxis: {
            show: true,
            type: 'category',
            boundaryGap: false,
            nameLocation: "end",
            inside: true,
            offset: -40,
            axisLabel: {
                show: true,
                color: '#999999',
                interval: 0,
                showMinLabel: false,
                showMaxLabel: false,
                align:window.screen.width > 1366 ? "center" : "left",
                formatter: function(params) {
                    if (params != "--") {
                        return params.substring(params.indexOf("-") + 1);
                    } else {
                        return params;
                    }
                }
            },
            axisLine: {
                lineStyle: {
                    show: false,
                    color: '#eee'
                }
            },
            axisTick: {
                show: false
            },
            data: ['start', '--', '--', '--', '--', '--', '--', '--', 'end'],
        },
        yAxis: {
            type: 'value',
            scale: true,
            axisLine: {
                show: false
            },
            axisLabel: {
                showMinLabel: false,
                inside: true,
                textStyle: {
                    fontSize: 14,
                },
                backgroundColor: "#fff",
                formatter: function(value) {
                    return '{value|' + value + '}';
                },
                rich: {
                    value: {
                        width: window.screen.width > 1366 ? 60 : 30,
                        height: window.screen.width > 1366 ? 30 : 20,
                        padding: window.screen.width > 1366 ? [0, 0, 0, 15] :[0, 0, 0, 5],
                    }
                }
            },
            splitLine: {
                show: true,
                lineStyle: {
                    color: ['#eeeeee']
                }
            },
            axisTick: {
                show: false
            },
        },
        series: [{
            type: 'line',
            // smooth: true,
            stack: '总量',
            symbolSize: 1,
            symbol: 'circle',
            areaStyle: {
                normal: {
                    opacity: "0.4",
                }
            },
            data: [0, 0, 0, 0, 0, 0, 0, 0, 0]
        }, {
            type: 'line',
            stack: '总量',
            symbolSize: 1,
            symbol: 'circle',
            areaStyle: {
                normal: {
                    opacity: "0.4",
                }
            },
            data: [0, 0, 0, 0, 0, 0, 0, 0, 0]
        }, {
            type: 'line',
            stack: '总量',
            symbolSize: 1,
            symbol: 'circle',
            areaStyle: {
                normal: {
                    opacity: "0.4",
                }
            },
            data: [0, 0, 0, 0, 0, 0, 0, 0, 0]
        }, {
            type: 'line',
            stack: '总量',
            symbolSize: 1,
            symbol: 'circle',
            areaStyle: {
                normal: {
                    opacity: "0.4",
                }
            },
            data: [0, 0, 0, 0, 0, 0, 0, 0, 0]
        }]
    };
}
