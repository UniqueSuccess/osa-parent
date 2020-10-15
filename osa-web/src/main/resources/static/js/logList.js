
var newDate = new Date();
console.log(newDate);
var startDate = newDate.getFullYear()+"-"+(newDate.getMonth()+1)+"-"+newDate.getDate()+" "+newDate.getHours()+':'+newDate.getMinutes();
var date2 = new Date();
date2.setDate(newDate.getDate()-7);
var endDate = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate()+" "+newDate.getHours()+':'+newDate.getMinutes();

var deteDefault = [endDate,startDate,];
var searchStrVal = '';

var logList = new Vue({
    el: '#contentDiv',
    data: {
        recentPickerConfig: {
            value: deteDefault,
            maxDate: '2028-06-03',//最大日期
            minDate: '2018-06-08',//最小日期
            timeValue: ['00:00', '23:59'],
            timeStep: 15,//时间最小单位
            dateRange: true,//日期范围选择
            timePicker: true,//展示时间选择器
            change: function (time) {
                log(time);//时间改变时触发
                var timeArr = time.split(' ~ ');
                deteDefault = [timeArr[0],timeArr[1]];
                searchStrVal = $(".gd-iconinput input").val();
                gd.table('operateTable').reload(false,{searchStr:searchStrVal,startTime:deteDefault[0],endTime:deteDefault[1]},false)
                gd.table('authTable').reload(false,{searchStr:searchStrVal,startTime:deteDefault[0],endTime:deteDefault[1]},false)
                gd.table('approvalTable').reload(false,{searchStr:searchStrVal,startTime:deteDefault[0],endTime:deteDefault[1]},false)
                gd.table('strategyTable').reload(false,{searchStr:searchStrVal,startTime:deteDefault[0],endTime:deteDefault[1]},false)
                gd.table('operationTable').reload(false,{searchStr:searchStrVal,startTime:deteDefault[0],endTime:deteDefault[1]},false)
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
                label: '最近7天',
                checked:true
            }, {
                value: 15 * 24,
                label: '最近15天'
            }, {
                value: 30 * 24,
                label: '最近30天'
            }]
        },

        toolbarConfig: [
            {
                type: 'searchbox',
                placeholder: "",
                action: function(val) {
                    searchStrVal = val;
                    gd.table('operateTable').reload(false,{searchStr:searchStrVal,startTime:deteDefault[0],endTime:deteDefault[1]},false)
                    gd.table('authTable').reload(false,{searchStr:searchStrVal,startTime:deteDefault[0],endTime:deteDefault[1]},false)
                    gd.table('approvalTable').reload(false,{searchStr:searchStrVal,startTime:deteDefault[0],endTime:deteDefault[1]},false)
                    gd.table('strategyTable').reload(false,{searchStr:searchStrVal,startTime:deteDefault[0],endTime:deteDefault[1]},false)
                    gd.table('operationTable').reload(false,{searchStr:searchStrVal,startTime:deteDefault[0],endTime:deteDefault[1]},false)
                }
            }
        ],

        operateTableConfig: {
            id: 'operateTable',
            length: 50, //每页多少条,默认50，可选
            enableJumpPage: false, //启用跳页，默认false，可选
            enableLengthMenu: true, //启用可选择每页多少条，默认true，可选
            enablePaging: true, //启用分页,默认true，可选
            orderColumn: 'loginTimes', //排序列
            orderType: 'desc',
            filterImmediately: true,
            fillBlank: '--',
            excludeSearchKey:['startTime','endTime'],
            ajax: {
                url: baseUrl + '/logSystem/getLogSystemsInPage?logBigType=1',
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.logType,
                            obj.userUsername,
                            obj.ip,
                            obj.logOperateParam,
                            obj.time
                        ];
                    });
                    return data;
                },
                data: {
                    startTime:deteDefault[0],
                    endTime:deteDefault[1],
                    searchStr: $.trim(searchStrVal),
                }
            },
            columns: [
                {
                    name: 'logType',
                    head: '类型',
                    width: '180',
                    filterName: 'logSmallType',
                    title: function(cell, row, raw) {
                    },
                    render: function(cell, row, raw) {
                        var html='';
                        switch (cell){
                            case 1:
                                html ="<span class='bar-status-mark status-add'>新建</span>";
                                break;
                            case 2:
                                html ="<span class='bar-status-mark status-delete'>删除</span>";
                              break;
                            case 3:
                                html ="<span class='bar-status-mark status-edit'>编辑</span>";
                              break;
                            case 4:
                                html ="<span class='bar-status-mark status-detail'>查看</span>";
                              break;
                            case 5:
                                html ="<span class='bar-status-mark status-login'>登录</span>";
                              break;
                            case 6:
                                html ="<span class='bar-status-mark status-logout'>退出</span>";
                                break;
                            case 7:
                                html ="<span class='bar-status-mark status-replay'>播放</span>";
                                break;
                            case 8:
                                html ="<span class='bar-status-mark status-stop'>阻断</span>";
                                break;
                            case 9:
                                html ="<span class='bar-status-mark status-replay'>回放</span>";
                                break;
                        }
                        return html
                    },
                    filters: [
                        {
                            label: '新建',
                            value: '1',
                        }, {
                            label: '删除',
                            value: '2'
                        },
                        {
                            label: '修改',
                            value: '3',
                        }, {
                            label: '查看',
                            value: '4'
                        },
                        {
                            label: '登录',
                            value: '5',
                        }, {
                            label: '登出',
                            value: '6'
                        }, {
                            label: '播放',
                            value: '7'
                        }, {
                            label: '阻断',
                            value: '8'
                        }, {
                            label: '回放',
                            value: '9'
                        }
                    ]
                },
                {
                    name: 'assetName',
                    head: '操作员',
                    width:'150',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'assetIp',
                    head: 'IP地址',
                    width:'250',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'accountName',
                    head: '详情',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'operate',
                    head: '时间',
                    align: 'center',
                    width: 200,
                }
            ]
        },

        authTableConfig: {
            id: 'authTable',
            length: 50, //每页多少条,默认50，可选
            enableJumpPage: false, //启用跳页，默认false，可选
            enableLengthMenu: true, //启用可选择每页多少条，默认true，可选
            enablePaging: true, //启用分页,默认true，可选
            orderColumn: 'loginTimes', //排序列
            orderType: 'desc',
            filterImmediately: true,
            fillBlank: '--',
            excludeSearchKey:['startTime','endTime'],
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/logSystem/getLogSystemsInPage?logBigType=2',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.logType,
                            obj.userUsername,
                            obj.ip,
                            obj.logOperateParam,
                            obj.time
                        ];
                    });
                    return data;
                },
                //请求参数
                data: {
                    startTime:deteDefault[0],
                    endTime:deteDefault[1],
                    searchStr: $.trim(searchStrVal),
                }
            },
            columns: [
                {
                    name: 'logType',
                    head: '类型',
                    width: '180',
                    filterName: 'logSmallType',
                    class: 'logType',
                    render: function(cell, row, raw) {
                        var html='';
                        switch (cell){
                            case 10:
                                html ="<span class='bar-status-mark status-detail'>新建</span>";
                                break;
                            case 11:
                                html ="<span class='bar-status-mark status-add'>删除</span>";
                                break;
                            case 18:
                                html ="<span class='bar-status-mark status-edit'>撤销</span>";
                                break;
                        }
                        return html
                    },
                    filters: [
                        {
                            label: '新建',
                            value: '10',
                        }, {
                            label: '删除',
                            value: '11'
                        }, {
                            label: '撤销',
                            value: '18'
                        }
                    ]
                },
                {
                    name: 'assetName',
                    head: '操作员',
                    width:200,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'assetIp',
                    head: 'IP地址',
                    width:180,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'accountName',
                    head: '详情',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'operate',
                    head: '时间',
                    align: 'center',
                    width: 180,
                }
            ]
        },

        approvalTableConfig: {
            id: 'approvalTable',
            length: 50, //每页多少条,默认50，可选
            enableJumpPage: false, //启用跳页，默认false，可选
            enableLengthMenu: true,
            enablePaging: true, //启用分页,默认true，可选
            orderColumn: 'loginTimes', //排序列
            orderType: 'desc',
            filterImmediately: true,
            fillBlank: '--',
            excludeSearchKey:['startTime','endTime'],
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/logSystem/getLogSystemsInPage?logBigType=3',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.logType,
                            obj.userUsername,
                            obj.ip,
                            obj.logOperateParam,
                            obj.time
                        ];
                    });
                    return data;
                },
                //请求参数
                data: {
                    startTime:deteDefault[0],
                    endTime:deteDefault[1],
                    searchStr: $.trim(searchStrVal),
                }
            },
            columns: [
                {
                    name: 'logType',
                    head: '类型',
                    width: '180',
                    filterName: 'logSmallType',
                    class: 'logType',
                    render: function(cell, row, raw) {
                        var html='';
                        switch (cell){
                            case 12:
                                html ="<span class='bar-status-mark status-detail'>通过</span>";
                                break;
                            case 13:
                                html ="<span class='bar-status-mark status-edit'>拒绝</span>";
                                break;
                        }
                        return html
                    },
                    filters: [
                        {
                            label: '通过',
                            value: '12',
                        }, {
                            label: '拒绝',
                            value: '13'
                        }
                    ]
                },
                {
                    name: 'assetName',
                    head: '操作员',
                    width:200,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'assetIp',
                    head: 'IP地址',
                    width:180,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'accountName',
                    head: '详情',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'operate',
                    head: '时间',
                    align: 'center',
                    width: 180,
                }
            ]
        },

        strategyTableConfig:{
            id: 'strategyTable',
            length: 50, //每页多少条,默认50，可选
            enableJumpPage: false, //启用跳页，默认false，可选
            enableLengthMenu: true,
            enablePaging: true, //启用分页,默认true，可选
            orderColumn: 'loginTimes', //排序列
            orderType: 'desc',
            filterImmediately: true,
            fillBlank: '--',
            excludeSearchKey:['startTime','endTime'],
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/logSystem/getLogSystemsInPage?logBigType=4',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.logType,
                            obj.userUsername,
                            obj.ip,
                            obj.logOperateParam,
                            obj.time
                        ];
                    });
                    return data;
                },
                //请求参数
                data: {
                    startTime:deteDefault[0],
                    endTime:deteDefault[1],
                    searchStr: $.trim(searchStrVal),
                }
            },
            columns: [
                {
                    name: 'logType',
                    head: '类型',
                    width: '180',
                    filterName: 'logSmallType',
                    class: 'logType',
                    render: function(cell, row, raw) {
                        var html='';
                        switch (cell){
                            case 14:
                                html ="<span class='bar-status-mark status-replay'>命令</span>";
                                break;
                            case 15:
                                html ="<span class='bar-status-mark status-detail'>登录</span>";
                                break;
                        }
                        return html
                    },
                    filters: [
                        {
                            label: '命令',
                            value: '14',
                        }, {
                            label: '登录',
                            value: '15'
                        }
                    ]
                },
                {
                    name: 'assetName',
                    head: '用户名',
                    width:120,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'assetIp',
                    head: 'IP地址',
                    width:200,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'accountName',
                    head: '详情',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'operate',
                    head: '时间',
                    align: 'center',
                    width: 180,
                }
            ]
        },

        operationTableConfig:{
            id: 'operationTable',
            length: 50, //每页多少条,默认50，可选
            enableJumpPage: false, //启用跳页，默认false，可选
            enableLengthMenu: true,
            enablePaging: true, //启用分页,默认true，可选
            fillBlank: '--',
            excludeSearchKey:['startTime','endTime'],
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/logSystem/getLogSystemsInPage?logBigType=5',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.userUsername,
                            obj.ip,
                            obj.logOperateParam,
                            obj.time
                        ];
                    });
                    return data;
                },
                //请求参数
                data: {
                    startTime:deteDefault[0],
                    endTime:deteDefault[1],
                    searchStr: $.trim(searchStrVal),
                }
            },
            columns: [
                {
                    name: 'assetName',
                    head: '用户名',
                    width:200,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'assetIp',
                    head: 'IP地址',
                    width:200,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'accountName',
                    head: '详情',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'operate',
                    head: '时间',
                    align: 'center',
                    width: 180,
                }
            ]
        },

    }
});