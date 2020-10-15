var sysAccount_info;
var accountConfig;
var sysAccountListArr = [];
var sysRoleListArr = [];
var treeAllData = '';
var treeAssetGroupData = '';
var allowEdit = false;
var allowDelete = false;
var allowEditRole = false;
var allowDeleteRole = false;
var allowSaveRole = false;

// 权限
var permissionSetAccountConfig = [];//左侧导航

var permissionSetRole = []; //角色权限

// 角色权限
if(permissionSetArr.indexOf('role::all') > -1){
    allowEditRole = true;
    allowDeleteRole = true;
    allowSaveRole = true;
    permissionSetRole.push({
        type: 'button',
        icon: 'icon-add',
        title: '添加',
        action: function () {
            gd.showLayer({
                id: 'addRole_layer',
                title: '新建角色',
                content: $('#addRoleBox').html(),
                size: [550, 280],
                btn: [
                    {
                        text: '确定',
                        enter: true,//响应回车
                        action: function (dom) {
                            var validate = gd.validate('#roleForm', {});
                            var result = validate.valid();
                            if(result){
                                var addObject = $("#addRole_layer form").serializeJSON();
                                $.ajax({
                                    type: 'post',
                                    url: baseUrl + "/role/role",
                                    data: addObject,
                                    success: function (res) {
                                        gd.closeLoading();
                                        if(!res.resultCode){
                                            gd.showSuccess("操作成功！");
                                            console.log(res.data);
                                            getSysRoleList('repeat',res.data)
                                        }else{
                                            gd.showError(res.resultMsg);
                                        }
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
                success: function (dom) {
                    sysAccount_info = new Vue({
                        el: '#addRole',
                        methods: {},
                        // mounted:function (){
                       //      $.get(baseUrl + '/userAssetAssetgroup/getSystemUserRoleTypes',function (res) {
                       //       console.log(res);
                       //          $.each(res.data,function (index,item) {
                       //              sysAccount_info.sysAccountTypeArr.push({
                       //                  guid:item.guid,
                       //                  name:item.name
                       //              })
                       //          })
                       //          sysAccount_info.roleType = sysAccount_info.sysAccountTypeArr[0].guid
                       //      })
                       //  },
                        data: {
                            name:'',
                        }
                    })


                }
            });
        }
    })
}

// 新建
if(permissionSetArr.indexOf('accountConfig::save') > -1){
    // 左侧添加
    permissionSetAccountConfig.push({
                type: 'button',
                icon: 'icon-add',
                title: '添加',
                action: function () {
                    var validate = '';
                    gd.showLayer({
                        id: 'addSysAccount_layer',
                        title: '新建账户',
                        content: $('#addSysAccountBox').html(),
                        size: [550, 380],
                        btn: [
                            {
                                text: '确定',
                                enter: true,//响应回车
                                action: function (dom) {//参数为当前窗口dom对象
                                    var result = validate.valid();//获得整体的校验结果

                                    if(result){
                                        var addObject = $("#addSysAccount_layer form").serializeJSON();
                                        addObject.password = encrypt(addObject.password).toUpperCase();

                                        $.ajax({
                                            type: 'post',
                                            url: baseUrl + "/system/accountConfig/saveSystemUser",
                                            data: addObject,
                                            success: function (res) {
                                                gd.closeLoading();
                                                if(!res.resultCode){
                                                    gd.showSuccess("操作成功！");
                                                    getSysAccountList('repeat',res.data);
                                                }else{
                                                    gd.showError(res.resultMsg);
                                                }
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
                        success: function (dom) {
                            sysAccount_info = new Vue({
                                el: '#addSysAccount',
                                methods: {},
                                mounted:function (){
                                    $.get(baseUrl + '/userAssetAssetgroup/getSystemUserRoleTypes',function (res) {
                                        $.each(res.data,function (index,item) {
                                            sysAccount_info.sysAccountTypeArr.push({
                                                guid:item.guid,
                                                name:item.name
                                            })
                                        })
                                        sysAccount_info.roleType = sysAccount_info.sysAccountTypeArr[0].guid
                                    })
                                },
                                data: {
                                    sysAccountTypeArr:[],
                                    name:'',
                                    username:'',
                                    roleType:'',
                                    password:'',
                                    disabledStatus:false,
                                    pwLength:''
                                }
                            })

                            // 密码配置
                            gd.get(baseUrl + '/system/systemSet/platform/security', function(msg) {
                                if (msg.resultCode == 0) {
                                    sysAccount_info.pwLength = msg.data.minLength;
                                    validate = gd.validate('#addSysAccount_layer form', {
                                        rules: [
                                            {
                                                name: 'hasNumber',
                                                msg: '必须包含数字',
                                                valid: function(value, el) {
                                                    if (msg.data.number) {
                                                        return value == '' || /[0-9]/.test(value);
                                                    } else {
                                                        return true;
                                                    }
                                                }
                                            },
                                            {
                                                name: 'hasLowerCase',
                                                msg: '必须包含小写字母',
                                                valid: function(value, el) {
                                                    if (msg.data.lowercase) {
                                                        return value == '' || /[a-z]/.test(value);
                                                    } else {
                                                        return true;
                                                    }
                                                }
                                            },
                                            {
                                                name: 'hasCapital',
                                                msg: '必须包含大写字母',
                                                valid: function(value, el) {
                                                    if (msg.data.capital) {
                                                        return value == '' || /[A-Z]/.test(value);
                                                    } else {
                                                        return true;
                                                    }
                                                }
                                            }
                                        ]
                                    });
                                } else {
                                    gd.showError('数据加载失败！');
                                }
                            });

                        }
                    });
                }
    })
}

// 修改
if(permissionSetArr.indexOf('accountConfig::update') > -1){
    allowEdit = true
}

// 删除
if(permissionSetArr.indexOf('accountConfig::delete') > -1){
    allowDelete = true;
}

permissionSetAccountConfig.push({
    type: 'searchbox',
    action: function (val) {
        getSysAccountList('repeat','',val)
    }
})


// 系统账号列表
function getSysAccountList(repeat,dataId,searchStr){
	sysAccountListArr = [];
    var url = baseUrl + '/userAssetAssetgroup/getSystemUsers';
    if(searchStr){
        url = baseUrl + '/userAssetAssetgroup/getSystemUsers?searchStr='+ searchStr;
    }
  	$.get(url,function (res) {
        var indexNum = 0;
        $.each(res.data,function (index,item) {
            sysAccountListArr.push({
                guid:item.guid,
                roleType:item.roleType,
                username:item.username,
                name:item.name
            })
            if(dataId && item.guid == dataId){
                indexNum = index
            }
        })

        if(repeat){
        	accountConfig.sysAccountListArr = sysAccountListArr
        }
        accountConfig.sysAccountListArr = sysAccountListArr

        if(dataId){
            accountConfig.$nextTick(function(){
                accountConfig.sysAccountChange(dataId,indexNum);
            })
        }else{
            accountConfig.sysAccountChange(sysAccountListArr[0].guid,indexNum);
        }
    })
}

// 角色列表
function getSysRoleList(repeat,dataId){
	sysRoleListArr = [];
	gd.get(baseUrl + '/role/getRoleList',function (res) {
        var indexNum = 0;
        $.each(res.data,function (index,item) {
            sysRoleListArr.push({
                icon:item.icon,
                guid:item.guid,
                name:item.name,
                type:item.type
            })
            if(item.guid == dataId){
                indexNum = index
            }
        })

        if(repeat){
        	accountConfig.sysRoleListArr = sysRoleListArr
        }

        accountConfig.sysRoleListArr = sysRoleListArr;

        if(dataId){
            accountConfig.$nextTick(function(){
                accountConfig.sysRoleChange(dataId,indexNum);
            })
        }else{
            accountConfig.sysRoleChange(sysRoleListArr[0].guid,indexNum,1);
        }

    })
}

// 右侧权限列表显示, 第一级parentList传{}
function roleInfo(list, parentList, n) {
    if ($.isEmptyObject(parentList)) {
        parentList.id = 1;
    }
    var content = '';
    $.each(list, function(index, obj) {
        if(obj.type == 1) {
            content = '<div class="navigation-row navigation-level'+ n +'" id="">';
            if (obj.sub && obj.sub.length != 0) {
                content += '<label class="gd-checkbox"><input type="checkbox" id="check_'+ n +'_1_'+ obj.id +'" ';
            } else {
                content += '<label class="gd-checkbox"><input type="checkbox" id="check_'+ n +'_0_'+ obj.id +'" ';
            }
            if (obj.parentId) {
                content += 'data-pid="'+ obj.parentId +'" data-type="'+ obj.type +'" type="checkbox" class="navigation-check"';
            } else {
                content += 'data-pid="-1" data-type="'+ obj.type +'" type="checkbox" class="navigation-check"';
            }

            if(obj.checked == true ){ // 是否选中
                content += 'checked ';
            }
            content += '/><i></i></label>'+
                    '<span class="iconfont '+ obj.icon +'"></span>'+
                    '<label class="navigation-label">'+ obj.name +'</label>'+
                    '</div>';
            $('#navigation_box').append(content);
        } else {
            if (index == 0) {
                // 操作权限显示
                roleInfoOper(obj, list, n);
            }
        }

        // 若有子级页面，递归调用
        if(obj.sub && obj.sub.length != 0) {
            roleInfo(obj.sub, obj, n + 1);
        }
    });
}

// 操作权限显示
function roleInfoOper(list, parentList, n) {
    var content = '<div class="navigation-row navigation-level'+ n +'" id="">';
    if(!$.isEmptyObject(parentList)) {
        $.each(parentList, function(index, obj) {
            content += '<div class="navigation-row0">'+
                        '<label class="gd-checkbox"><input type="checkbox" id="check_'+ n +'_0_'+ obj.id +'" data-pid="'+ obj.navigationId +'" data-type="'+ obj.type +'" class="navigation-check"';
            if(obj.checked == true ){
                content += 'checked ';
            }
            content += '/><i></i></label>'+
                        '<label class="navigation-label">'+ obj.description +'</label>'+
                        '</div>';
        });
    }
    content += '</div>';
    $('#navigation_box').append(content);
}

accountConfig = new Vue({
    el: '#contentDiv',
    methods: {
        sysAccountChange:function(dataId,index){
            if(dataId == 2 || dataId ==3 || dataId ==4 ){
                return false
            }
            $("#permissionUser_list_box li").removeClass('active');
            $("#permissionUser_list_box li").eq(index).addClass('active');

            $("#assetAuth,#auditAuth").html('');

            var permissionSetAssetBar = []; //设备
            var permissionSetAssetTable = []; //设备
            var permissionSetAssetGroupBar = [];//设备组
            var permissionSetAssetGroupTable = []; //设备组
            var permissionSetApprovalBar = []; //审计
            var permissionSetApprovalTable = []; //审计

            $.ajax({
                type: 'get',
                url: baseUrl + "/userAssetAssetgroup/detail/"+dataId,
                success: function (res) {
                    if(res.resultCode == 0){

                        // 添加
                        if(permissionSetArr.indexOf('accountConfig::save') > -1){

                            // 设备添加
                            permissionSetAssetBar.push({
                                type: 'button',
                                icon: 'icon-add',
                                title: '添加',
                                action: function () {
                                    $.ajax({
                                        url: baseUrl + '/userAssetAssetgroup/getAssetListTreeByUserGuid?userGuid='+dataId,
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
                                                    text: '确定',
                                                    enter: true,//响应回车
                                                    action: function (dom) {//参数为当前窗口dom对象
                                                        var newAssetArr = [];
                                                        var oldAssetArr = authorizedAssetAdd.assetArr;
                                                        for(var i=0;i< oldAssetArr.length;i++){
                                                            var oldAssetSplit = oldAssetArr[i].split('-');
                                                            newAssetArr.push(oldAssetSplit[1]);
                                                        }
                                                        $.ajax({
                                                            url: baseUrl + '/userAssetAssetgroup/save/asset',
                                                            type:'post',
                                                            data:{'userGuid':dataId,'assetIds':newAssetArr.join(',')},
                                                            success:function(res){
                                                                if(res.resultCode == 0){
                                                                    gd.showSuccess('添加成功');
                                                                    gd.table('assetListTable').reload()
                                                                }
                                                            }
                                                        })
                                                    }
                                                }, {
                                                    text: '取消',
                                                    action: function () {
                                                    }
                                                }],
                                                success: function (dom) {
                                                    authorizedAssetAdd = new Vue({
                                                        el:'#authorizedAssetAdd',
                                                        data:{
                                                            assetList:[],
                                                            assetAuthListLen:0,
                                                            treeAuthConfig:{
                                                                id: 'authorizedTree',
                                                                accordion: true,
                                                                data: treeAllData,
                                                                showCheckBox: true,
                                                                linkable: true,
                                                                onSelect: function (node) {
                                                                    console.log('ewerwerw');
                                                                    console.log(node)
                                                                },
                                                                onChange: function (nodes) {
                                                                    console.log(nodes);
                                                                    authorizedAssetAdd.assetList = [];
                                                                    authorizedAssetAdd.assetArr = [];
                                                                    nodes.map(function(item,index){
                                                                        if(item.id.indexOf('asset')>-1){
                                                                            if(!item.ignore){
                                                                                var assetCon = gd.tree('authorizedTree').getNode(item.pId);
                                                                                var obj = {
                                                                                    assetGroupName:assetCon.name,
                                                                                    assetName:item.name
                                                                                }
                                                                                authorizedAssetAdd.assetArr.push(item.id);
                                                                                authorizedAssetAdd.assetList.push(obj);
                                                                            }
                                                                        }
                                                                    })
                                                                    authorizedAssetAdd.assetAuthListLen = authorizedAssetAdd.assetList.length;
                                                                },
                                                                ready: function (nodes) {
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
                            });

                            // 设备组添加
                            permissionSetAssetGroupBar.push({
                                type: 'button',
                                icon: 'icon-add',
                                title: '添加',
                                action: function () {
                                    // 设备组树
                                    $.ajax({
                                        url: baseUrl + '/userAssetAssetgroup/getAssetgroupListTreeByUserGuid?userGuid='+dataId,
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
                                                    text: '确定',
                                                    enter: true,//响应回车
                                                    action: function (dom) {//参数为当前窗口dom对象
                                                        $.ajax({
                                                            url: baseUrl + '/userAssetAssetgroup/save/assetGroup',
                                                            type:'post',
                                                            data:{'userGuid':dataId,'assetGroupIds':authorizedAssetGroupAdd.assetGroupArr.join(',')},
                                                            success:function(res){
                                                                if(res.resultCode == 0){
                                                                    gd.showSuccess('添加成功');
                                                                    gd.table('assetGroupListTable').reload()
                                                                }
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
                            });

                            // 审计添加
                            permissionSetApprovalBar.push({
                                type: 'button',
                                icon: 'icon-add',
                                title: '添加',
                                action: function () {
                                    // 设备组树
                                    $.ajax({
                                        url: baseUrl + '/userAssetAssetgroup/auditList/'+dataId,
                                        dataType: "json",
                                        success: function (data) {
                                            var waitAccountVue;
                                            var dataIdArr = [];
                                            gd.showLayer({
                                                id: 'auditAdd_layer',
                                                title: '添加操作员',
                                                content: $('#addAuditAuthBox').html(),
                                                size: [740, 530],
                                                btn: [{
                                                    text: '确定',
                                                    enter: true,//响应回车
                                                    action: function (dom) {//参数为当前窗口dom对象
                                                        $.ajax({
                                                            url: baseUrl + '/userAssetAssetgroup/save/audit',
                                                            type:'post',
                                                            data:{'userGuid':dataId,'operatorIds':dataIdArr.join(',')},
                                                            success:function(res){
                                                                if(res.resultCode == 0){
                                                                    gd.showSuccess('添加成功');
                                                                    gd.table('auditListTable').reload()
                                                                }
                                                            }
                                                        })
                                                    }
                                                }, {
                                                    text: '取消',
                                                    action: function () {
                                                    }
                                                }],
                                                success: function (dom) {
                                                    $.ajax({
                                                        url:baseUrl + '/userAssetAssetgroup/auditList/'+dataId,
                                                        type:'get',
                                                        success:function(res){
                                                            waitAccountVue =  new Vue({
                                                                el:'#addAuditAuth',
                                                                methods:{
                                                                    accountChange: function(dataId){
                                                                        dataIdArr = [];
                                                                        waitAccountVue.selectAccountArr = [];
                                                                        $(".nonSelect-ul li").each(function(){
                                                                            if($(this).find('input').is(':checked') &&  !$(this).find('input').is(':disabled')){
                                                                                var dataId = $(this).attr('data-id');
                                                                                var dataName = $(this).attr('data-name');
                                                                                dataIdArr.push(dataId);
                                                                                var obj = {
                                                                                    id: dataId ,
                                                                                    name: dataName
                                                                                }
                                                                                waitAccountVue.selectAccountArr.push(obj);
                                                                            }
                                                                        })

                                                                        waitAccountVue.auditAuthListLen = waitAccountVue.selectAccountArr.length;
                                                                    }
                                                                },
                                                                data:{
                                                                    waitAccountArr:res.data,
                                                                    selectAccountArr:[],
                                                                    auditAuthListLen:0
                                                                }
                                                            })
                                                        }
                                                    })
                                                }
                                            })
                                        }
                                    })
                                }
                            });
                        }

                        // 删除
                        if(permissionSetArr.indexOf('accountConfig::delete') > -1){
                            // 设备组批量删除
                            permissionSetAssetGroupBar.push({
                                type: 'button',
                                icon: 'icon-delete',
                                title: '删除',
                                action: function () {
                                    var allCheckedData = gd.table("assetGroupListTable").getCheckedData();
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
                                        id:'userDeleteWindow',
                                        content:'确定要删除吗?',
                                        btn:[{
                                            text:'删除',
                                            class:'gd-btn-danger',
                                            enter:true, // 响应回车
                                            disabled:false,
                                            action:function (dom) {
                                                $.ajax({
                                                    url:baseUrl + "/userAssetAssetgroup/delete/assetGroup?userGuid="+dataId+"&assetGroupIds="+deleteIds,
                                                    type:'DELETE',
                                                    success:function (data) {
                                                        gd.showSuccess("删除成功");
                                                        gd.table("assetGroupListTable").reload(1);
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

                            // 设备组单个删除
                            permissionSetAssetGroupTable.push({
                                icon: 'icon-delete',
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
                                                    url:baseUrl + "/userAssetAssetgroup/delete/assetGroup?userGuid="+dataId+"&assetGroupIds="+cell,
                                                    type:'DELETE',
                                                    success:function (data) {
                                                        gd.showSuccess("删除成功");
                                                        gd.table("assetGroupListTable").reload(1);
                                                    }
                                                })
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

                            // 设备批量删除
                            permissionSetAssetBar.push({
                                type: 'button',
                                icon: 'icon-delete',
                                title: '删除',
                                action: function () {
                                    var allCheckedData = gd.table("assetListTable").getCheckedData();
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
                                        id:'userDeleteWindow',
                                        content:'确定要删除吗?',
                                        btn:[{
                                            text:'删除',
                                            class:'gd-btn-danger',
                                            enter:true, // 响应回车
                                            disabled:false,
                                            action:function (dom) {
                                                $.ajax({
                                                    url:baseUrl + "/userAssetAssetgroup/delete/asset?userGuid=" + dataId + "&assetIds=" + deleteIds,
                                                    type:'DELETE',
                                                    success:function (data) {
                                                        gd.showSuccess("删除成功");
                                                        gd.table("assetListTable").reload(1);
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
                            // 审计批量删除
                            permissionSetApprovalBar.push({
                                type: 'button',
                                icon: 'icon-delete',
                                title: '删除',
                                action: function () {
                                    var allCheckedData = gd.table("auditListTable").getCheckedData();
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
                                        id:'userDeleteWindow',
                                        content:'确定要删除吗?',
                                        btn:[{
                                            text:'删除',
                                            class:'gd-btn-danger',
                                            enter:true, // 响应回车
                                            disabled:false,
                                            action:function (dom) {
                                                $.ajax({
                                                    url:baseUrl + "/userAssetAssetgroup/delete/audit?userGuid="+dataId+"&operatorIds="+deleteIds,
                                                    type:'DELETE',
                                                    success:function (data) {
                                                        gd.showSuccess("删除成功");
                                                        gd.table("auditListTable").reload();
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

                            // 审计单个删除
                            permissionSetApprovalTable.push({
                                icon: 'icon-delete',
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
                                                    url:baseUrl + "/userAssetAssetgroup/delete/audit?userGuid="+dataId+"&operatorIds="+cell,
                                                    type:'DELETE',
                                                    success:function (data) {
                                                        gd.showSuccess("删除成功");
                                                        gd.table("auditListTable").reload();
                                                    }
                                                })
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

                        // 设备权限
                        if(res.data.assetPermission != null){
                            $("#assetAuth").html($("#assetAuthHtml").html());

                            var assetAuthVue = new Vue({
                                el:'#assetAuthHtmlBox',
                                data:{
                                    assetListArr:[],
                                    assetGroupListArr:[],
                                    toolbarAssetConfig:permissionSetAssetBar,
                                    toolbarAssetGroupConfig:permissionSetAssetGroupBar,

                                    //设备table
                                    assetTableConfig: {
                                        id: 'assetListTable',
                                        length: 20,
                                        curPage: 1,
                                        lengthMenu: [10, 30, 50, 100],
                                        enableJumpPage: false,
                                        enableLengthMenu: true,
                                        enablePaging: false,
                                        columnResize: true,
                                        filterImmediately: true,
                                        fillBlank: '--',
                                        ajax: {
                                            //其它ajax参数同jquery
                                           url: baseUrl + '/userAssetAssetgroup/detail/asset/'+ dataId,
                                            //改变从服务器返回的数据给table
                                            dataSrc: function (data) {
                                                data.rows = data.rows.map(function (obj) {
                                                    return [
                                                        obj.id,
                                                        obj.icon,
                                                        obj.name,
                                                        obj.ip,
                                                        obj.groupName,
                                                        obj.id
                                                    ]
                                                });
                                                return data;
                                            }
                                        },
                                        columns: [
                                            {
                                                name: 'checkbox',
                                                type: 'checkbox',
                                                width: '80', //列宽
                                                align: 'center',
                                            },
                                            {
                                                name: 'name',
                                                head: '类型',
                                                width:'150',
                                            },
                                            {
                                                name: 'department',
                                                head: '设备名称',
                                            },
                                            {
                                                name: "lastLoginTime",
                                                head: '设备IP',
                                                width:'180',
                                            },
                                            {
                                                name: "lastLoginTime",
                                                head: '设备组',
                                            },
                                            {
                                                name: 'operate',
                                                head: '操作',
                                                align: 'center',
                                                width: 200,
                                                operates:permissionSetAssetTable
                                            }
                                        ]
                                    },

                                    // 设备组table
                                    assetGroupTableConfig: {
                                        id: 'assetGroupListTable',
                                        length: 20,
                                        curPage: 1,
                                        lengthMenu: [10, 30, 50, 100],
                                        enableJumpPage: false,
                                        enableLengthMenu: true,
                                        enablePaging: false,
                                        columnResize: true,
                                        fillBlank: '--',
                                        ajax: {
                                            //其它ajax参数同jquery
                                            url:baseUrl + '/userAssetAssetgroup/detail/assetGroup/'+ dataId,
                                            //改变从服务器返回的数据给table
                                            dataSrc: function (data) {
                                                data.rows = data.rows.map(function (obj) {
                                                    return [
                                                        obj.id,
                                                        obj.name,
                                                        obj.pname,
                                                        obj.assetCount,
                                                        obj.id
                                                    ]
                                                });
                                                return data;
                                            }
                                        },
                                        columns: [
                                            {
                                                name: 'checkbox',
                                                type: 'checkbox',
                                                width: '40', //列宽
                                                align: 'center',
                                            },
                                            {
                                                name: 'name',
                                                head: '名称',
                                                width:'150',
                                            },
                                            {
                                                name: 'department',
                                                head: '所属组',
                                            },
                                            {
                                                name: "lastLoginTime",
                                                head: '设备数',
                                                width:'180',
                                            },
                                            {
                                                name: 'operate',
                                                head: '操作',
                                                align: 'center',
                                                width: 200,
                                                operates: permissionSetAssetGroupTable
                                            }
                                        ]
                                    }

                                }
                            })
                        }

                        // 审计权限
                        if(res.data.auditPermission != null){
                            $("#auditAuth").html($("#auditAuthHtml").html());
                            var auditAuthVue = new Vue({
                                el:'#auditAuthHtmlBox',
                                data:{
                                    toolbarAuditConfig:permissionSetApprovalBar,

                                    //表格配置
                                    auditTableConfig: {
                                        id: 'auditListTable',
                                        length: 20,
                                        curPage: 1,
                                        lengthMenu: [10, 30, 50, 100],
                                        enableJumpPage: false,
                                        enableLengthMenu: true,
                                        enablePaging: false,
                                        columnResize: true,
                                        filterImmediately: true,
                                        fillBlank: '--',
                                        ajax: {
                                            //其它ajax参数同jquery
                                           url: baseUrl + '/userAssetAssetgroup/detail/audit/'+ dataId,
                                            //改变从服务器返回的数据给table
                                            dataSrc: function (data) {

                                                data.rows = data.rows.map(function (obj) {
                                                    return [
                                                        obj.id,
                                                        obj.username,
                                                        obj.name,
                                                        obj.roleName,
                                                        obj.id
                                                    ]
                                                });
                                                return data;
                                            }
                                        },
                                        columns: [
                                            {
                                                name: 'checkbox',
                                                type: 'checkbox',
                                                width: '80', //列宽
                                                align: 'center',
                                                change: function () {
                                                    log(checkedData)
                                                }
                                            },
                                            {
                                                name: 'name',
                                                head: '系统账号',
                                                title: function (cell, row, raw) {
                                                    return cell
                                                }
                                            },
                                            {
                                                name: 'department',
                                                head: '名称',
                                                title: function (cell, row, raw) {
                                                    return cell
                                                }
                                            },
                                            {
                                                name: "lastLoginTime",
                                                head: '角色',
                                            },
                                            {
                                                name: 'operate',
                                                head: '操作',
                                                align: 'center',
                                                width: 200,
                                                operates: permissionSetApprovalTable
                                            }
                                        ]
                                    }
                                }
                            })
                        }

                    }
                },
                error: function (xhr, errorText, errorStatus) {
                }
            })

        },
        editSysAccount:function(data){
            var validate = '';
            gd.showLayer({
                id: 'editSysAccount_layer',
                title: '编辑账号',
                content: $('#addSysAccountBox').html(),
                size: [550, 380],
                btn: [
                    {
                        text: '确定',
                        enter: true,//响应回车
                        action: function (dom) {//参数为当前窗口dom对象
                            var result = validate.valid();//获得整体的校验结果

                            if(result){
                                var addObject = $("#editSysAccount_layer form").serializeJSON();

                                if(addObject.password){
                                    addObject.password = encrypt(addObject.password).toUpperCase();
                                }

                                $.ajax({
                                    type: 'post',
                                    url: baseUrl + "/system/accountConfig/editSystemUser?guid="+data,
                                    data: addObject,
                                    success: function (res) {
                                        gd.closeLoading();
                                        if(!res.resultCode){
                                            gd.showSuccess("操作成功！");
                                            getSysAccountList('repeat',res.data);
                                        }else{
                                            gd.showError(res.resultMsg);
                                        }
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
                success: function (dom) {
                    var obj = {};
                    sysAccountListArr.forEach(function(item,index){
                        if(item.guid == data){
                            obj = {
                                name:item.name,
                                username:item.username,
                                roleType:item.roleType,
                                guid:item.guid
                            }
                        }
                    })

                    $("input[name=username]").prop('disabled',true);

                    sysAccount_info = new Vue({
                        el: '#addSysAccount',
                        methods: {},
                        mounted:function (){
                            $.get(baseUrl + '/userAssetAssetgroup/getSystemUserRoleTypes',function (res) {
                                $.each(res.data,function (index,item) {
                                    sysAccount_info.sysAccountTypeArr.push({
                                        guid:item.guid,
                                        name:item.name
                                    })
                                })
                            })
                        },
                        data: {
                            sysAccountTypeArr:[],
                            name:obj.name,
                            username:obj.username,
                            roleType:obj.roleType,
                            password:'',
                            disabledStatus:true,
                            pwLength:'',
                        }
                    })

                      // 密码配置
                    gd.get(baseUrl + '/system/systemSet/platform/security', function(msg) {
                        if (msg.resultCode == 0) {
                            sysAccount_info.pwLength = msg.data.minLength;
                            validate = gd.validate('#addSysAccount_layer form', {
                                rules: [
                                    {
                                        name: 'hasNumber',
                                        msg: '必须包含数字',
                                        valid: function(value, el) {
                                            if (msg.data.number) {
                                                return value == '' || /[0-9]/.test(value);
                                            } else {
                                                return true;
                                            }
                                        }
                                    },
                                    {
                                        name: 'hasLowerCase',
                                        msg: '必须包含小写字母',
                                        valid: function(value, el) {
                                            if (msg.data.lowercase) {
                                                return value == '' || /[a-z]/.test(value);
                                            } else {
                                                return true;
                                            }
                                        }
                                    },
                                    {
                                        name: 'hasCapital',
                                        msg: '必须包含大写字母',
                                        valid: function(value, el) {
                                            if (msg.data.capital) {
                                                return value == '' || /[A-Z]/.test(value);
                                            } else {
                                                return true;
                                            }
                                        }
                                    }
                                ]
                            });
                        } else {
                            gd.showError('数据加载失败！');
                        }
                    });

                    $("#userType input").prop('disabled',true);
                }
            });
        },

        deleteAccount:function(dataId,e){
            // e.stopPropagation();
            gd.showConfirm({
                id:'DeleteWindow',
                content:'确定要删除吗?',
                btn:[{
                    text:'删除',
                    class:'gd-btn-danger',
                    enter:true, // 响应回车
                    disabled:false,
                    action:function (dom) {
                        $.ajax({
                            type: 'DELETE',
                            url: baseUrl + "/system/accountConfig/" + dataId,
                            success: function (res) {
                                gd.closeLoading();
                                if(!res.resultCode){
                                    gd.showSuccess("删除成功！");
                                    getSysAccountList('repeat');
                                }else{
                                    gd.showError(res.resultMsg);
                                }
                            },
                            error: function (xhr, errorText, errorStatus) {
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
        },

        sysRoleChange:function(data,index,type){

                console.log($("#permission_list_box li"));
                if(type && type == 1){
                    $("#save_navigation_box").hide();
                    $(".navigation-box").css('height','100%');
                }else{
                    $("#save_navigation_box").show();
                    $(".navigation-box").css('height','calc(100% - 75px)');
                }
                if(data){
                    $('#navigation_box').html('');
                    $.get(baseUrl + '/permission/findPermissionsByRoleGuid/'+ data,function (res) {
                        if (res.resultCode == 0) {
                            roleInfo(res.data, {}, 1);
                            if(type && type == 1){
                                $("#navigation_box input").prop('disabled',true)
                            }
                        } else {
                        }
                    })
                    $("#permission_list_box li").removeClass('active');
                    $("#permission_list_box li").eq(index).addClass('active');
                }
        },

        editRole:function(dataId,dataName){
            gd.showLayer({
                id: 'editRole_layer',
                title: '编辑角色',
                content: $('#addRoleBox').html(),
                size: [550, 280],
                btn: [
                    {
                        text: '确定',
                        enter: true,//响应回车
                        action: function (dom) {
                            var validate = gd.validate('#roleForm', {});
                            var result = validate.valid();
                            if(result){
                                var addObject = $("#editRole_layer form").serializeJSON();
                                $.ajax({
                                    type: 'put',
                                    url: baseUrl + "/role/role?guid=" + dataId,
                                    data: addObject,
                                    success: function (res) {
                                        gd.closeLoading();
                                        if(!res.resultCode){
                                            gd.showSuccess("操作成功！");
                                            getSysRoleList('repeat',dataId)
                                        }else{
                                            gd.showError(res.resultMsg);
                                        }
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
                success: function (dom) {
                    sysAccount_info = new Vue({
                        el: '#addRole',
                        methods: {},
                        data: {
                            name:dataName,
                        }
                    })
                }
            })
        },

        deleteRole:function(dataId,e){
            // e.stopPropagation();
            gd.showConfirm({
                id:'DeleteWindow',
                content:'确定要删除吗?',
                btn:[{
                    text:'删除',
                    class:'gd-btn-danger',
                    enter:true, // 响应回车
                    disabled:false,
                    action:function (dom) {
                        $.ajax({
                            type: 'DELETE',
                            url: baseUrl + "/role/role/" + dataId,
                            success: function (res) {
                                gd.closeLoading();
                                if(!res.resultCode){
                                    gd.showSuccess("删除成功！");
                                    getSysRoleList('repeat')
                                }else{
                                    gd.showError(res.resultMsg);
                                }
                            },
                            error: function (xhr, errorText, errorStatus) {
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
        },

        saveNavigation:function(){
            var roleId = $('#permission_list_box li.active').attr('data-id');
            var leftIndex = $('#permission_list_box li.active').index();
            var navigationList = [];
            $('.navigation-check').each(function() {
                if ($(this).is(':checked')) {
                    var navigationId = $(this).attr('id').split('_')[3];
                    var type = $(this).attr('data-type');
                    var item = {
                        type: type,
                        id: navigationId
                    }
                    navigationList.push(item);
                }
            });

            if(navigationList.length == 0){
                gd.showError('请至少勾选一个页签！');
                return false;
            }
            var postData = {
                guid: roleId,
                list: navigationList
            }
            $.ajax({
                url:baseUrl + '/role/rolePermissions',
                type:'post',
                data:JSON.stringify(postData),
                contentType:'application/json',
                success:function(msg){
                    if (msg.resultCode == 0) {
                        gd.showSuccess('保存成功');
                        getSysRoleList('repeat',roleId)
                    } else {
                        gd.showError('保存失败');
                    }
                }
            })
        }
    },
    mounted:function (){
    },
    data:{
        // 角色
        sysRoleListArr:sysRoleListArr,
        allowEditRole: allowEditRole,
        allowDeleteRole: allowDeleteRole,
        // 系统账号
        allowEdit:allowEdit,
        allowDelete:allowDelete,
        sysAccountListArr :sysAccountListArr,
        assetAuthHtml:'',
        auditAuthHtml:'',
        sysRoleTopbar:permissionSetRole,
        sysAccountTopbar:permissionSetAccountConfig,
    },
});

getSysRoleList();

getSysAccountList();


/**
 * 权限列表，上下级 级联选择, 全选or全不选
 * arr=['check', '1', '1', '4']
 * arr[1]: 层级level
 * arr[2]: 是否有子级
 * arr[3]: 自身id
 */
function cascadingSelect(arr, isChecked) {
    $('.navigation-check').each(function() {
        var pid = $(this).attr('data-pid');
        if(pid == arr[3] && $(this).is(':disabled') == false) {
            $(this).prop('checked', isChecked);
            var secArr = $(this).attr('id').split('_');
            if(secArr[2] == '1'){
                cascadingSelect(secArr, isChecked);
            }
        }
    })
}

/**
 * 权限列表，同级 级联上级, 全不选级联； 选中一个级联
 * powerType: 2(操作权限)， 1(页面权限)
 */
function peerSelect(powerType, pid) {
    var selectArr = [];
    $('.navigation-check').each(function() {
        var peerPid = $(this).attr('data-pid');
        var type = $(this).attr('data-type');
        if (peerPid == pid && type == powerType) {
            selectArr.push($(this).is(':checked'));
        }
    });
    if (selectArr.length == 1) { // 子级全不选时，父级不选中
        if(selectArr[0] == false) {
            $('.navigation-check').each(function() {
                var id = $(this).attr('id').split('_')[3];
                var type = $(this).attr('data-type');
                if (id == pid && type == 1 && powerType == 1) {
                    $(this).click();
                }
            });
        }
    } else { // 子级有一个选中时，父级选中
        var count = 0;
        for (var i = 0; i < selectArr.length; i++) {
            if(selectArr[i] == true) {
                count++;
            }
        }
        console.log(count);

        if (count >= 1) {
            $('.navigation-check').each(function() {
                var id = $(this).attr('id').split('_')[3];
                var type = $(this).attr('data-type');
                if (id == pid && type == 1) {
                    $(this).prop('checked', true);
                    var thdPid = $(this).attr('data-pid');
                    if (thdPid !== -1) { // 当级联的上级还有上级元素时
                        $('.navigation-check').each(function() {
                            var thdId = $(this).attr('id').split('_')[3];
                            var type = $(this).attr('data-type');
                            if (thdId == thdPid ) {
                                $(this).prop('checked', true);
                            }
                        });
                    }
                }
            });
        }
    }
}


$('body')
    .on('change', '.navigation-check', function() {
        var arr = $(this).attr('id').split('_');
        var type = $(this).attr('data-type'); // 页面权限or操作权限
        var isChecked = $(this).is(':checked'); // 当前点击元素选中状态
        var pid = $(this).attr('data-pid'); // 当前节点的父id

        // 页面权限
        if (type == 1) {
            cascadingSelect(arr, isChecked);  // 点击级联全选、全不选
            // 同级选择时级联上级
            if (pid !== '-1') {
                peerSelect(1, pid);
            }
        } else { // 操作权限  ,
            if (pid !== -1) {
                peerSelect(2, pid);
            }
        }
    })