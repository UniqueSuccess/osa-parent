var dateArr = [];

// 权限
var permissionSetStrategy = [];
var permissionSetStrategyTable = [];
// 新建
if (permissionSetArr.indexOf('strategy::add::strategy') > -1) {
    var addStrategyBox = null;
    permissionSetStrategy.push({
        type: 'button',
        icon: 'icon-add',
        title: '添加',
        action: function() {
            gd.showLayer({
                id: 'addstrategy_layer',
                title: '新建策略',
                content: $('#addStrategyBox').html(),
                size: [990, 600],
                btn: [
                    {
                        text: '确定',
                        action: function(dom) {
                            var validate = gd.validate('#addStrategyForm', {});
                            var result = validate.valid();
                            if (result) {
                                dateArr.map(function(item, index) {
                                    if (item.endTime == '24:00') {
                                        item.endTime = '23:59';
                                    }
                                });

                                var addObject = $('#addStrategyForm').serializeJSON();

                                addObject.strategyCommandBlock = addObject.strategyCommandBlock.split('\r\n');
                                // addObject.strategyCommandPending = addObject.strategyCommandPending.split("\r\n");
                                addObject.strategyCommandProhibit = addObject.strategyCommandProhibit.split('\r\n');
                                // var loginTimeString = '';
                                // $(".logintimeList li").each(function(){
                                //     var className = $(this).attr('class');
                                //     if(className == 'dateActive'){
                                //         loginTimeString += '1';
                                //     }else{
                                //         loginTimeString += '0';
                                //     }
                                // })
                                // addObject.strategyLoginTime = loginTimeString;
                                addObject.strategyLoginTime = dateArr;

                                // if(addObject.rdp instanceof Array){
                                //     addObject.rdp = addObject.rdp.join(',');
                                // }
                                // if(addObject.sessionType instanceof Array){
                                //     addObject.sessionType = addObject.sessionType.join(',');
                                // }
                                // if(addObject.ssh instanceof Array){
                                //     addObject.ssh = addObject.ssh.join(',');
                                // }
                                $.extend(true, addObject, getWaterMarkData(addStrategyBox));
                                delete addObject.mode;
                                var sumLocation = 0;
                                addStrategyBox.waterMarkPositionArr.forEach(function(item) {
                                    sumLocation += Number(item);
                                });
                                if (addStrategyBox.waterMarkPosition == 1 && sumLocation == 0) {
                                    gd.showWarning('请至少选择1个水印位置！');
                                    return false;
                                }
                                $.ajax({
                                    type: 'post',
                                    url: baseUrl + '/strategy/strategy',
                                    data: JSON.stringify(addObject),
                                    contentType: 'application/json',
                                    success: function(res) {
                                        if (!res.resultCode) {
                                            gd.showSuccess('创建策略成功！');
                                            gd.table('strategylistTable').reload();
                                        } else {
                                            gd.showError(res.resultMsg);
                                        }
                                    },
                                    error: function(xhr, errorText, errorStatus) {}
                                });
                            } else {
                                return false;
                            }
                        }
                    },
                    {
                        text: '取消',
                        action: function() {}
                    }
                ],
                success: function(dom) {
                    dateArr = [
                        { type: 1, startTime: '00:00', endTime: '23:59' },
                        { type: 2, startTime: '00:00', endTime: '23:59' },
                        { type: 3, startTime: '00:00', endTime: '23:59' },
                        { type: 4, startTime: '00:00', endTime: '23:59' },
                        { type: 5, startTime: '00:00', endTime: '23:59' },
                        { type: 6, startTime: '00:00', endTime: '23:59' },
                        { type: 7, startTime: '00:00', endTime: '23:59' }
                    ];
                    addStrategyBox = new Vue({
                        el: '#addStrategy',
                        methods: methodsAll,
                        data: {
                            name: '',
                            commandBlock: '',
                            commandPending: '',
                            commandProhibit: '',
                            allDateList: [],
                            screenWatermark: {
                                content: {
                                    computername: 0,
                                    depname: 0,
                                    macaddress: 0,
                                    ip: 0,
                                    time: 0,
                                    manualtext: '',
                                    mode: 0,
                                    username: 0,
                                    manual: 0,
                                    direction: 0,
                                    localtion: 0,
                                    locaktiontemp: [],
                                    color: '',
                                    tcolor: '',
                                    opacity: 255,
                                    fontsize: 14,
                                    isshow: 0
                                },
                                enable: 0
                            },
                            fileMon: { enable: 0, content: { resourceManager: 0, fileExt: '' } },
                            fileTypeList: [],
                            waterMarkColor: 'ff0000',
                            waterMarkPosition: 0,
                            waterMarkPositionArr: [],
                            fileTypeTreeConfig: {
                                id: 'fileTypeTree',
                                showCheckBox: true,
                                disabled: true,
                                data: [],
                                onSelect: function(node) {
                                    // 点击树节点时触发 返回点击选中的节点数据
                                },
                                onChange: function(nodes) {
                                    // 点击复选框时触发  返回所有选中复选框的数据
                                    addStrategyBox.fileTypeList = nodes.filter(function(n) {
                                        return n.pId !== null;
                                    });
                                },
                                ready: function(data) {}
                            },
                            timeRangeRulerConfig1: {
                                step: 15, //设置间隔分钟数，可选，默认是30分钟
                                value: ['00:00', '23:59'], //设置初始时间,可选
                                range: true, //时间范围选择
                                change: function(time) {
                                    dateArr[0] = {
                                        type: 1,
                                        startTime: time[0],
                                        endTime: time[1]
                                    };
                                }
                            },
                            timeRangeRulerConfig2: {
                                step: 15, //设置间隔分钟数，可选，默认是30分钟
                                value: ['00:00', '23:59'], //设置初始时间,可选
                                range: true, //时间范围选择
                                change: function(time) {
                                    dateArr[1] = {
                                        type: 2,
                                        startTime: time[0],
                                        endTime: time[1]
                                    };
                                }
                            },
                            timeRangeRulerConfig3: {
                                step: 15, //设置间隔分钟数，可选，默认是30分钟
                                value: ['00:00', '23:59'], //设置初始时间,可选
                                range: true, //时间范围选择
                                change: function(time) {
                                    dateArr[2] = {
                                        type: 3,
                                        startTime: time[0],
                                        endTime: time[1]
                                    };
                                }
                            },
                            timeRangeRulerConfig4: {
                                step: 15, //设置间隔分钟数，可选，默认是30分钟
                                value: ['00:00', '23:59'], //设置初始时间,可选
                                range: true, //时间范围选择
                                change: function(time) {
                                    dateArr[3] = {
                                        type: 4,
                                        startTime: time[0],
                                        endTime: time[1]
                                    };
                                }
                            },
                            timeRangeRulerConfig5: {
                                step: 15, //设置间隔分钟数，可选，默认是30分钟
                                value: ['00:00', '23:59'], //设置初始时间,可选
                                range: true, //时间范围选择
                                change: function(time) {
                                    dateArr[4] = {
                                        type: 5,
                                        startTime: time[0],
                                        endTime: time[1]
                                    };
                                }
                            },
                            timeRangeRulerConfig6: {
                                step: 15, //设置间隔分钟数，可选，默认是30分钟
                                value: ['00:00', '23:59'], //设置初始时间,可选
                                range: true, //时间范围选择
                                change: function(time) {
                                    dateArr[5] = {
                                        type: 6,
                                        startTime: time[0],
                                        endTime: time[1]
                                    };
                                }
                            },
                            timeRangeRulerConfig7: {
                                step: 15, //设置间隔分钟数，可选，默认是30分钟
                                value: ['00:00', '23:59'], //设置初始时间,可选
                                range: true, //时间范围选择
                                change: function(time) {
                                    dateArr[6] = {
                                        type: 7,
                                        startTime: time[0],
                                        endTime: time[1]
                                    };
                                }
                            }
                            // sessionTypeList:[],
                            // rdpList:[],
                            // sshList:[],
                        },
                        watch: {
                            'fileMon.enable': function(val) {
                                this.fileTypeTreeConfig.disabled = !val;
                            }
                        },
                        mounted: function() {
                            $('#watermarkPickColor').colpick({
                                submit: true,
                                onSubmit: function(color, color2) {
                                    addStrategyBox.waterMarkColor = color2;
                                    $('.colpick.colpick_full').hide();
                                    addStrategyBox.$refs.waterMarkColorSelect.isDroped = false;
                                }
                            });
                            gd.get(baseUrl + '/strategy/getFileTypeTree', function(msg) {
                                var data = msg.map(function(node) {
                                    node.pId = node.pid;
                                    delete node.pid;
                                    node.expand = false;
                                    // node.disabled = true;
                                    return node;
                                });
                                gd.tree('fileTypeTree').setData(data);
                            });
                            // gd.get(baseUrl + '/strategy/getStrategySessionType',{},function(res){
                            //     addStrategyBox.sessionTypeList = res.data
                            // })
                            // gd.get( baseUrl + '/strategy/getStrategyRDP',{},function(res){
                            //     addStrategyBox.rdpList = res.data
                            // })
                            // gd.get( baseUrl + '/strategy/getStrategySSH',{},function(res){
                            //     addStrategyBox.sshList = res.data
                            // })
                        }
                    });
                    // for(var i=0; i<24*7; i++){
                    //     var obj = {
                    //         'value':i,
                    //         'class':'dateActive'
                    //     }
                    //     addStrategyBox.allDateList.push(obj)
                    // }
                    $('.timerulerUl li').click(function(e) {
                        var index = $(this).index();
                        $(this)
                            .siblings()
                            .removeClass('active');
                        $(this).addClass('active');
                        $('.timeRulerConBox').css('margin-left', -610 * index);
                        // $('.timeRulerConBox .timerulerCon').hide();
                        // $('.timeRulerConBox .timerulerCon').eq(index).show();
                        // addStrategyBox.timeRangeRulerConfig2.value =  ['00:00', '23:59'];
                        // addStrategyBox.timeRangeRulerConfig6.value =  ['00:00', '23:59'];
                    });
                },
                end: function(dom) {}
            });
        }
    });
}

// 编辑
if (permissionSetArr.indexOf('strategy::update::strategy') > -1) {
    var addStrategyBox = null;
    permissionSetStrategyTable.push({
        icon: 'icon-edit',
        title: '编辑',
        action: function(cell, row, raw) {
            gd.showLayer({
                id: 'addstrategy_layer',
                title: '编辑策略',
                content: $('#addStrategyBox').html(),
                size: [990, 600],
                btn: [
                    {
                        text: '确定',
                        action: function(dom) {
                            var validate = gd.validate('#addStrategyForm', {});
                            var result = validate.valid();
                            if (result) {
                                dateArr.map(function(item, index) {
                                    if (item.endTime == '24:00') {
                                        item.endTime = '23:59';
                                    }
                                });
                                var addObject = $('#addStrategyForm').serializeJSON();
                                addObject.strategyCommandBlock = addObject.strategyCommandBlock.split('\r\n');
                                // addObject.strategyCommandPending = addObject.strategyCommandPending.split("\r\n");
                                addObject.strategyCommandProhibit = addObject.strategyCommandProhibit.split('\r\n');
                                addObject.guid = cell;
                                // var loginTimeString = '';
                                // $(".logintimeList li").each(function(){
                                //     var className = $(this).attr('class');
                                //     if(className == 'dateActive'){
                                //         loginTimeString += '1';
                                //     }else{
                                //         loginTimeString += '0';
                                //     }
                                // })
                                // addObject.strategyLoginTime = loginTimeString;

                                addObject.strategyLoginTime = dateArr;

                                // if(addObject.rdp instanceof Array){
                                //     addObject.rdp = addObject.rdp.join(',');
                                // }
                                // if(addObject.sessionType instanceof Array){
                                //     addObject.sessionType = addObject.sessionType.join(',');
                                // }
                                // if(addObject.ssh instanceof Array){
                                //     addObject.ssh = addObject.ssh.join(',');
                                // }
                                $.extend(true, addObject, getWaterMarkData(addStrategyBox));
                                delete addObject.mode;
                                var sumLocation = 0;
                                addStrategyBox.waterMarkPositionArr.forEach(function(item) {
                                    sumLocation += Number(item);
                                });
                                if (addStrategyBox.waterMarkPosition == 1 && sumLocation == 0) {
                                    gd.showWarning('请至少选择1个水印位置！');
                                    return false;
                                }
                                $.ajax({
                                    type: 'PUT',
                                    url: baseUrl + '/strategy/strategy',
                                    data: JSON.stringify(addObject),
                                    contentType: 'application/json',
                                    success: function(res) {
                                        if (!res.resultCode) {
                                            gd.showSuccess('编辑策略成功！');
                                            gd.table('strategylistTable').reload();
                                        } else {
                                            gd.showError(res.resultMsg);
                                        }
                                    },
                                    error: function(xhr, errorText, errorStatus) {}
                                });
                            } else {
                                return false;
                            }
                        }
                    },
                    {
                        text: '取消',
                        action: function() {}
                    }
                ],
                success: function(dom) {
                    gd.get(baseUrl + '/strategy/strategy/' + cell, {}, function(res) {
                        if (res.resultCode == 0) {
                            var raw = res.data;

                            dateArr = raw.strategyLoginTime;
                            var waterMarkData = parseWaterMarkData(raw);

                            addStrategyBox = new Vue({
                                el: '#addStrategy',
                                methods: methodsAll,
                                data: {
                                    name: raw.name,
                                    commandBlock: raw.strategyCommandBlock ? raw.strategyCommandBlock.join('\r\n') : '',
                                    // commandPending:raw.strategyCommandPending?raw.strategyCommandPending.join("\r\n"):'',
                                    commandProhibit: raw.strategyCommandProhibit
                                        ? raw.strategyCommandProhibit.join('\r\n')
                                        : '',
                                    allDateList: [],
                                    screenWatermark: waterMarkData.screenWatermark,
                                    fileMon: waterMarkData.fileMon,
                                    fileTypeList: waterMarkData.fileTypeList,
                                    waterMarkColor: waterMarkData.waterMarkColor,
                                    waterMarkPosition: waterMarkData.waterMarkPosition,
                                    waterMarkPositionArr: waterMarkData.waterMarkPositionArr,
                                    fileTypeTreeConfig: {
                                        id: 'fileTypeTree',
                                        showCheckBox: true,
                                        data: waterMarkData.fileTypeTreeData,
                                        disabled: !waterMarkData.fileMon.enable,
                                        onSelect: function(node) {
                                            // 点击树节点时触发 返回点击选中的节点数据
                                        },
                                        onChange: function(nodes) {
                                            // 点击复选框时触发  返回所有选中复选框的数据
                                            addStrategyBox.fileTypeList = nodes.filter(function(n) {
                                                return n.pId !== null;
                                            });
                                        },
                                        ready: function(data) {}
                                    },

                                    timeRangeRulerConfig1: {
                                        step: 15,
                                        value: [dateArr[0].startTime, dateArr[0].endTime],
                                        range: true,
                                        change: function(time) {
                                            dateArr[0] = {
                                                type: 1,
                                                startTime: time[0],
                                                endTime: time[1]
                                            };
                                        }
                                    },
                                    timeRangeRulerConfig2: {
                                        step: 15, //设置间隔分钟数，可选，默认是30分钟
                                        value: [dateArr[1].startTime, dateArr[1].endTime],
                                        range: true, //时间范围选择
                                        change: function(time) {
                                            dateArr[1] = {
                                                type: 2,
                                                startTime: time[0],
                                                endTime: time[1]
                                            };
                                        }
                                    },
                                    timeRangeRulerConfig3: {
                                        step: 15, //设置间隔分钟数，可选，默认是30分钟
                                        value: [dateArr[2].startTime, dateArr[2].endTime],
                                        range: true, //时间范围选择
                                        change: function(time) {
                                            dateArr[2] = {
                                                type: 3,
                                                startTime: time[0],
                                                endTime: time[1]
                                            };
                                        }
                                    },
                                    timeRangeRulerConfig4: {
                                        step: 15, //设置间隔分钟数，可选，默认是30分钟
                                        value: [dateArr[3].startTime, dateArr[3].endTime],
                                        range: true, //时间范围选择
                                        change: function(time) {
                                            dateArr[3] = {
                                                type: 4,
                                                startTime: time[0],
                                                endTime: time[1]
                                            };
                                        }
                                    },
                                    timeRangeRulerConfig5: {
                                        step: 15, //设置间隔分钟数，可选，默认是30分钟
                                        value: [dateArr[4].startTime, dateArr[4].endTime],
                                        range: true, //时间范围选择
                                        change: function(time) {
                                            dateArr[4] = {
                                                type: 5,
                                                startTime: time[0],
                                                endTime: time[1]
                                            };
                                        }
                                    },
                                    timeRangeRulerConfig6: {
                                        step: 15, //设置间隔分钟数，可选，默认是30分钟
                                        value: [dateArr[5].startTime, dateArr[5].endTime],
                                        range: true, //时间范围选择
                                        change: function(time) {
                                            dateArr[5] = {
                                                type: 6,
                                                startTime: time[0],
                                                endTime: time[1]
                                            };
                                        }
                                    },
                                    timeRangeRulerConfig7: {
                                        step: 15, //设置间隔分钟数，可选，默认是30分钟
                                        value: [dateArr[6].startTime, dateArr[6].endTime],
                                        range: true, //时间范围选择
                                        change: function(time) {
                                            dateArr[6] = {
                                                type: 7,
                                                startTime: time[0],
                                                endTime: time[1]
                                            };
                                        }
                                    }

                                    // sessionTypeList:raw.sessionType.split(','),
                                    // rdpList:raw.rdp.split(','),
                                    // sshList:raw.ssh.split(','),
                                },
                                watch: {
                                    'fileMon.enable': function(val) {
                                        this.fileTypeTreeConfig.disabled = !val;
                                    }
                                },
                                mounted: function() {
                                    $('#watermarkPickColor').colpick({
                                        submit: true,
                                        onSubmit: function(color, color2) {
                                            addStrategyBox.waterMarkColor = color2;
                                            $('.colpick.colpick_full').hide();
                                            addStrategyBox.$refs.waterMarkColorSelect.isDroped = false;
                                        }
                                    });
                                    // gd.get(baseUrl + '/strategy/getStrategySessionType',{'guid':cell},function(res){
                                    //     addStrategyBox.sessionTypeList = res.data
                                    // })
                                    // gd.get( baseUrl + '/strategy/getStrategyRDP',{'guid':cell},function(res){
                                    //     addStrategyBox.rdpList = res.data
                                    // })
                                    // gd.get( baseUrl + '/strategy/getStrategySSH',{'guid':cell},function(res){
                                    //     addStrategyBox.sshList = res.data
                                    // })
                                }
                            });

                            // var strategyLoginTimeArr = raw.strategyLoginTime.split('');
                            // for(var i=0; i< strategyLoginTimeArr.length; i++){
                            //     var className = "dateActive";
                            //     if(strategyLoginTimeArr[i] == '0'){
                            //         className = '';
                            //     }
                            //     var obj = {
                            //         'value':i,
                            //         'class':className
                            //     }
                            //     addStrategyBox.allDateList.push(obj);
                            // }
                            addStrategyBox.confirmTime();

                            $('.timerulerUl li').click(function(e) {
                                var index = $(this).index();
                                $(this)
                                    .siblings()
                                    .removeClass('active');
                                $(this).addClass('active');
                                $('.timeRulerConBox').css('margin-left', -610 * index);
                                // $('.timeRulerConBox .timerulerCon').hide();
                                // $('.timeRulerConBox .timerulerCon').eq(index).show();
                                // addStrategyBox.timeRangeRulerConfig2.value =  ['00:00', '23:59'];
                                // addStrategyBox.timeRangeRulerConfig6.value =  ['00:00', '23:59'];
                            });
                        } else {
                        }
                    });
                },
                end: function(dom) {}
            });
        }
    });
}

// 删除
if (permissionSetArr.indexOf('strategy::delete::strategy') > -1) {
    permissionSetStrategy.push({
        type: 'button',
        icon: 'icon-delete',
        title: '删除',
        disabled: true,
        action: function() {
            var allCheckedData = gd.table('strategylistTable').getCheckedData();
            if (allCheckedData.length == 0) {
                gd.showWarning('请至少选择一条记录', {
                    id: 'warning'
                });
                return false;
            }
            // 暂存批量删除id
            var deleteIds = [];
            for (var i = 0; i < allCheckedData.length; i++) {
                deleteIds.push(allCheckedData[i][0]);
            }
            deleteIds = deleteIds.join(',');
            gd.showConfirm({
                id: 'strategyDeleteWindow',
                content: '确定要删除吗?',
                btn: [
                    {
                        text: '删除',
                        class: 'gd-btn-danger',
                        enter: true, // 响应回车
                        disabled: false,
                        action: function(dom) {
                            $.ajax({
                                url: baseUrl + '/strategy/deleteStrategiesByGuids?strategyIds=' + deleteIds,
                                type: 'DELETE',
                                success: function(data) {
                                    if (data.resultCode == '0') {
                                        gd.showSuccess('删除成功');
                                        gd.table('strategylistTable').reload();
                                    } else {
                                        gd.showError(data.resultMsg);
                                    }
                                },
                                error: function(xhr, errorText, errorStatus) {
                                    gd.showError(data.resultMsg);
                                }
                            });
                        }
                    },
                    {
                        text: '取消',
                        action: function() {}
                    }
                ],
                end: function(dom) {}
            });
        }
    });
    permissionSetStrategyTable.push({
        icon: 'icon-delete',
        title: '删除',
        action: function(cell, row, raw) {
            gd.showConfirm({
                id: 'wind',
                content: '确定要删除吗?',
                btn: [
                    {
                        text: '删除',
                        class: 'gd-btn-danger',
                        enter: true, //响应回车
                        action: function(dom) {
                            $.ajax({
                                url: baseUrl + '/strategy/strategy/' + cell,
                                type: 'DELETE',
                                contentType: 'application/json', //设置请求参数类型为json字符串
                                dataType: 'json',
                                success: function(res) {
                                    if (res.resultCode == '0') {
                                        gd.showSuccess('删除成功');
                                        gd.table('strategylistTable').reload();
                                    } else {
                                        gd.showMsg(res.resultMsg, { time: 5000 });
                                    }
                                }
                            });
                        }
                    },
                    {
                        text: '取消',
                        action: function() {}
                    }
                ]
            });
        }
    });
}

permissionSetStrategy.push({
    type: 'searchbox',
    placeholder: '名称',
    action: function(val) {
        gd.table('strategylistTable').reload(false, { searchStr: val }, false);
    }
});

var methodsAll = {
    logintimeChange: function(data) {
        if (data.target.className == 'dateActive') {
            data.target.className = '';
        } else {
            data.target.className = 'dateActive';
        }
    },
    loginTimeUpDown: function(data) {
        var visible =
            $('.logintimeDown').css('visibility') && $('.logintimeDown').css('visibility') == 'hidden' ? false : true;
        var display = $('.logintimeDown').is(':visible'); //返回值为true则对象是显示的，false则是隐藏的
        if (!visible) {
            $('.logintimeDown').css('visibility', 'visible');
        } else {
            $('.logintimeDown').css('visibility', 'hidden');
        }
    },
    confirmTime: function(data) {
        var valHtml = '';
        dateArr.map(function(item, index) {
            if (item.type == 1) {
                valHtml += '周一' + item.startTime + '-' + item.endTime + ';';
            }
            if (item.type == 2) {
                valHtml += '周二' + item.startTime + '-' + item.endTime + ';';
            }
            if (item.type == 3) {
                valHtml += '周三' + item.startTime + '-' + item.endTime + ';';
            }
            if (item.type == 4) {
                valHtml += '周四' + item.startTime + '-' + item.endTime + ';';
            }
            if (item.type == 5) {
                valHtml += '周五' + item.startTime + '-' + item.endTime + ';';
            }
            if (item.type == 6) {
                valHtml += '周六' + item.startTime + '-' + item.endTime + ';';
            }
            if (item.type == 7) {
                valHtml += '周日' + item.startTime + '-' + item.endTime;
            }
        });

        $('.logintimeSpan').html(valHtml);
        $('.logintimeSpan').attr('title', valHtml);
        $('.logintimeDown').css('visibility', 'hidden');

        // timeRulerArr.push(data1)
    },

    cancelTime: function(data) {
        $('.logintimeDown').css('visibility', 'hidden');
        dateArr = [
            { type: 1, startTime: '00:00', endTime: '23:59' },
            { type: 2, startTime: '00:00', endTime: '23:59' },
            { type: 3, startTime: '00:00', endTime: '23:59' },
            { type: 4, startTime: '00:00', endTime: '23:59' },
            { type: 5, startTime: '00:00', endTime: '23:59' },
            { type: 6, startTime: '00:00', endTime: '23:59' },
            { type: 7, startTime: '00:00', endTime: '23:59' }
        ];
    }
};

var strategylist = new Vue({
    el: '#contentDiv',
    methods: {},
    data: {
        authenticationMethod: 1,
        userStrategy: 2,
        //工具栏配置
        toolbarConfig: permissionSetStrategy,

        //表格配置
        tableConfig: {
            id: 'strategylistTable',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            orderColumn: 'createTime',
            orderType: 'desc',
            fillBlank: '--',
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/strategy/getStrategyInPage',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    // console.log(data);
                    data.rows = data.rows.map(function(obj) {
                        return [obj.guid, obj.name, obj.createTime, obj.guid];
                    });
                    return data;
                },
                data: {
                    searchStr: ''
                }
            },
            columns: [
                {
                    name: 'checkbox',
                    type: 'checkbox',
                    width: '60', //列宽
                    align: 'center', //对齐方式，默认left，与class不同，class只影响内容，align会影响内容和表头
                    change: function(checkedData, checkedRawData) {
                        //复选框改变，触发事件，返回所有选中的列的数据,第1个参数为加工后的表格数据，第2个参数为未加工的表格数据
                        setToolBtnDisable(strategylist.toolbarConfig, 'icon-delete', checkedData.length == 0);
                    }
                },
                {
                    name: 'name',
                    head: '策略名称',
                    width: '600', //列宽
                    //ellipsis: false，可以禁用text ellipsis,默认为true
                    title: function(cell, row, raw) {
                        //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据
                        return cell;
                    }
                },
                {
                    name: 'createTime',
                    head: '创建时间',
                    width: '200',
                    orderable: true
                },
                {
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    operates: permissionSetStrategyTable
                }
            ]
        }
    },
    mounted: function() {}
});

function getWaterMarkData(vm) {
    var temp = gd.clone(vm._data);
    var data = {
        screenWatermark: temp.screenWatermark,
        fileMon: temp.fileMon
    };
    data.screenWatermark.content.localtion = 0;
    data.screenWatermark.content.locaktiontemp = [];
    if (temp.waterMarkPosition == 1) {
        temp.waterMarkPositionArr.forEach(function(item) {
            data.screenWatermark.content.locaktiontemp.push(Number(item));
            data.screenWatermark.content.localtion += Number(item);
        });
    }
    data.screenWatermark.content.color = Number('0xff' + temp.waterMarkColor);
    data.screenWatermark.enable = Number(data.screenWatermark.enable);
    data.screenWatermark.content.computername = Number(data.screenWatermark.content.computername);
    data.screenWatermark.content.depname = Number(data.screenWatermark.content.depname);
    data.screenWatermark.content.macaddress = Number(data.screenWatermark.content.macaddress);
    data.screenWatermark.content.ip = Number(data.screenWatermark.content.ip);
    data.screenWatermark.content.time = Number(data.screenWatermark.content.time);
    data.screenWatermark.content.username = Number(data.screenWatermark.content.username);
    data.screenWatermark.content.manual = Number(data.screenWatermark.content.manual);
    data.screenWatermark.content.direction = Number(data.screenWatermark.content.direction);
    data.screenWatermark.content.fontsize = Number(data.screenWatermark.content.fontsize);
    data.screenWatermark.content.opacity = Number(data.screenWatermark.content.opacity);
    data.fileMon.enable = Number(data.fileMon.enable);
    data.fileMon.content.resourceManager = Number(data.fileMon.content.resourceManager);
    data.fileMon.content.fileExt = temp.fileTypeList
        .map(function(obj) {
            return obj.name;
        })
        .join(';');
    data.screenWatermark = JSON.stringify(data.screenWatermark);
    data.fileMon = JSON.stringify(data.fileMon);
    return data;
}

function parseWaterMarkData(data) {
    var temp = {
        screenWatermark: JSON.parse(data.screenWatermark),
        fileMon: JSON.parse(data.fileMon)
    };
    temp.waterMarkColor = temp.screenWatermark.content.color.toString(16).substr(2) || 'ff0000';
    temp.waterMarkPosition = temp.screenWatermark.content.localtion > 0 ? 1 : 0;
    temp.waterMarkPositionArr = [];
    for (var i = 0; i < temp.screenWatermark.content.locaktiontemp.length; i++) {
        temp.waterMarkPositionArr.push(temp.screenWatermark.content.locaktiontemp[i]);
    }
    var fileExts = temp.fileMon.content.fileExt.split(';');
    temp.fileTypeTreeData = data.fileMonTree.map(function(node) {
        node.pId = node.pid;
        delete node.pid;
        node.expand = false;
        if (node.pId !== null) {
            node.checked = fileExts.indexOf(node.name) > -1;
        }
        return node;
    });
    temp.fileTypeList = temp.fileTypeTreeData.filter(function(node) {
        return fileExts.indexOf(node.name) > -1;
    });
    return temp;
}
