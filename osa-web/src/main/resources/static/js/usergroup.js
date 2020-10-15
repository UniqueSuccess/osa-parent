
var treeData = '';
var treeAllData = '';
var treeAssetGroupData = '';

// 权限
var permissionSetUserGroup = [];
var permissionSetUserGroupTable = [];

// 新建
if(permissionSetArr.indexOf('usergroup::add') > -1){
    permissionSetUserGroup.push({
        type: 'button',
        icon: 'icon-add',
        title: '添加',
        action: function () {
            $("#addUsergroupBox").show();
            usergroupList.addDom = gd.showLayer({
                id: 'adduserGroup_layer',
                title: '新建用户组',
                content: $('#addUsergroupBox'),
                size: [530, 380],
                btn: [
                    {
                        text: '确定',
                        enter: true,//响应回车
                        action: function (dom) {
                            var validate = gd.validate('#usergroupForm', {
                                rules:[{
                                    name:'groupBelongRule',
                                    msg:'必填项',
                                    valid:function(value,el){
                                        if(value){
                                            return true
                                        }else{
                                            return false
                                        }
                                    }
                                },
                                {
                                    name:'exceptSpecialChar1',
                                    msg:'不能包含特殊字符',
                                    valid:function(value,el){
                                        var reg = /[\`\^\(\)\；\：\'\"\\\|\，\,<\.\>\/\?\[\]\{\}]/;
                                        if(!reg.test(value)){
                                            return true
                                        }else{
                                            return false
                                        }
                                    }
                                }
                               ]
                            });
                            var result = validate.valid();//获得整体的校验结果
                            if(result){
                                var addObject = $("#adduserGroup_layer form").serializeJSON();
                                addObject.pid = usergroupList.groupId;
                                $.ajax({
                                    type: 'post',
                                    url: baseUrl + "/usergroup/usergroup",
                                    data: addObject,
                                    success: function (res) {
                                        gd.closeLoading();
                                        if(!res.resultCode){
                                            gd.showSuccess("创建用户组成功！");
                                            gd.table('usergrouplistTable').reload();
                                        }else{
                                            gd.showError(res.resultMsg);
                                        }
                                        $("#addUsergroupBox").hide();
                                    },
                                    error: function (xhr, errorText, errorStatus) {
                                    }
                                })
                            }else{
                                return false
                            }
                        }
                    }, {
                        text: '取消',
                        action: function () {
                        }
                    }
                ],
                success: function (dom) {//参数为当前窗口dom对象
                    $.ajax({
                        url: baseUrl + '/usergroup/list',
                        dataType: "json",
                        success: function (data) {
                            treeData = data;
                            gd.tree('usergroupTree').setData(treeData);

                        }
                    })
                    usergroupList.groupId = '';
                    usergroupList.groupName = '';
                    usergroupList.groupBelong = '';
                    usergroupList.remark = '';
                },
                end: function (dom) {//参数为当前窗口dom对象
                }
            });
        }
    })
}

// 编辑
if(permissionSetArr.indexOf('usergroup::update') > -1){
    permissionSetUserGroupTable.push({
        icon: 'icon-edit',
        title: '编辑',//设置图标title
        action: function (cell, row, raw) {

            $("#addUsergroupBox").show();
            usergroupList.addDom = gd.showLayer({
                id: 'edituserGroup_layer',
                title: '编辑用户组',
                content: $('#addUsergroupBox'),
                size: [530, 380],
                btn: [
                    {
                        text: '确定',
                        enter: true,//响应回车
                        action: function (dom) {//参数为当前窗口dom对象
                           var validate = gd.validate('#usergroupForm', {
                                rules:[{
                                    name:'groupBelongRule',
                                    msg:'必填项',
                                    valid:function(value,el){
                                        if(value){
                                            return true;
                                        }else{
                                            if(cell == '1'){
                                                return true;
                                            }else{
                                                return false;
                                            }
                                        }
                                    }
                                }]
                            });
                            var result = validate.valid();//获得整体的校验结果
                            if(result){
                                var addObject = $("#edituserGroup_layer form").serializeJSON();
                                addObject.pid = usergroupList.groupId;
                                if(cell == '1'){
                                    addObject.pid = null
                                }
                                $.ajax({
                                    type: 'PUT',
                                    url: baseUrl + "/usergroup/usergroup/"+cell,
                                    data: addObject,
                                    success: function (res) {
                                        gd.closeLoading();
                                        if(!res.resultCode){
                                            gd.showSuccess("编辑用户组成功！");
                                            gd.table('usergrouplistTable').reload();
                                        }else{
                                            gd.showError(res.resultMsg);
                                        }
                                        $("#addUsergroupBox").hide();
                                    },
                                    error: function (xhr, errorText, errorStatus) {
                                    }
                                })
                            }else{
                                return false
                            }
                        }
                    }, {
                        text: '取消',
                        action: function () {
                        }
                    }
                ],
                success: function (dom) {//参数为当前窗口dom对象
                    $.ajax({
                        url: baseUrl + '/usergroup/list',
                        dataType: "json",
                        success: function (data) {
                            treeData = data;
                            var nodeData = {};
                            data.map(function(item,index){
                                if(cell == item.id){
                                    nodeData = item
                                    nodeData.disabled = true;
                                }
                            })
                            gd.tree('usergroupTree').setData(treeData);
                            gd.tree('usergroupTree').setNode(cell,nodeData);
                        }
                    })

                    if(cell == '1'){
                        $("#groupDiv").hide();
                    }else{
                        $("#groupDiv").show();
                    }

                    usergroupList.groupId = raw.pId;
                    usergroupList.groupName = raw.name;
                    usergroupList.groupBelong = raw.pName;
                    usergroupList.remark = raw.remark;
                },
                end: function (dom) {//参数为当前窗口dom对象
                }
            });
        }
    })
}

// 授权
if(permissionSetArr.indexOf('usergroup::granted') > -1){
    permissionSetUserGroupTable.push({
        icon: 'icon-authorize',
        text: '授权设备',
        title:'授权设备',
        action: function (cell,row,raw) {
            gd.showLayer({
                id: 'authorizedAddBox_layer',
                title: '授权设备',
                content: $('#authorizedAssetBox').html(),
                size: [1000, 600],
                btn: [],
                success: function (dom) {
                    var authorized = new Vue({
                        el:'#authorizedBox',
                        data:{
                            toolbarAssetConfig:[
                                {
                                    type: 'button',
                                    icon: 'icon-add',
                                    title: '添加设备权限',
                                    action: function () {
                                        $.ajax({
                                            url: baseUrl + '/granted/getGrantedTree',
                                            data:{'guid':cell,'isUserGroup':true},
                                            dataType: "json",
                                            success: function (data) {
                                                var authorizedAssetAdd = '';
                                                treeAllData = data;
                                                gd.showLayer({
                                                    id: 'authorizedAssetAdd_layer',
                                                    title: '添加设备',
                                                    content: $('#authorizedAssetAddBox').html(),
                                                    size: [740, 530],
                                                    btn: [{
                                                        text: '申请',
                                                        enter: true,//响应回车
                                                        action: function (dom) {//参数为当前窗口dom对象
                                                            var userObj = {
                                                                "userType": 2,
                                                                "userId": '',
                                                                "usergroupId": cell
                                                            }

                                                            var grantedObj = {
                                                                "grantedType": 3,
                                                                "assetaccounts": authorizedAssetAdd.assetAccountArr.join(','),
                                                                "assetgroups": "",
                                                            }

                                                            $.ajax({
                                                                type: 'post',
                                                                url: baseUrl + "/granted/grantedAsset4User",
                                                                data: {userJson:JSON.stringify(userObj),grantedJson:JSON.stringify(grantedObj)},
                                                                success: function (res) {
                                                                    if(!res.resultCode){
                                                                        gd.showSuccess("授权设备成功！");
                                                                        gd.table('assetAuthlistTable').reload(false);
                                                                    }else{
                                                                        gd.showError(res.resultMsg);
                                                                    }
                                                                    $("#authorizedAddBox").hide();
                                                                },
                                                                error: function (xhr, errorText, errorStatus) {
                                                                }
                                                            })
                                                        }
                                                    }, {
                                                        text: '取消',
                                                        action: function () {
                                                            $("#authorizedAddBox").hide();
                                                        }
                                                    }],
                                                    success: function (dom) {
                                                        authorizedAssetAdd = new Vue({
                                                            el:'#authorizedAssetAdd',
                                                            data:{
                                                                assetAccountList:[],
                                                                assetAuthListLen:0,
                                                                treeAuthConfig:{
                                                                    id: 'authorizedTree',//树的id，用于提供API
                                                                    accordion: true,
                                                                    data: treeAllData,
                                                                    showCheckBox: true,//默认是false;显示checkbox
                                                                    onSelect: function (node) {// 点击树节点时触发
                                                                        // gd.table('userlistTable').reload(false,{usergroupPid:node.id},false)
                                                                    },
                                                                    onChange: function (nodes) {// 点击复选框时触发
                                                                        authorizedAssetAdd.assetAccountList = [];
                                                                        authorizedAssetAdd.assetAccountArr = [];
                                                                        nodes.map(function(item,index){
                                                                            if(item.id.indexOf('account')>-1 && !item.ignore){
                                                                                var assetCon = gd.tree('authorizedTree').getNode(item.pId);
                                                                                var obj = {
                                                                                    assetName:assetCon.name,
                                                                                    accountName:item.name
                                                                                }
                                                                                authorizedAssetAdd.assetAccountArr.push(item.id);
                                                                                authorizedAssetAdd.assetAccountList.push(obj);
                                                                            }
                                                                        })
                                                                        authorizedAssetAdd.assetAuthListLen = authorizedAssetAdd.assetAccountList.length;
                                                                    },
                                                                    ready: function (data) {//树的数据改变后，dom渲染完成后触发，data为树的数据
                                                                    }
                                                                },
                                                            }
                                                        })
                                                    }
                                                })
                                                // gd.tree('authorizedTree').setData(treeAllData);
                                            }
                                        })
                                    }
                                }
                            ],
                            toolbarAssetGroupConfig:[{
                                type: 'button',
                                icon: 'icon-add',
                                title: '添加设备组权限',
                                action: function () {
                                    // 设备组树
                                    $.ajax({
                                        url: baseUrl + '/granted/getGrantedTree',
                                        data:{'guid':cell,'isGroup':true,'isUserGroup':true},
                                        dataType: "json",
                                        success: function (data) {
                                            var authorizedAssetGroupAdd = '';
                                            treeAssetGroupData = data;
                                            gd.showLayer({
                                                id: 'authorizedAssetGroupAdd_layer',
                                                title: '添加设备组',
                                                content: $('#authorizedGroupAddBox').html(),
                                                size: [740, 530],
                                                btn: [{
                                                    text: '申请',
                                                    enter: true,//响应回车
                                                    action: function (dom) {//参数为当前窗口dom对象
                                                        var userObj = {
                                                            "userType": 2,
                                                            "userId": '',
                                                            "usergroupId": cell
                                                        }
                                                        var grantedObj = {
                                                            "grantedType": 1,
                                                            "assetaccounts": '',
                                                            "assetgroups": authorizedAssetGroupAdd.assetGroupArr.join(','),
                                                        }

                                                        $.ajax({
                                                            type: 'post',
                                                            url: baseUrl + "/granted/grantedAsset4User",
                                                            data: {userJson:JSON.stringify(userObj),grantedJson:JSON.stringify(grantedObj)},
                                                            success: function (res) {
                                                                if(!res.resultCode){
                                                                    gd.showSuccess("授权设备组成功！");
                                                                    gd.table('assetGroupAuthlistTable').reload(false);
                                                                }else{
                                                                    gd.showError(res.resultMsg);
                                                                }
                                                            },
                                                            error: function (xhr, errorText, errorStatus) {
                                                            }
                                                        })
                                                    }
                                                }, {
                                                    text: '取消',
                                                    action: function () {
                                                    }
                                                }],
                                                success: function (dom) {
                                                    authorizedAssetGroupAdd = new Vue({
                                                        el:'#authorizedGroupAdd',
                                                        data:{
                                                            assetGroupAuthList:[],
                                                            assetGroupArr:[],
                                                            assetGroupAuthListLen:0,
                                                            treeAuthGroupConfig:{
                                                                id: 'authorizedGroupTree',//树的id，用于提供API
                                                                accordion: true,
                                                                data: treeAssetGroupData,
                                                                showCheckBox: true,//默认是false;显示checkbox
                                                                linkable: true,
                                                                onSelect: function (node) {// 点击树节点时触发
                                                                },
                                                                onChange: function (nodes) {// 点击复选框时触发

                                                                    authorizedAssetGroupAdd.assetGroupAuthList = [];
                                                                    authorizedAssetGroupAdd.assetGroupArr = [];

                                                                    nodes.map(function(item,index){
                                                                        if(!item.ignore && item.id.indexOf('nihility') == -1){
                                                                            var obj = {
                                                                                assetGroupName:item.name,
                                                                            }
                                                                            authorizedAssetGroupAdd.assetGroupArr.push(item.id);
                                                                            authorizedAssetGroupAdd.assetGroupAuthList.push(obj);
                                                                        }
                                                                    })
                                                                    authorizedAssetGroupAdd.assetGroupAuthListLen = authorizedAssetGroupAdd.assetGroupAuthList.length;

                                                                },
                                                                ready: function (data) {//树的数据改变后，dom渲染完成后触发，data为树的数据
                                                                }
                                                            }
                                                        }
                                                    })
                                                }
                                            })
                                        }
                                    })
                                }
                            }],
                            tableAssetConfig:{
                                id: 'assetAuthlistTable',
                                length: 20,
                                curPage: 1,
                                lengthMenu: [10, 30, 50, 100],
                                enableJumpPage: false,
                                enableLengthMenu: true,
                                enablePaging: true,
                                columnResize: true,
                                fillBlank: '--',
                                ajax: {
                                    url: baseUrl + '/granted/getAssestAccountsInPage?usergroupId='+ cell,
                                    dataSrc: function (data) {
                                        data.rows = data.rows.map(function (obj) {
                                            return [
                                                obj.status,
                                                obj.assetname,
                                                obj.assetip,
                                                obj.accountname,
                                                obj.assettypename,
                                                obj.assetgroupname,
                                                obj.trusteeship,
                                                obj.id,
                                            ]
                                        });
                                        return data;
                                    }
                                },
                                columns: [
                                    {
                                        name: 'username',//本列如果有排序或高级搜索，必须要有name
                                        head: '状态',
                                        align:'center',
                                        width:'80',
                                        title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell
                                        },
                                        render: function (cell, row, raw) {//自定义表格内容
                                            var html = '';
                                            if(cell == '0'){
                                                html += '<span class="auth-status-mark status-mark-wait">待审批</span>'
                                            }else if(cell =='1'){
                                                html += '<span class="auth-status-mark status-mark-auth">已授权</span>'
                                            }
                                            return html
                                        }
                                    },{
                                        name: 'username',//本列如果有排序或高级搜索，必须要有name
                                        head: '设备名称',
                                        title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell
                                        },
                                        render: function (cell, row, raw) {//自定义表格内容
                                            return cell;
                                        }
                                    },{
                                        name: 'username',//本列如果有排序或高级搜索，必须要有name
                                        head: '设备IP',
                                        title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell
                                        },
                                        render: function (cell, row, raw) {//自定义表格内容
                                            return cell;
                                        }
                                    },{
                                        name: 'username',//本列如果有排序或高级搜索，必须要有name
                                        head: '设备账号',
                                        title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell
                                        },
                                        render: function (cell, row, raw) {//自定义表格内容
                                            return cell;
                                        }
                                    },{
                                        name: 'username',//本列如果有排序或高级搜索，必须要有name
                                        head: '设备类型',
                                        title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell
                                        },
                                        render: function (cell, row, raw) {//自定义表格内容
                                            return cell;
                                        }
                                    },{
                                        name: 'username',//本列如果有排序或高级搜索，必须要有name
                                        head: '设备组',
                                        title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell
                                        },
                                        render: function (cell, row, raw) {//自定义表格内容
                                            return cell;
                                        }
                                    },{
                                        name: 'username',//本列如果有排序或高级搜索，必须要有name
                                        head: '账号权限',
                                        title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell
                                        },
                                        render: function (cell, row, raw) {//自定义表格内容
                                            return cell;
                                        }
                                    },
                                    {
                                        name: 'operate',
                                        head: '操作',
                                        align: 'center',
                                        width: 120,
                                        operates: [
                                            {
                                                icon:'icon-reset',
                                                text:'撤销',
                                                disabled: function (cell, row, raw) {
                                                    if(raw.status == '0'){
                                                        return false
                                                    }else{
                                                        return true
                                                    }
                                                },
                                                action:function(cell,row,raw){
                                                    var dom = gd.showConfirm({
                                                        id: 'resetAuthwind',
                                                        content: '确定要撤销吗?',
                                                        btn: [{
                                                            text: '确定',
                                                            class: 'gd-btn-danger',
                                                            enter: true,//响应回车
                                                            action: function (dom) {
                                                                $.ajax({
                                                                    url:baseUrl + "/granted/revokeGrantedById/"+ cell,
                                                                    type:"PUT",
                                                                    contentType:"application/json",//设置请求参数类型为json字符串
                                                                    dataType:"json",
                                                                    success:function(res){
                                                                        if(res.resultCode == '0'){
                                                                            gd.showSuccess('操作成功');
                                                                            gd.table('assetAuthlistTable').reload();
                                                                        }else{
                                                                            gd.showMsg(res.resultMsg, {time: 5000});
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }, {
                                                            text: '取消',
                                                            action: function () {
                                                               dom.close()
                                                            }
                                                        }],
                                                    });
                                                }
                                            },
                                            {
                                                icon: 'icon-delete',
                                                text: '删除',
                                                disabled: function (cell, row, raw) {
                                                    if(raw.status == '0'){
                                                        return true
                                                    }else{
                                                        return false
                                                    }
                                                },
                                                action: function (cell,row,raw) {
                                                    var dom = gd.showConfirm({
                                                        id: 'assetAuthwind',
                                                        content: '确定要删除吗?',
                                                        btn: [{
                                                            text: '删除',
                                                            class: 'gd-btn-danger',
                                                            enter: true,//响应回车
                                                            action: function (dom) {
                                                                $.ajax({
                                                                    url:baseUrl + "/granted/deleteById/"+ cell,
                                                                    type:"DELETE",
                                                                    contentType:"application/json",//设置请求参数类型为json字符串
                                                                    dataType:"json",
                                                                    success:function(res){
                                                                        if(res.resultCode == '0'){                                                                                       gd.showSuccess("授权设备成功！");
                                                                            gd.table('assetAuthlistTable').reload();
                                                                        }else{
                                                                            gd.showMsg(res.resultMsg, {time: 5000});
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }, {
                                                            text: '取消',
                                                            action: function () {
                                                               dom.close()
                                                            }
                                                        }],
                                                    });
                                                }
                                            }
                                        ]
                                    }
                                ]
                            },

                            tableAssetGroupConfig:{
                                id: 'assetGroupAuthlistTable',
                                length: 20,
                                curPage: 1,
                                lengthMenu: [10, 30, 50, 100],
                                enableJumpPage: false,
                                enableLengthMenu: true,
                                enablePaging: true,
                                columnResize: true,
                                fillBlank: '--',
                                ajax: {
                                    url: baseUrl + '/granted/getAssestgroupsInPage?usergroupId='+ cell,
                                    dataSrc: function (data) {
                                        data.rows = data.rows.map(function (obj) {
                                            return [
                                                obj.status,
                                                obj.assetgroupname,
                                                obj.id
                                            ]
                                        });
                                        return data;
                                    }
                                },
                                columns: [
                                    {
                                        name: 'status',
                                        head: '状态',
                                        align:'center',
                                        width:'120',
                                        title: function (cell, row, raw) {
                                            log(cell)
                                            return cell
                                        },
                                        render: function (cell, row, raw) {
                                            var html = '';
                                            if(cell == '0'){
                                                html += '<span class="auth-status-mark status-mark-wait">待审批</span>'
                                            }else if(cell =='1'){
                                                html += '<span class="auth-status-mark status-mark-auth">已授权</span>'
                                            }
                                            return html
                                        }
                                    },{
                                        name: 'assetgroupname',
                                        head: '设备组名称',
                                        title: function (cell, row, raw) {
                                            return cell
                                        },
                                        render: function (cell, row, raw) {
                                            return cell;
                                        }
                                    },
                                    {
                                        name: 'operate',
                                        head: '操作',
                                        align: 'center',
                                        width: 120,
                                        operates: [
                                            {
                                                icon: 'icon-document',
                                                text: '详情',
                                                action: function (cell,row,raw) {
                                                    gd.showLayer({
                                                        id: 'assetDetail_layer',
                                                        title: '设备详情',
                                                        content: $('#assetDetailBox').html(),
                                                        size: [860, 500],
                                                        btn: [],
                                                        success: function (dom) {
                                                            $("#assetDetail").show();
                                                            var assetDetail = new Vue({
                                                                el: '#assetDetail',
                                                                data: {
                                                                    tableAssetDetailConfig: {
                                                                        id: 'assetDetaillistTable',
                                                                        enableLengthMenu: true,
                                                                        enablePaging: false,
                                                                        columnResize: true,
                                                                        fillBlank: '--',
                                                                        ajax: {
                                                                            url: baseUrl + '/assetgroup/' + raw.assetgroupId + '/account',
                                                                            dataSrc: function (data) {
                                                                                data.rows = data.rows.map(function (obj) {
                                                                                    return [
                                                                                        obj.assetName,
                                                                                        obj.assetIp,
                                                                                        obj.username,
                                                                                        obj.assetTypeName,
                                                                                        obj.assetGroupName,
                                                                                        obj.trusteeshipName,
                                                                                    ]
                                                                                });
                                                                                return data;
                                                                            }
                                                                        },
                                                                        columns: [
                                                                            {
                                                                                name: 'assetname',
                                                                                head: '设备名称',
                                                                                width:'150',
                                                                                title: function (cell, row, raw) {
                                                                                    return cell
                                                                                },
                                                                                render: function (cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            },{
                                                                                name: 'status',
                                                                                head: '设备IP',
                                                                                align:'center',
                                                                                width:'120',
                                                                                title: function (cell, row, raw) {
                                                                                    return cell
                                                                                },
                                                                                render: function (cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            },{
                                                                                name: 'status',
                                                                                head: '设备账号',
                                                                                align:'center',
                                                                                title: function (cell, row, raw) {
                                                                                    return cell
                                                                                },
                                                                                render: function (cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            },{
                                                                                name: 'status',
                                                                                head: '设备类型',
                                                                                align:'center',
                                                                                title: function (cell, row, raw) {
                                                                                    return cell
                                                                                },
                                                                                render: function (cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            },{
                                                                                name: 'status',
                                                                                head: '设备组',
                                                                                align:'center',
                                                                                title: function (cell, row, raw) {
                                                                                    return cell
                                                                                },
                                                                                render: function (cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            },{
                                                                                name: 'status',
                                                                                head: '账号权限',
                                                                                align:'center',
                                                                                width:'120',
                                                                                title: function (cell, row, raw) {
                                                                                    return cell
                                                                                },
                                                                                render: function (cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            }
                                                                        ]
                                                                    }
                                                                }
                                                            })

                                                        },
                                                    })

                                                }
                                            },
                                            {
                                                icon:'icon-reset',
                                                text:'撤销',
                                                disabled: function (cell, row, raw) {
                                                    if(raw.status == '0'){
                                                        return false
                                                    }else{
                                                        return true
                                                    }
                                                },
                                                action:function(cell,row,raw){
                                                    var dom = gd.showConfirm({
                                                        id: 'resetAuthwind',
                                                        content: '确定要撤销吗?',
                                                        btn: [{
                                                            text: '确定',
                                                            class: 'gd-btn-danger',
                                                            enter: true,//响应回车
                                                            action: function (dom) {
                                                                $.ajax({
                                                                    url:baseUrl + "/granted/revokeGrantedById/"+ cell,
                                                                    type:"PUT",
                                                                    contentType:"application/json",//设置请求参数类型为json字符串
                                                                    dataType:"json",
                                                                    success:function(res){
                                                                        if(res.resultCode == '0'){
                                                                            gd.showSuccess('操作成功');
                                                                            gd.table('assetGroupAuthlistTable').reload();
                                                                        }else{
                                                                            gd.showMsg(res.resultMsg, {time: 5000});
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }, {
                                                            text: '取消',
                                                            action: function () {
                                                               dom.close()
                                                            }
                                                        }],
                                                    });
                                                }
                                            },
                                            {
                                                icon: 'icon-delete',
                                                text: '删除',
                                                disabled: function (cell, row, raw) {
                                                    if(raw.status == '0'){
                                                        return true
                                                    }else{
                                                        return false
                                                    }
                                                },
                                                action: function (cell,row,raw) {

                                                     var dom = gd.showConfirm({
                                                        id: 'assetGroupAuthwind',
                                                        content: '确定要删除吗?',
                                                        btn: [{
                                                            text: '删除',
                                                            class: 'gd-btn-danger',
                                                            enter: true,//响应回车
                                                            action: function (dom) {
                                                                $.ajax({
                                                                    url:baseUrl + "/granted/deleteById/"+ cell,
                                                                    type:"DELETE",
                                                                    contentType:"application/json",//设置请求参数类型为json字符串
                                                                    dataType:"json",
                                                                    success:function(res){
                                                                        if(res.resultCode == '0'){
                                                                            gd.table('assetGroupAuthlistTable').reload();
                                                                        }else{
                                                                            gd.showMsg(res.resultMsg, {time: 5000});
                                                                        }
                                                                    },

                                                                });
                                                            }
                                                        }, {
                                                            text: '取消',
                                                            action: function () {
                                                               dom.close()
                                                            }
                                                        }],
                                                    });



                                                }
                                            }
                                        ]
                                    }
                                ]
                            }


                        }
                    })
                }
            });
        }
    })
}

// 批量删除、删除
if(permissionSetArr.indexOf('usergroup::delete') > -1){
    permissionSetUserGroup.push({
        type: 'button',
        icon: 'icon-delete',
        title: '删除',
        disabled:true,
        action: function () {
            var allCheckedData = gd.table("usergrouplistTable").getCheckedData();
            if (allCheckedData.length == 0) {
                gd.showWarning('请至少选择一条记录', {
                    id:'warning'
                });
                return false;
            }
            // 暂存批量删除id
            var deleteIds = [];
            for(var i = 0; i < allCheckedData.length; i++) {
                deleteIds.push(allCheckedData[i][0]);
            }
            deleteIds = deleteIds.join(",");
            gd.showConfirm({
                id:'userGroupDeleteWindow',
                content:'确定要删除吗?',
                btn:[{
                    text:'删除',
                    class:'gd-btn-danger',
                    enter:true, // 响应回车
                    disabled:false,
                    action:function (dom) {
                        $.ajax({
                            url:baseUrl + "/usergroup/usergroup/delete?ids=" + deleteIds,
                            type:'DELETE',
                            success:function (data) {
                                if(data.resultCode =='0'){
                                    gd.showSuccess("删除成功");
                                    gd.table("usergrouplistTable").reload();
                                }else{
                                    gd.showError(data.resultMsg)
                                }
                            }
                        })
                    }
                }, {
                    text:'取消',
                    action:function () {
                    }
                }],
                end:function (dom) {

                }
            });
        }
    })

    permissionSetUserGroupTable.push( {
        icon: 'icon-delete',
        text: '删除',
        title: '删除',
        action: function (cell,row,raw) {
            var dom = gd.showConfirm({
                id: 'wind',
                content: '确定要删除吗?',
                btn: [{
                    text: '删除',
                    class: 'gd-btn-danger',
                    enter: true,//响应回车
                    action: function (dom) {
                        $.ajax({
                            url:baseUrl + "/usergroup/usergroup/"+ cell,
                            type:"DELETE",
                            contentType:"application/json",//设置请求参数类型为json字符串
                            dataType:"json",
                            success:function(res){
                                if(res.resultCode == '0'){
                                    gd.showSuccess("删除成功");
                                    gd.table('usergrouplistTable').reload();
                                }else{
                                    gd.showError(res.resultMsg);
                                }
                            },

                        });
                    }
                }, {
                    text: '取消',
                    action: function () {
                        dom.close()
                    }
                }],
            });
        }
    })
}


permissionSetUserGroup.push({
    type: 'searchbox',
    placeholder: "名称",
    action: function (val) {
        gd.table('usergrouplistTable').reload(false,{searchStr:val},false)
    }
})


var usergroupList = new Vue({
    el: '#contentDiv',
    methods: {
    },
    data: {
        groupId:'',
        groupName:'',
        groupBelong:'',
        groupDisabled:false,
        remark:'',
        treeWinConfig:{
            id: 'usergroupTree',
            accordion: true,
            linkable: false,
            accordion: false,
            data:[],
            onSelect: function(node){
                usergroupList.groupId = node.id;
                usergroupList.groupBelong = node.name;
                usergroupList.$refs.groupBelong.isDroped=false
            },
            onChange:function(node){
            },
            ready:function(data){
            }
        },
        toolbarConfig:permissionSetUserGroup,
        //表格配置
        tableConfig: {
            id: 'usergrouplistTable',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            fillBlank: '--',
            excludeSearchKey:['usergroupPid'],
            ajax: {
                url: baseUrl + '/usergroup/getUsergroupInPage',
                dataSrc: function (data) {
                    data.rows = data.rows.map(function (obj) {
                        return [
                            obj.id,
                            obj.name,
                            obj.pName,
                            obj.userCount,
                            obj.remark,
                            obj.id
                        ]
                    });
                    return data;
                },
                data: {
                    usergroupPid:1,
                    searchStr: ''
                }
            },
            columns: [
                {
                    name: 'checkbox',
                    type: 'checkbox',
                    width: '40', //列宽
                    align: 'center',//对齐方式，默认left，与class不同，class只影响内容，align会影响内容和表头
                    change: function (checkedData, checkedRawData) {//复选框改变，触发事件，返回所有选中的列的数据,第1个参数为加工后的表格数据，第2个参数为未加工的表格数据
                        setToolBtnDisable(usergroupList.toolbarConfig,'icon-delete',checkedData.length==0);
                    }
                },
                {
                    name: 'name',//本列如果有排序或高级搜索，必须要有name
                    head: '名称',
                    ellipsis: false,//可以禁用text ellipsis,默认为true
                    title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                        return cell
                    },
                    render: function (cell, row, raw) {//自定义表格内容
                        return cell;
                    }
                },
                {
                    name: 'name',
                    head: '所属组',
                    title: function (cell, row, raw) {//设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据
                        return cell
                    }
                },
                {
                    name: 'department',
                    head: '用户数'
                },
                {
                    name: 'authenticationMethod',
                    head: '备注',
                    title:true,
                },
                {
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    width: 120,
                    operates: permissionSetUserGroupTable
                }
            ]
        }

    }
})