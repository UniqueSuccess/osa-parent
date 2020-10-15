var resourceChart = "";
var resourceOption = "";
var nowTime = new Date().getHours() + ":" + new Date().getMinutes();
var timePicker = GetDateStr(-7) + " " + nowTime + "~" + GetDateStr(0) + " " + nowTime;
var searchInput = "";
var timeInput = timePicker;
var pageAllNum = 0;
$(function() {
    $.ajax({
        url: baseUrl + '/asset/getAssetTypeTree',
        dataType: "json",
        success: function(data) {
            gd.tree('assetTypeTree').setData(data);
        }
    })
})
var app = new Vue({
    el: '#contentDiv',
    data: {
        assetTypeTreeConfig: {
            id: 'assetTypeTree',
            showCheckBox: true,
            data: [],
            onSelect: function(n) {},
            onChange: function(n) {
                var ids = n.map(function(node) {
                    return node.id
                });
                gd.table('resourceTable').reload(1, { assetType: ids.join(',') });
            }
        },
        recentRangePickerConfig: {
            value: ['最近7天'], //设置初始时间,可选
            dateRange: true, //日期范围选择
            timePicker: true, //展示时间选择器
            timeStep: 15, //时间最小单位
            change: function(time) {
                timeInput = time;
                getEcharsData(time,searchInput);
                gd.table('resourceTable').reload(1, {
                    dateTime: time
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
                    var exportUrl = "/report/assetReportExport?"
                    initExport("resourceTable","resource",exportUrl);
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
                    getEcharsData(timeInput,val);
                }
            }
        ],
        tableConfig: {
            id: 'resourceTable', //给table一个id,调用gd.tableReload('demoTable');可重新加载表格数据并保持当前页码，gd.tableReload('demoTable'，1)，第二个参数可在加载数据时指定页码
            length: 50, //每页多少条,默认50，可选
            enableJumpPage: false, //启用跳页，默认false，可选
            enableLengthMenu: true, //启用可选择每页多少条，默认true，可选
            enablePaging: true, //启用分页,默认true，可选
            orderColumn: 'ip', //排序列
            orderType: 'desc', //排序规则，desc或asc,默认desc
            fillBlank: '--',
            excludeSearchKey:['dateTime'],
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/report/getResourceReportList',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    pageAllNum = data.total;
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.typename,
                            obj.assetname,
                            obj.ip,
                            obj.account,
                            obj.username,
                            obj.truename,
                            obj.groupname
                        ];
                    });
                    return data;
                },
                //请求参数
                data: {
                    dateTime: timePicker,
                    searchStr:''
                }
            },
            columns: [{
                    name: 'typename',
                    head: '目标设备类型',
                    filters: '#assetTypeTreeBox',
                    class: 'assettypeName',
                    title: true
                },
                {
                    name: 'assetname',
                    head: '目标设备名称',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'ip',
                    head: '目标设备IP',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'account',
                    head: '目标设备账号',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'username',
                    head: '用户名',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'truename',
                    head: '姓名',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'groupname',
                    head: '用户组',
                    title: function(cell, row, raw) {
                        return cell;
                    },
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
        getEcharsData(timePicker,"");
    }
})

/**
 * 获取echars 数据
 * @param  {[type]} getTime [时间参数 默认显示最近7天]
 * @return {[type]}            [description]
 */
function getEcharsData(getTime,search) {
    resourceChart = echarts.init(document.getElementById('resource-pie'));
    var numTotal = 0,
        legendData = [],
        seriesData = [];
    $.get(baseUrl + '/report/getResourceReportChart', { dateTime: getTime, searchStr:search}, function(msg) {
        if (msg.resultCode == 0) {
            if (msg.data.length > 0) {
                $.each(msg.data, function(index, val) {
                    numTotal += val.num;
                    legendData.push(val.typeName);
                    seriesData.push({
                        name: val.typeName,
                        value: val.num
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
                if(total == 0){
                    var percent = 0;
                }else{
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
            pageIconColor:"#aaa",
            pageIconInactiveColor :"#2F4554",
            orient: 'vertical',
            icon: "circle",
            itemWidth: 10,
            itemHeight: 10,
            orient: 'vertical',
            right: 50,
            top: 'center',
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