var commandChart = "";
var commandOption = "";
var nowTime = new Date().getHours() + ":" + new Date().getMinutes();
var timePicker = GetDateStr(-7) + " " + nowTime + "~" + GetDateStr(0) + " " + nowTime;
var searchInput = "";
var timeInput = timePicker;
var pageAllNum = 0;
var app = new Vue({
    el: '#contentDiv',
    data: {
        recentRangePickerConfig: {
            value: ['最近7天'], //设置初始时间,可选
            dateRange: true, //日期范围选择
            timePicker: true, //展示时间选择器
            timeStep: 15, //时间最小单位
            change: function(time) {
                timeInput = time;
                getEcharsData(time.replace(" ~ ", "~"), searchInput);
                gd.table('commandTable').reload(1, {
                    dateTime: time.replace(" ~ ", "~")
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
                    var exportUrl = "/report/commandReportExport?"
                    initExport("commandTable","command",exportUrl);
                }
            },
            {
                type: 'searchbox',
                placeholder: "",
                action: function(val) {
                    searchInput = val;
                    gd.table('commandTable').reload(1, {
                        searchStr: val
                    })
                    getEcharsData(timeInput, val);
                }
            }
        ],
        tableConfig: {
            id: 'commandTable', //给table一个id,调用gd.tableReload('demoTable');可重新加载表格数据并保持当前页码，gd.tableReload('demoTable'，1)，第二个参数可在加载数据时指定页码
            length: 50, //每页多少条,默认50，可选
            enableJumpPage: false, //启用跳页，默认false，可选
            enableLengthMenu: true, //启用可选择每页多少条，默认true，可选
            enablePaging: true, //启用分页,默认true，可选
            orderColumn: '', //排序列
            orderType: '', //排序规则，desc或asc,默认desc
            fillBlank: '--',
            excludeSearchKey:['dateTime'],
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/report/getCommandReportList',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    pageAllNum = data.total;
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.status,
                            obj.input,
                            obj.typename,
                            obj.asset,
                            obj.ip,
                            obj.account,
                            obj.username,
                            obj.truename,
                            obj.groupname,
                            obj.timestamp
                        ];
                    });
                    return data;
                },
                //请求参数
                data: {
                    dateTime: timePicker,
                    searchStr: ''
                }
            },
            columns: [{
                    name: 'status',
                    head: '违规类型',
                    width: 100,
                    filters: [ //设置检索条件
                        /*{
                            label: '审批',
                            value: '0,1,-1,2'
                        }, */
                        {
                            label: '禁止',
                            value: '3'
                        }, {
                            label: '阻断',
                            value: '4'
                        }
                    ],
                    title: function(cell, row, raw) {
                        return $(cell).html();
                    },
                    render: function(cell, row, raw) {
                        if (cell == 3) {
                            return "<span class='com-type com-prohibit'>禁止</span>";
                        } else if (cell == 4) {
                            return "<span class='com-type com-bock'>阻断</span>";
                        }
                        /*else {
                            return "<span class='com-type com-approval'>审批</span>";
                        }*/
                    },
                }, {
                    name: 'input',
                    head: '命令',
                    title: true
                },
                {
                    name: 'typename',
                    head: '目标资源类型',
                    class: 'assettypeName',
                    title: true
                },
                {
                    name: 'asset',
                    head: '目标资源名称',
                    title: true
                },
                {
                    name: 'ip',
                    head: '目标资源IP',
                    title: true
                },
                {
                    name: 'account',
                    head: '目标资源账号',
                    title: true
                },
                {
                    name: 'username',
                    head: '用户名',
                    title: true
                },
                {
                    name: 'truename',
                    head: '姓名',
                    title: true
                },
                {
                    name: 'groupname',
                    head: '用户组',
                    title: true
                },
                {
                    name: 'timestamp',
                    head: '时间',
                    orderable: true,
                    title: true
                }
            ]
        }
    },
    mounted: function() {
        $(window).resize(function() {
            commandChart.resize();
        });
        initOption();
        getEcharsData(timePicker, "");
    }
})

/**
 * 获取数据
 * @param  {[type]} getTime [description]
 * @return {[type]}         [description]
 */
function getEcharsData(getTime, search) {
    commandChart = echarts.init(document.getElementById('command-line'));
    var xAxisData = [],
        legendData = [],
        seriesData = [];
        var approve = [],forbid = [],block = [];
    $.get(baseUrl + '/report/getCommandReportChart', { dateTime: getTime, searchStr: search }, function(msg) {
        if (msg.resultCode == 0) {
            var legendData = ['禁止', '阻断'];
            if (msg.data.list.length > 0) {
                $.each(msg.data.list, function(index, val) {
                    if (msg.data.type == "day") {
                        xAxisData.push(val.key.substring(0, val.key.indexOf(" ")));
                    } else {
                        xAxisData.push(val.key.substring(val.key.indexOf(" ") + 1, val.key.indexOf("~")));
                    }
                    approve.push(val.approve);
                    forbid.push(val.forbid);
                    block.push(val.block);
                });
                commandOption.series[0].data = forbid;
                commandOption.series[1].data = block;
                commandOption.color = ["#6785CF", "#F5A623"];
            } else {
                commandOption.color = ["#2B3743", "#2B3743"];
                xAxisData = ['--', '--', '--', '--', '--', '--', '--'];
                commandOption.series[0].data = [100, 100, 100, 100, 100, 100, 100];
                commandOption.series[1].data = [100, 100, 100, 100, 100, 100, 100];
            }
            commandOption.xAxis[0].data = xAxisData;
            commandChart.setOption(commandOption);
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

function initOption() {
    commandOption = {
        color: ["#6785CF", "#F5A623"],
        tooltip: {
            trigger: 'axis',
            axisPointer: { // 坐标轴指示器，坐标轴触发有效
                type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
            },
            backgroundColor: 'rgba(255,255,255,0.7)',
            textStyle: {
                color: 'black'
            },
            formatter: function(params) {
                var name = params[0].name;
                var innerHtml = "";
                $.each(params, function(index, val) {
                    if (name == "--") {
                        innerHtml += "<div><span></span><span>" + val.seriesName + "</span><span> 0 </span></div>";
                    } else {
                        innerHtml += "<div><span></span><span>" + val.seriesName + "</span><span>" + val.value + "</span></div>";
                    }
                })
                return "<div class='format-box'><p>" + name + "</p>" + innerHtml + "</div>";
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '10',
            containLabel: true
        },
        xAxis: [{
            type: 'category',
            data: [],
            // x轴的字体样式
            axisLabel: {
                show: true,
                textStyle: {
                    color: '#999'
                }
            },
            // 控制网格线是否显示
            splitLine: {
                show: false,
                //  改变轴线颜色
                lineStyle: {
                    // 使用深浅的间隔色
                    color: ['red']
                }
            },
            // x轴的颜色和宽度
            axisLine: {
                show: false,
                lineStyle: {
                    color: '#273340'
                }
            }
        }],
        yAxis: [{
            type: 'value',
            name: '总价(万元)',
            axisLabel: {
                formatter: '{value}'
            },
            axisLabel: {
                show: true,
                textStyle: {
                    color: '#999'
                }
            },
            // 控制网格线是否显示
            splitLine: {
                show: false,
                //  改变轴线颜色
                lineStyle: {
                    // 使用深浅的间隔色
                    color: ['red']
                }
            },
            // x轴的颜色和宽度
            axisLine: {
                show: false,
                lineStyle: {
                    color: '#273340'
                }
            }
        }],
        series: [{
            name: '禁止',
            type: 'bar',
            data: [0, 0, 0, 0, 0, 0, 0],
            barMaxWidth: 66,
            // barGaps: 20,
            // barCategoryGap: 4,
            itemStyle: {
                normal: {
                    barBorderRadius: [4, 4, 0, 0],
                }
            }
        }, {
            name: '阻断',
            type: 'bar',
            data: [],
            barMaxWidth: 66,
            // barGaps: 20,
            // barCategoryGap: 4,
            itemStyle: {
                normal: {
                    barBorderRadius: [4, 4, 0, 0],
                }
            }
        }]
    };
}