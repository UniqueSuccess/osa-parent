var resourceChart = "";
var resourceOption = "";
var nowTime = new Date().getHours() + ":" + new Date().getMinutes();
var startTime = GetDateStr(-7) + " " + nowTime;
var endTime = GetDateStr(0) + " " + nowTime;
var searchInput = "";
var pageAllNum = 0;
var opeType = [ //设置检索条件
    {
        label: '移动',
        value: '1'
    }, {
        label: '复制',
        value: '2'
    }, {
        label: '删除',
        value: '3'
    }, {
        label: '重命名',
        value: '4'
    }, {
        label: '创建目录',
        value: '5'
    }, {
        label: '新建',
        value: '6'
    }, {
        label: '删除目录',
        value: '8'
    }, {
        label: '恢复',
        value: '9'
    }
]
var app = new Vue({
    el: '#contentDiv',
    data: {
        recentRangePickerConfig: {
            value: ['最近7天'], //设置初始时间,可选
            dateRange: true, //日期范围选择
            timePicker: true, //展示时间选择器
            timeStep: 15, //时间最小单位
            change: function(time) {
                startTime = time.split(" ~ ")[0];
                endTime = time.split(" ~ ")[1];
                getEcharsData(startTime,endTime, searchInput);
                gd.table('resourceTable').reload(1, {
                    startTime: startTime,
                    endTime:endTime
                })
            },
            items: [{
                value: 8,
                label: '最近8小时'
            }, {
                value: 24,
                label: '最近1天'
            }, {
                value: 72,
                label: '最近3天'
            }, {
                value: 7 * 24,
                label: '最近7天'
            }, {
                value: 15 * 24,
                label: '最近15天'
            }, {
                value: 30 * 24,
                label: '最近30天'
            }]
        },
        toolbarConfig: [{
                type: 'button',
                icon: 'icon-export',
                title: "导出",
                action: function() {
                    var exportUrl = "/report/fileOpsReportExport?"
                    initExport("resourceTable","action",exportUrl);
                }
            },
            {
                type: 'searchbox',
                placeholder: "",
                action: function(val) {
                    searchInput = val;
                    gd.table('resourceTable').reload(1, {
                        searchStr: val
                    })
                    getEcharsData(startTime,endTime, val);
                }
            }
        ],
        tableConfig: {
            id: 'resourceTable', //给table一个id,调用gd.tableReload('demoTable');可重新加载表格数据并保持当前页码，gd.tableReload('demoTable'，1)，第二个参数可在加载数据时指定页码
            length: 50, //每页多少条,默认50，可选
            enableJumpPage: false, //启用跳页，默认false，可选
            enableLengthMenu: true, //启用可选择每页多少条，默认true，可选
            enablePaging: true, //启用分页,默认true，可选
            orderColumn: 'time', //排序列
            orderType: 'desc', //排序规则，desc或asc,默认desc
            fillBlank: '--',
            excludeSearchKey: ['dateTime'],
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/report/getFileOpsReportInPage',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    pageAllNum = data.total;
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.usergroupNames,
                            obj.userName,
                            obj.userFullName,
                            obj.localIp,
                            obj.fileName,
                            obj.optype,
                            obj.srcPath,
                            obj.dstPath,
                            obj.time
                        ];
                    });
                    return data;
                },
                //请求参数
                data: {
                    startTime: startTime,
                    endTime: endTime,
                    searchStr: ''
                }
            },
            columns: [{
                    name: 'usergroupNames',
                    head: '用户组',
                    title: true
                },
                {
                    name: 'userName',
                    head: '用户',
                    title: true,
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'userFullName',
                    head: '姓名',
                    title: true,
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'localIp',
                    head: '客户端IP',
                    title: true,
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'fileName',
                    head: '文件名',
                    title: true,
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'optype',
                    head: '操作类型',
                    title: true,
                    render: function(cell, row, raw) {
                        return returnOpeType(cell);
                    },
                    filterName: 'fileOpsType', //高级查询字段名，不写为name
                    filters: opeType
                },
                {
                    name: 'srcPath',
                    head: '源路径',
                    title: true,
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'dstPath',
                    head: '目标路径',
                    title: true,
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'time',
                    head: '时间',
                    orderable: true,
                    title: true,
                    render: function(cell, row, raw) {
                        return cell;
                    }
                }
            ]
        }
    },
    mounted: function() {
        $(window).resize(function() {
            resourceChart.resize();
        });
        initOption();
        getEcharsData(startTime,endTime, "");
    }
})


/**
 * 获取echars 数据
 * @param  {[type]} getTime [时间参数 默认显示最近7天]
 * @return {[type]}            [description]
 */
function getEcharsData(startTime,endTime, search) {
    resourceChart = echarts.init(document.getElementById('resource-pie'));
    var numTotal = 0,
        legendData = [],
        seriesData = [];
    $.get(baseUrl + '/report/getFileOpsReportChart', { startTime: startTime,endTime: endTime, searchStr: search }, function(msg) {
        if (msg.resultCode == 0) {
            if (msg.data.length > 0) {
                $.each(msg.data, function(index, val) {
                    numTotal += val.optNums;
                    legendData.push(val.optName);
                    seriesData.push({
                        name: val.optName,
                        value: val.optNums
                    })
                });
                $(".pie_number").html(numTotal);
            } else {
                $(".pie_number").html(0);
                legendData = ['-- ', '--  ', '--   ', '--    ', '--     ']
                seriesData = [
                    { value: 0, name: '-- ', percent: "0" },
                    { value: 0, name: '--  ', percent: "0" },
                    { value: 0, name: '--   ', percent: "0" },
                    { value: 0, name: '--    ', percent: "0" },
                    { value: 0, name: '--     ', percent: "0" }
                ]
            }
            resourceOption.series[0].data = seriesData;
            resourceOption.legend.data = legendData;
            resourceChart.setOption(resourceOption);
        }
    })
}
//获取前几天或后几天的日期 格式2016-11-15格式不能乱
function GetDateStr(AddDayCount) {
    var dd = new Date();
    dd.setDate(dd.getDate() + AddDayCount); //获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = (dd.getMonth() + 1) < 10 ? "0" + (dd.getMonth() + 1) : (dd.getMonth() + 1); //获取当前月份的日期，不足10补0
    var d = dd.getDate() < 10 ? "0" + dd.getDate() : dd.getDate(); //获取当前几号，不足10补0
    return y + "-" + m + "-" + d;
}

/**
 * 获取列表返回值得类型
 * @param  {[type]} cell [description]
 * @return {[type]}      [description]
 */
function returnOpeType(cell){
    var dataType = "";
    $.each(opeType,function(index,val){
        if(cell == val.value){
            dataType = val.label;
            return false
        }
    })
    return dataType;
}
function initOption() {
    resourceOption = {
        color: ["#6289D5", "#7FA8DF", "#97CBEF", "#D8F1FC", "#FFDB5C", "#ff9f7f", "#fb7293", "#E062AE", "#E690D1", "#e7bcf3", "#9d96f5", "#8378EA", "#96BFFF"],
        tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(255,255,255,0.7)',
            textStyle: {
                color: 'black'
            },
            formatter: function(params) {
                var total = $(".pie_number").html();
                if (total == 0) {
                    var percent = 0;
                } else {
                    var percent = Math.ceil((params.data.value / total) * 100);
                }
                return '<div style="padding: 16px 20px;color: black">' +
                    '<div style="margin-bottom: 16px;text-align: center">' +
                    percent + '%' +
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
            right: 50,
            top: 'center',
            pageIconColor:"#aaa",
            pageIconInactiveColor :"#2F4554",
            data: [],
            textStyle: {
                color: '#999'
            },
            formatter: function(data) {
                var legendName = "";
                $.each(resourceOption.series[0].data, function(index, val) {
                    if (val.name == data) {
                        legendName = data.length > 8 ? ('{a|' + data.substring(0, 8) + ' ... ' + '}' + '{b|' + val.value + '}') : ('{a|' + data + '}' + '{b|' + val.value + '}');
                    }
                });
                return data;
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
        series: [

            {
                name: '',
                type: 'pie',
                radius: ['60%', '85%'],
                data: [],
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                label: {
                    normal: {
                        // position: 'inner',
                        // formatter: '{d}%',
                        show: false,
                        textStyle: {
                            color: '#fff',
                            fontWeight: 'bold',
                            fontSize: 14
                        }
                    }
                },
            }
        ]
    };
}

