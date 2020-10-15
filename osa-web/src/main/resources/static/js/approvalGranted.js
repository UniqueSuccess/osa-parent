var timer;
var everyApprovalTable = '';
var waitUrl = '';
var everyApprovedDetail;
var everyApprovalDetail;

// 权限
var permissionSetApproval = [];

// 通过
if (permissionSetArr.indexOf('approval::operate::approval') > -1) {
    permissionSetApproval.push({
        icon: 'icon-checkmark',
        title: '通过',
        action: function(cell, row, raw) {
            var dom = gd.showConfirm({
                id: 'assetAuthwind',
                content: '确定要通过设备的授权申请?',
                btn: [
                    {
                        text: '通过',
                        class: 'gd-btn',
                        enter: true, //响应回车
                        action: function(dom) {
                            $.ajax({
                                url: baseUrl + '/approval/approvalResut',
                                type: 'GET',
                                data: { flowId: cell, approvalResult: 1 },
                                contentType: 'application/json',
                                dataType: 'json',
                                success: function(res) {
                                    if (res.resultCode == '0') {
                                        gd.showSuccess('操作成功！');
                                        clearInterval(timer);
                                        gd.table('approvalGrantedTable').reload();
                                        gd.table('approvalCommandTable').reload();
                                    }
                                }
                            });
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
    });
}

// 拒绝
if (permissionSetArr.indexOf('approval::operate::approval') > -1) {
    permissionSetApproval.push({
        icon: 'icon-insulate',
        title: '拒绝',
        action: function(cell, row, raw) {
            var dom = gd.showConfirm({
                id: 'assetAuthwind',
                content: '确定要拒绝设备的授权申请?',
                btn: [
                    {
                        text: '拒绝',
                        class: 'gd-btn-danger',
                        enter: true, //响应回车
                        action: function(dom) {
                            $.ajax({
                                url: baseUrl + '/approval/approvalResut',
                                type: 'GET',
                                data: { flowId: cell, approvalResult: -1 },
                                contentType: 'application/json',
                                dataType: 'json',
                                success: function(res) {
                                    if (res.resultCode == '0') {
                                        gd.showSuccess('操作成功！');
                                        clearInterval(timer);
                                        gd.table('approvalGrantedTable').reload();
                                        gd.table('approvalCommandTable').reload();
                                    }
                                }
                            });
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
    });
}

function methodDefficult(method, style) {
    if (method == '1' || method == '5' || method == '6') {
        //设备-用户1，删除设备5，删除账号6
        everyApprovalTable = {
            id: 'everyApprovalDetail',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            filterImmediately: true,
            fillBlank: '--',
            ajax: {
                //其它ajax参数同jquery
                url: waitUrl,
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    if (style) {
                        everyApprovedDetail.approvalPeople = data.data.applicantName;
                        everyApprovedDetail.approveRemark = data.data.approvalRemark;
                    } else {
                        everyApprovalDetail.approvalPeople = data.data.applicantName;
                    }

                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.definitionId,
                            obj.grantedMethod,
                            obj.assettypeName,
                            obj.assetName,
                            obj.assetIp,
                            obj.assetAccountName,
                            obj.assetgroupName,
                            obj.userUsername,
                            obj.userName,
                            obj.usergroupNames
                        ];
                    });
                    return data;
                }
            },
            columns: [
                {
                    name: 'status',
                    head: '类型',
                    width: '90',
                    align: 'center',
                    render: function(cell, row, raw) {
                        var html = '';
                        if (cell == 2) {
                            html += '<span class="bar-status-mark status-mark-add">添加</span>';
                        } else if (cell == 3 || cell == 4 || cell == 5 || cell == 6) {
                            html += '<span class="bar-status-mark status-mark-delete">删除</span>';
                        }
                        return html;
                    }
                },
                {
                    name: 'username',
                    head: '授权方式',
                    width: '160',
                    render: function(cell, row, raw) {
                        if (cell == 1) {
                            return '设备<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 2) {
                            return '设备组<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 3) {
                            return '设备<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 4) {
                            return '设备组<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 5) {
                            return '删除设备';
                        }
                        if (cell == 6) {
                            return '删除设备账号';
                        }
                        if (cell == 7) {
                            return '删除设备组';
                        }
                    }
                },
                {
                    name: 'username',
                    head: '设备类型',
                    width: '150'
                },
                {
                    name: 'department',
                    head: '设备名称'
                },
                {
                    name: 'department',
                    head: '设备IP'
                },
                {
                    name: 'department',
                    head: '设备账号'
                },
                {
                    name: 'department',
                    head: '设备组'
                },
                {
                    name: 'department',
                    head: '用户名'
                },
                {
                    name: 'department',
                    head: '姓名'
                },
                {
                    name: 'department',
                    head: '用户组'
                }
            ]
        };
    } else if (method == '2') {
        //设备组-用户
        everyApprovalTable = {
            id: 'everyApprovalDetail',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            filterImmediately: true,
            fillBlank: '--',
            ajax: {
                //其它ajax参数同jquery
                url: waitUrl,
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    if (style) {
                        everyApprovedDetail.approvalPeople = data.data.applicantName;
                        everyApprovedDetail.approveRemark = data.data.approvalRemark;
                    } else {
                        everyApprovalDetail.approvalPeople = data.data.applicantName;
                    }
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.definitionId,
                            obj.grantedMethod,
                            obj.assetgroupName,
                            obj.assetgroupPname,
                            obj.assetgroupRelationNumber,
                            obj.userUsername,
                            obj.userName,
                            obj.usergroupNames
                        ];
                    });
                    return data;
                }
            },
            columns: [
                {
                    name: 'status',
                    head: '类型',
                    width: '90',
                    align: 'center',
                    render: function(cell, row, raw) {
                        var html = '';
                        if (cell == 2) {
                            html += '<span class="bar-status-mark status-mark-add">添加</span>';
                        } else if (cell == 3 || cell == 4 || cell == 5 || cell == 6) {
                            html += '<span class="bar-status-mark status-mark-delete">删除</span>';
                        }
                        return html;
                    }
                },
                {
                    name: 'username',
                    head: '授权方式',
                    width: '160',
                    render: function(cell, row, raw) {
                        if (cell == 1) {
                            return '设备<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 2) {
                            return '设备组<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 3) {
                            return '设备<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 4) {
                            return '设备组<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 5) {
                            return '删除设备';
                        }
                        if (cell == 6) {
                            return '删除设备账号';
                        }
                        if (cell == 7) {
                            return '删除设备组';
                        }
                    }
                },
                {
                    name: 'username',
                    head: '设备组',
                    width: '150'
                },
                {
                    name: 'department',
                    head: '所属组'
                },
                {
                    name: 'department',
                    head: '设备数'
                },
                {
                    name: 'department',
                    head: '用户名'
                },
                {
                    name: 'department',
                    head: '姓名'
                },
                {
                    name: 'department',
                    head: '用户组'
                }
            ]
        };
    } else if (method == '3') {
        //设备-用户组
        everyApprovalTable = {
            id: 'everyApprovalDetail',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            filterImmediately: true,
            fillBlank: '--',
            ajax: {
                //其它ajax参数同jquery
                url: waitUrl,
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    if (style) {
                        everyApprovedDetail.approvalPeople = data.data.applicantName;
                        everyApprovedDetail.approveRemark = data.data.approvalRemark;
                    } else {
                        everyApprovalDetail.approvalPeople = data.data.applicantName;
                    }
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.definitionId,
                            obj.grantedMethod,
                            obj.assettypeName,
                            obj.assetName,
                            obj.assetIp,
                            obj.assetAccountName,
                            obj.assetgroupName,
                            obj.usergroupNames,
                            obj.usergroupPname,
                            obj.usergroupRelationNumber
                        ];
                    });
                    return data;
                }
            },
            columns: [
                {
                    name: 'status',
                    head: '类型',
                    width: '90',
                    align: 'center',
                    render: function(cell, row, raw) {
                        var html = '';
                        if (cell == 2) {
                            html += '<span class="bar-status-mark status-mark-add">添加</span>';
                        } else if (cell == 3 || cell == 4 || cell == 5 || cell == 6) {
                            html += '<span class="bar-status-mark status-mark-delete">删除</span>';
                        }
                        return html;
                    }
                },
                {
                    name: 'username',
                    head: '授权方式',
                    width: '160',
                    render: function(cell, row, raw) {
                        if (cell == 1) {
                            return '设备<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 2) {
                            return '设备组<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 3) {
                            return '设备<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 4) {
                            return '设备组<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 5) {
                            return '删除设备';
                        }
                        if (cell == 6) {
                            return '删除设备账号';
                        }
                        if (cell == 7) {
                            return '删除设备组';
                        }
                    }
                },

                {
                    name: 'username',
                    head: '设备类型',
                    width: '150'
                },
                {
                    name: 'department',
                    head: '设备名称'
                },
                {
                    name: 'department',
                    head: '设备IP'
                },
                {
                    name: 'department',
                    head: '设备账号'
                },
                {
                    name: 'department',
                    head: '设备组'
                },
                {
                    name: 'department',
                    head: '用户组'
                },
                {
                    name: 'department',
                    head: '所属组'
                },
                {
                    name: 'department',
                    head: '用户数'
                }
            ]
        };
    } else if (method == '4' || method == '7') {
        //删除设备组7，设备组-用户组4

        everyApprovalTable = {
            id: 'everyApprovalDetail',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            filterImmediately: true,
            fillBlank: '--',
            ajax: {
                //其它ajax参数同jquery
                url: waitUrl,
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    if (style) {
                        everyApprovedDetail.approvalPeople = data.data.applicantName;
                        everyApprovedDetail.approveRemark = data.data.approvalRemark;
                    } else {
                        everyApprovalDetail.approvalPeople = data.data.applicantName;
                    }

                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.definitionId,
                            obj.grantedMethod,
                            obj.assetgroupName,
                            obj.assetgroupPname,
                            obj.assetgroupRelationNumber,
                            obj.usergroupNames,
                            obj.usergroupPname,
                            obj.usergroupRelationNumber
                        ];
                    });
                    return data;
                }
            },
            columns: [
                {
                    name: 'status',
                    head: '类型',
                    width: '90',
                    align: 'center',
                    render: function(cell, row, raw) {
                        var html = '';
                        if (cell == 2) {
                            html += '<span class="bar-status-mark status-mark-add">添加</span>';
                        } else if (cell == 3 || cell == 4 || cell == 5 || cell == 6) {
                            html += '<span class="bar-status-mark status-mark-delete">删除</span>';
                        }
                        return html;
                    }
                },
                {
                    name: 'username',
                    head: '授权方式',
                    width: '160',
                    render: function(cell, row, raw) {
                        if (cell == 1) {
                            return '设备<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 2) {
                            return '设备组<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 3) {
                            return '设备<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 4) {
                            return '设备组<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 5) {
                            return '删除设备';
                        }
                        if (cell == 6) {
                            return '删除设备账号';
                        }
                        if (cell == 7) {
                            return '删除设备组';
                        }
                    }
                },
                {
                    name: 'username',
                    head: '设备组',
                    width: '150'
                },
                {
                    name: 'department',
                    head: '所属组'
                },
                {
                    name: 'department',
                    head: '设备数'
                },
                {
                    name: 'department',
                    head: '用户组'
                },
                {
                    name: 'department',
                    head: '所属组'
                },
                {
                    name: 'department',
                    head: '用户数'
                }
            ]
        };
    }
}

function approvalDetail(detailId, method) {
    var validate = '';
    var dom = gd.showLayer({
        id: 'everyApprovalDetail_layer',
        title: '设备授权申请',
        content: $('#everyApprovalDetailBox').html(),
        size: [1200, 600],
        btn: (function() {
            var btn = '';
            if (hasPermission('approval::operate::approval')) {
                btn = [
                    {
                        text: '通过',
                        action: function(dom) {
                            if(!validate.valid()){
                                return false
                            }
                            $.ajax({
                                url: baseUrl + '/approval/approvalResut',
                                type: 'GET',
                                data: {
                                    flowId: detailId,
                                    approvalResult: 1,
                                    approvalRemark: everyApprovalDetail.approveRemark
                                },
                                contentType: 'application/json',
                                dataType: 'json',
                                success: function(res) {
                                    if (res.resultCode == '0') {
                                        gd.showSuccess('操作成功！');
                                        clearInterval(timer);
                                        gd.table('approvalGrantedTable').reload();
                                        dom.close();
                                    }else{
                                        gd.showError(res.resultMsg)
                                    }
                                }
                            });
                        }
                    },
                    {
                        text: '拒绝',
                        class: 'gd-btn-danger',
                        action: function() {
                            if(!validate.valid()){
                                return false;
                            }
                            $.ajax({
                                url: baseUrl + '/approval/approvalResut',
                                type: 'GET',
                                data: {
                                    flowId: detailId,
                                    approvalResult: -1,
                                    approvalRemark: everyApprovalDetail.approveRemark
                                },
                                contentType: 'application/json',
                                dataType: 'json',
                                success: function(res) {
                                    if (res.resultCode == '0') {
                                        gd.showSuccess('操作成功');
                                        clearInterval(timer);
                                        gd.table('approvalGrantedTable').reload();
                                        dom.close();
                                    }
                                }
                            });
                        }
                    }
                ];
            }
            return btn;
        })(),
        success: function(dom) {
            waitUrl = baseUrl + '/approval/getApprovalFlowGrantedsDetailInPage?flowId=' + detailId;
            methodDefficult(method);
            everyApprovalDetail = new Vue({
                el: '#everyApprovalDetail',
                data: {
                    approvalPeople: '',
                    approveRemark: '',
                    everyApprovalTableConfig: everyApprovalTable
                },
                mounted:function(){
                    validate = gd.validate('#everyApprovalDetail form')
                }
            });
        },
        end: function() {}
    });
}

function approvaledDetail(detailId, method) {
    var dom = gd.showLayer({
        id: 'everyApprovedDetail_layer',
        title: '设备授权详情',
        content: $('#everyApprovedDetailBox').html(),
        size: [1200, 600],
        btn: [],
        success: function(dom) {
            waitUrl = baseUrl + '/approval/getApprovalFlowGrantedsDetailInPage?flowId=' + detailId;

            methodDefficult(method, 2);

            everyApprovedDetail = new Vue({
                el: '#everyApprovedDetail',
                data: {
                    approvalPeople: '',
                    approveRemark: '',
                    searchConfig: [
                        {
                            type: 'searchbox',
                            placeholder: '',
                            action: function(val) {
                                gd.table('everyApprovalDetail').reload(false, {searchStr:val}, false);
                            }
                        }
                    ],
                    everyApprovedTableConfig: everyApprovalTable
                }
            });
        },
        end: function() {}
    });
}

var approvalGrantedList = new Vue({
    el: '#contentDiv',
    mounted: function() {},
    methods: {},
    data: {
        toolbarConfig: [
            {
                type: 'searchbox',
                placeholder: '',
                action: function(val) {
                    gd.table('approvalGrantedTable').reload(false, { searchStr: val }, false);
                }
            }
        ],

        //表格配置
        tableConfig: {
            id: 'approvalGrantedTable',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            filterImmediately: true,
            orderColumn: 'applyTime',
            orderType: 'desc',
            fillBlank: '--',
            excludeSearchKey:['approvalType'],
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/approval/getApprovalFlowGrantedsInPage',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.definitionId,
                            obj.grantedMethod,
                            obj.grantedAssetNum,
                            obj.applicantName,
                            obj.applicantUsername,
                            obj.applyTime,
                            obj.effectiveTime,
                            obj.id
                        ];
                    });
                    return data;
                },
                data: {
                    approvalType: 0,
                    searchStr: ''
                }
            },
            columns: [
                {
                    name: 'username',
                    head: '类型',
                    align: 'center',
                    width: '80',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        var html = '';
                        if (cell == 2) {
                            html += '<span class="bar-status-mark status-mark-add">添加</span>';
                        } else if (cell == 3 || cell == 4 || cell == 5 || cell == 6) {
                            html += '<span class="bar-status-mark status-mark-delete">删除</span>';
                        }
                        return html;
                    }
                },
                {
                    name: 'username',
                    head: '授权方式',
                    width: '190',
                    render: function(cell, row, raw) {
                        if (cell == 1) {
                            return '设备<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 2) {
                            return '设备组<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 3) {
                            return '设备<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 4) {
                            return '设备组<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 5) {
                            return '删除设备';
                        }
                        if (cell == 6) {
                            return '删除设备账号';
                        }
                        if (cell == 7) {
                            return '删除设备组';
                        }
                    }
                },
                {
                    name: 'username',
                    head: '目标设备',
                    align: 'center',
                    width: '130',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return (
                            '<span class="bar-assetNum-mark pointer" onClick="approvalDetail(\'' +
                            raw.id +
                            "','" +
                            raw.grantedMethod +
                            '\')">' +
                            cell +
                            '</span>'
                        );
                    }
                },
                {
                    name: 'name',
                    head: '申请人',
                    width: '240',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return (
                            '<span class="pointer" onClick="approvalDetail(\'' +
                            raw.id +
                            "','" +
                            raw.grantedMethod +
                            '\')">' +
                            cell +
                            '</span>'
                        );
                    }
                },
                {
                    name: 'department',
                    head: '申请人用户名',
                    title: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'applyTime',
                    head: '提交时间',
                    width: '240',
                    orderable: true,
                    show: true
                },
                {
                    name: 'lastLoginTime',
                    head: '有效时间',
                    width: '220',
                    render: function(cell, row, raw) {
                        function getDate() {
                            var hours = Math.floor(cell / 3600);
                            var minutes = Math.floor(cell / 60) - Math.floor(cell / 3600) * 60;
                            var second = Math.floor(cell) - Math.floor(cell / 60) * 60;
                            if (hours < 10) {
                                hours = '0' + hours;
                            }
                            if (minutes < 10) {
                                minutes = '0' + minutes;
                            }
                            if (second < 10) {
                                second = '0' + second;
                            }
                            if (cell == 0) {
                                clearInterval(timer);
                            } else {
                                cell--;
                            }
                            return hours + ':' + minutes + ':' + second;
                        }
                        timer = setInterval(function() {
                            $('#time' + raw.id).html(getDate());
                        }, 1000);

                        if (cell == -1) {
                            return '无限制';
                        } else {
                            return (
                                '<span><i class="icon-time"></i><span id="time' +
                                raw.id +
                                '"' +
                                getDate() +
                                '</span></span>'
                            );
                        }
                    }
                },
                {
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    operates: permissionSetApproval
                }
            ]
        },

        approvedTableConfig: {
            id: 'approvalCommandTable',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            orderColumn: 'applyTime',
            orderType: 'desc',
            fillBlank: '--',
            excludeSearchKey:['approvalType'],
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/approval/getApprovalFlowGrantedsInPage',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.status,
                            obj.grantedMethod,
                            obj.grantedAssetNum,
                            obj.applicantName,
                            obj.applicantUsername,
                            obj.applyTime,
                            obj.approvalRemark
                        ];
                    });
                    return data;
                },
                data: {
                    approvalType: 1,
                    searchStr: ''
                }
            },
            columns: [
                {
                    name: 'status',
                    head: '类型',
                    width: '80',
                    align: 'center',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        var html = '';
                        if (cell == 1) {
                            html += '<span class="bar-status-mark status-mark-add">通过</span>';
                        } else if (cell == -1) {
                            html += '<span class="bar-status-mark status-mark-refause">拒绝</span>';
                        }
                        return html;
                    }
                },
                {
                    name: 'username',
                    head: '授权方式',
                    width: '200',
                    render: function(cell, row, raw) {
                        if (cell == 1) {
                            return '设备<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 2) {
                            return '设备组<span class="icon-arrow-right"></span>用户';
                        }
                        if (cell == 3) {
                            return '设备<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 4) {
                            return '设备组<span class="icon-arrow-right"></span>用户组';
                        }
                        if (cell == 5) {
                            return '删除设备';
                        }
                        if (cell == 6) {
                            return '删除设备账号';
                        }
                        if (cell == 7) {
                            return '删除设备组';
                        }
                    }
                },
                {
                    name: 'username',
                    head: '目标设备',
                    align: 'center',
                    width: '150',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return (
                            '<span class="bar-assetNum-mark pointer" onClick="approvaledDetail(\'' +
                            raw.id +
                            "','" +
                            raw.grantedMethod +
                            '\')">' +
                            cell +
                            '</span>'
                        );
                    }
                },
                {
                    name: 'name',
                    head: '申请人',
                    width: '260',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return (
                            '<span class="pointer" onClick="approvaledDetail(\'' +
                            raw.id +
                            "','" +
                            raw.grantedMethod +
                            '\')">' +
                            cell +
                            '</span>'
                        );
                    }
                },
                {
                    name: 'department',
                    head: '申请人用户名',
                    width: '260',
                    title: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'applyTime',
                    head: '提交时间',
                    width: '250',
                    orderable: true,
                    show: true
                },
                {
                    name: 'lastLoginTime',
                    head: '备注'
                }
            ]
        }
    }
});
