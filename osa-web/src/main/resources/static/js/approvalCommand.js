var approvalCommandList = new Vue({
    el: '#contentDiv',
    mounted:function (){
    },
    methods: {
    },
    data: {
        toolbarConfig: [
            {
                type: 'searchbox',
                placeholder: "",
                action: function (val) {
                    gd.table('approvalCommandTable').reload(false,{searchStr:val},false)
                }
            }
        ],

        //表格配置
        tableConfig: {
            id: 'approvalCommandTable',
            length: 20,
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
                url: baseUrl + '/approval/getApprovalFlowCommandsInPage',
                //改变从服务器返回的数据给table
                dataSrc: function (data) {
                    data.rows = data.rows.map(function (obj) {
                        return [
                            obj.commandContent,
                            obj.applicantName,
                            obj.applicantUsername,
                            obj.applyTime,
                            obj.applyTime,
                            obj.id
                        ]
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
                    name: 'username',//本列如果有排序或高级搜索，必须要有name
                    head: '命令',
                    title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                        return cell
                    },
                    render: function (cell, row, raw) {//自定义表格内容
                        return cell;
                    }
                },
                {
                    name: 'name',
                    head: '申请人',
                    width:'260',
                    title: function (cell, row, raw) {
                        return cell
                    }
                },
                {
                    name: 'department',
                    head: '申请人用户名',
                    width:'260',
                    title: function (cell, row, raw) {
                        return cell
                    }
                },
                {
                    name: "lastLoginTime",
                    head: '提交时间',
                    width:'250',
                    orderable: true,
                    show: true,
                },
                {
                    name: "lastLoginTime",
                    head: '有效时间',
                    width:'230',
                },
                {
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    operates: [
                        {
                            icon: 'icon-document',
                            title: '详情',
                            action: function (cell, row, raw) {
                            }
                        },
                        {
                            icon: 'icon-checkmark',
                            text: '通过',
                            action: function (cell,row,raw) {
                            }
                        },
                        {
                            icon: 'icon-insulate',
                            text: '拒绝',
                            action: function (cell,row,raw) {
                            }
                        }
                    ]
                }
            ]
        },

        approvedTableConfig: {
            id: 'approvalCommandTable',
            length: 20,
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
                url: baseUrl + '/approval/getApprovalFlowCommandsInPage',
                //改变从服务器返回的数据给table
                dataSrc: function (data) {
                    data.rows = data.rows.map(function (obj) {
                        return [
                            obj.status,
                            obj.commandContent,
                            obj.applicantName,
                            obj.applicantUsername,
                            obj.applyTime,
                            obj.approvalRemark,
                            obj.id
                        ]
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
                    name: 'status',//本列如果有排序或高级搜索，必须要有name
                    head: '状态',
                    width:'80',
                    align:'center',
                    title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                        return cell
                    },
                    render: function (cell, row, raw) {//自定义表格内容
                        var html = '';
                        if(cell == 1){
                            html += '<span class="bar-status-mark status-mark-add">通过</span>'
                        }else if(cell == -1){
                            html +='<span class="bar-status-mark status-mark-refause">拒绝</span>'
                        }
                        return html
                    }
                },
                {
                    name: 'username',//本列如果有排序或高级搜索，必须要有name
                    head: '命令',
                    title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                        return cell
                    },
                    render: function (cell, row, raw) {//自定义表格内容
                        return cell;
                    }
                },
                {
                    name: 'name',
                    head: '申请人',
                    width:'260',
                    title: function (cell, row, raw) {
                        return cell
                    }
                },
                {
                    name: 'department',
                    head: '申请人用户名',
                    width:'260',
                    title: function (cell, row, raw) {
                        return cell
                    }
                },
                {
                    name: "lastLoginTime",
                    head: '提交时间',
                    width:'250',
                    orderable: true,
                    show: true,
                },
                {
                    name: "lastLoginTime",
                    head: '备注',
                    width:'250',
                },
                {
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    width: '150',
                    operates: [
                        {
                            icon: 'icon-document',
                            title: '详情',
                            action: function (cell, row, raw) {
                            }
                        }
                    ]
                }
            ]
        },
    }
})