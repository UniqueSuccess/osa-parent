var treeData = '';
var remoteWind = {};

var permissionSetMonitorRealTimeTable = [];

// 播放
if(permissionSetArr.indexOf('realsession::monitor::monitor') > -1){
    permissionSetMonitorRealTimeTable.push({
        icon: 'icon-play',
        title: '播放',
        action: function(cell, row, raw) {
            remoteWind[raw.id] = window.open(
                baseUrl + '/granted/remote?' + $.param({ id: raw.id })
            );
        }
    })
}

// 阻断
if(permissionSetArr.indexOf('realsession::block::block') > -1){
    permissionSetMonitorRealTimeTable.push({
        icon: 'icon-insulate',
        title: '阻断',
        action: function(cell, row, raw) {
            var dom = gd.showConfirm({
                id: 'wind',
                content: '确定要阻断吗?',
                btn: [
                    {
                        text: '阻断',
                        class: 'gd-btn-danger',
                        enter: true,
                        action: function(dom) {
                            gd.post(
                                baseUrl + '/session/blockSessionBySessionId',
                                { assetId: raw.assetId, sessionId: raw.id },
                                function(msg) {
                                    if (msg.resultCode == 0) {
                                        gd.showSuccess('阻断成功！');
                                        gd.table('realTimeMonitorlistTable').reload();
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
    })
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
            excludeSearchKey:['isFinish'],
            ajax: {
                url: baseUrl + '/session/getSessionsInPage',
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.duration,
                            obj.userUsername,
                            obj.userName,
                            obj.remoteAddr,
                            obj.assetName,
                            obj.assetAddr,
                            obj.systemUser,
                            obj.protocol,
                            obj.dateStart,
                            obj.id
                        ];
                    });
                    return data;
                },
                data: {
                    isFinish: 0,
                    searchStr: ''
                }
            },
            columns: [
                {
                    name: 'dateEnd',
                    head: '监控中',
                    render: function(cell) {
                        if (cell) {
                            return '<i class="icon-monitoring"></i>' + cell;
                        }
                        return '';
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
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    width: 120,
                    operates:permissionSetMonitorRealTimeTable
                }
            ]
        }
    }
});
