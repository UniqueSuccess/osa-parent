var treeData = '';

var permissionSetMonitorHistoryTable = [];
if (permissionSetArr.indexOf('finishsession::replay::replay') > -1) {
    permissionSetMonitorHistoryTable.push({
        icon: 'icon-play',
        title: '回放',
        disabled: function (cell, row, raw) {//是否禁用
            return !raw.hasReplay;
        },
        action: function(cell, row, raw) {
            window.open(baseUrl + '/replay/player?id=' + cell);
        }
    });
}

if (permissionSetArr.indexOf('finishsession::command::page') > -1) {
    permissionSetMonitorHistoryTable.push({
        icon: 'icon-command',
        title: '命令审计',
        action: function(cell, row, raw) {
            audit(raw, 'command');
        }
    });
}

var usergroupList = new Vue({
    el: '#contentDiv',
    methods: {},
    data: {
        toolbarConfig: [
            {
                type: 'searchbox',
                placeholder: '用户名/IP',
                action: function(val) {
                    gd.table('realTimeMonitorlistTable').reload(false, { searchStr: val }, false);
                }
            }
        ],
        //表格配置
        tableConfig: {
            id: 'realTimeMonitorlistTable',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            fillBlank: '--',
            excludeSearchKey: ['isFinish'],
            ajax: {
                url: baseUrl + '/session/getSessionsInPage',
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.isBlocking,
                            obj.userUsername,
                            obj.userName,
                            obj.remoteAddr,
                            obj.assetName,
                            obj.assetAddr,
                            obj.systemUser,
                            obj.protocol,
                            obj.dateStart,
                            obj.dateEnd,
                            obj.duration,
                            obj.id
                        ];
                    });
                    return data;
                },
                data: {
                    isFinish: 1,
                    searchStr: ''
                }
            },
            columns: [
                {
                    name: 'isBlocking',
                    head: '状态',
                    filters: [
                        //设置检索条件
                        {
                            label: '正常',
                            value: 0
                        },
                        {
                            label: '阻断',
                            value: 1
                        }
                    ],
                    render: function(cell) {
                        var mark = 'mark-green';
                        var text = '正常';
                        if (cell) {
                            mark = 'mark-red';
                            text = '阻断';
                        }
                        return '<span class="gd-mark {0}">{1}</span>'.format(mark, text);
                    }
                },
                {
                    name: 'userUsername',
                    head: '用户名'
                },
                {
                    name: 'userName',
                    head: '姓名'
                },
                {
                    name: 'remoteAddr',
                    head: '用户IP'
                },
                {
                    name: 'assetName', //本列如果有排序或高级搜索，必须要有name
                    head: '目标设备名称',
                    ellipsis: false, //可以禁用text ellipsis,默认为true
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        //自定义表格内容
                        return cell;
                    }
                },
                {
                    name: 'assetAddr',
                    head: '目标设备IP'
                },
                {
                    name: 'systemUser',
                    head: '目标设备账号'
                },
                {
                    name: 'protocol',
                    head: '协议',
                    filters: [
                        //设置检索条件
                        {
                            label: 'SSH',
                            value: 'ssh'
                        },
                        {
                            label: 'RDP',
                            value: 'rdp'
                        }
                    ]
                },
                {
                    name: 'dateStart',
                    head: '开始时间',
                    orderable: true
                },
                {
                    name: 'dateEnd',
                    head: '结束时间',
                    orderable: true
                },
                {
                    name: 'duration',
                    head: '时长'
                },
                {
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    width: 120,
                    operates: permissionSetMonitorHistoryTable
                }
            ]
        }
    }
});

// 审计
function audit(raw, type) {
    gd.showLayer({
        id: 'auditWind',
        title: '审计详情',
        content: $('#audit_layer').html(),
        size: [900, 600],
        btn: [],
        success: function() {
            configApp = new Vue({
                el: '#audit_app',
                data: {
                    curIndex: 0,
                    hasCommand:raw.hasCommandReplay,
                    //表格配置
                    commandConfig: {
                        id: 'commandTable',
                        length: 50, //每页多少条,默认50，可选
                        orderColumn: '', //排序列
                        orderType: 'desc', //排序规则，desc或asc,默认desc
                        fillBlank: '--',
                        excludeSearchKey:['sessionId'],
                        ajax: {
                            //其它ajax参数同jquery
                            url: baseUrl + '/command/getCommandListInPage',
                            //改变从服务器返回的数据给table
                            dataSrc: function(data) {
                                data.rows = data.rows.map(function(obj) {
                                    return [obj.status, obj.input, obj.time];
                                });
                                return data;
                            },
                            //请求参数
                            data: {
                                sessionId: raw.id,
                                searchStr: ''
                            }
                        },
                        columns: [
                            {
                                name: 'state',
                                head: '状态',
                                align: 'center',
                                width: 200,
                                render: function(cell) {
                                    var map = {
                                        0: {
                                            status: 'forbidden',
                                            text: '禁止'
                                        },
                                        1: {
                                            status: 'normal',
                                            text: '正常'
                                        },
                                        2: {
                                            status: 'unpass',
                                            text: '审核拒绝'
                                        },
                                        3: {
                                            status: 'pass',
                                            text: '审核通过'
                                        },
                                        4: {
                                            status: 'block',
                                            text: '阻断'
                                        }
                                    };
                                    return '<div class="state-label" {0}>{1}</div>'.format(
                                        map[cell].status,
                                        map[cell].text
                                    );
                                }
                            },
                            {
                                name: 'state',
                                head: '命令'
                            },
                            {
                                name: 'state',
                                head: '执行时间',
                                orderable: true
                            }
                        ]
                    }
                },
                methods: {
                    tabChange: function(data) {
                        this.curIndex = data.index;
                    },
                    tableReload: function(e) {
                        var val = $(e.currentTarget)
                            .closest('.searchbox')
                            .find('input')
                            .val()
                            .trim();
                        switch (this.curIndex) {
                            case 0:
                                gd.table('commandTable').reload(1, { searchStr: val });
                                break;
                        }
                    },
                    commandPlay: function() {
                        if(!raw.hasCommandReplay){
                            return;
                        }
                        gd.get(
                            baseUrl + '/replay/getCommandReplayBySessionId?sessionId=' +  raw.id,
                            function(msg) {
                                if (msg.resultCode == 0) {
                                    gd.get(baseUrl + msg.data.filepath, function(json) {
                                        var data = {
                                            version: 1,
                                            width: 140,
                                            height: 34,
                                            duration: 0,
                                            command: '',
                                            title: '',
                                            env: {
                                                TERM: 'xterm-256color',
                                                SHELL: ''
                                            },
                                            stdout: []
                                        };
                                        var prevTime = 0;
                                        data.stdout.push([0, '']);
                                        $.each(json, function(curTime, m, i) {
                                            data.duration = Math.max(data.duration, Number(curTime));
                                            var command = [curTime - prevTime, m];
                                            data.stdout.push(command);
                                            prevTime = curTime;
                                        });
                                        var blob = new Blob([JSON.stringify(data)], { type: 'application/json' });
                                        gd.showLayer({
                                            id: 'commandPlay',
                                            title: '命令回放',
                                            content: $('#temp_commandPlay').html(),
                                            size: [936, 610],
                                            btn: [],
                                            success: function() {
                                                asciinema.player.js.CreatePlayer(
                                                    'asciinema_player',
                                                    URL.createObjectURL(blob),
                                                    {
                                                        width: data.width,
                                                        height: data.height,
                                                        autoPlay: true
                                                    }
                                                );
                                            }
                                        });
                                    });
                                } else {
                                    gd.showError(msg.resultMsg);
                                }
                            }
                        );
                    }
                },
                mounted: function() {}
            });
        }
    });
}
