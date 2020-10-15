var treeData = '';

// 权限
var permissionSetAssetGroup = [];
var permissionSetAssetGroupTable = [];

// 新建
if(permissionSetArr.indexOf('assetgroup::add::assetgroup') > -1){
    permissionSetAssetGroup.push({
                type: 'button',
                icon: 'icon-add',
                title: '添加',
                action: function () {
                    $("#addAssetGroupBox").show();
                    assetGrouplist.addDom = gd.showLayer({
                        id: 'addAssetGroup_layer',
                        title: '新建设备组',
                        content: $('#addAssetGroupBox'),
                        size: [530, 380],
                        btn: [
                            {
                                text: '确定',
                                enter: true,//响应回车
                                action: function (dom) {
                                    var validate = gd.validate('#assetFrom', {
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
                                        var addObject = $("#addAssetGroup_layer form").serializeJSON();
                                        addObject.pid = assetGrouplist.groupId;
                                        $.ajax({
                                            type: 'post',
                                            url: baseUrl + "/assetgroup/assetgroup",
                                            data: addObject,
                                            success: function (res) {
                                                gd.closeLoading();
                                                if(!res.resultCode){
                                                    gd.showSuccess("创建设备组成功！");
                                                    gd.table('assetGroupListTable').reload();
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
                                    $("#addAssetGroupBox").hide();
                                }
                            }
                        ],
                        success: function (dom) {
                            $.ajax({
                                url: baseUrl + '/assetgroup/getAssetgroupListByPid',
                                dataType: "json",
                                success: function (data) {
                                    treeData = data;
                                    gd.tree('assetGroupTree').setData(treeData);
                                }
                            })
                            assetGrouplist.groupId = '';
                            assetGrouplist.groupName = '';
                            assetGrouplist.groupBelong = '';
                            assetGrouplist.remark = '';
                        },
                        end: function (dom) {
                            $("#addAssetGroupBox").hide();
                        }
                    });
                }
    })
}

// 编辑
if(permissionSetArr.indexOf('assetgroup::update::assetgroup') > -1){
    permissionSetAssetGroupTable.push({
        icon: 'icon-edit',
        title: '编辑',//设置图标title
        action: function (cell, row, raw) {
            $("#addAssetGroupBox").show();
            assetGrouplist.editDom = gd.showLayer({
                id: 'addAssetGroup_layer',
                title: '编辑设备组',
                content: $('#addAssetGroupBox'),
                size: [530, 380],
                btn: [
                    {
                        text: '确定',
                        enter: true,
                        action: function (dom) {
                            var validate = gd.validate('#assetFrom', {
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
                                var addObject = $("#addAssetGroup_layer form").serializeJSON();
                                addObject.pid = assetGrouplist.groupId;
                                console.log(addObject.pid);
                                addObject.id = cell;
                                if(cell == '1'){
                                    addObject.pid = null
                                }

                                $.ajax({
                                    type: 'PUT',
                                    url: baseUrl + "/assetgroup/assetgroup",
                                    data: addObject,
                                    success: function (res) {
                                        gd.closeLoading();
                                        if(!res.resultCode){
                                            gd.showSuccess("编辑设备组成功！");
                                            gd.table('assetGroupListTable').reload();
                                        }else{
                                            gd.showError(res.resultMsg);
                                        }
                                    },
                                    error: function (xhr, errorText, errorStatus) {
                                    }
                                })
                                $("#addAssetGroupBox").hide();
                            }else{
                                return false
                            }
                        }
                    },
                    {
                        text: '取消',
                        action: function () {
                            $("#addAssetGroupBox").hide();
                        }
                    }],
                success: function (dom) {
                    $.ajax({
                        url: baseUrl + '/assetgroup/getAssetgroupListByPid',
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
                            gd.tree('assetGroupTree').setData(treeData);
                            gd.tree('assetGroupTree').setNode(cell,nodeData);
                        }
                    })
                    assetGrouplist.groupId = raw.pId;
                    assetGrouplist.groupName = raw.name;
                    assetGrouplist.groupBelong = raw.pname;
                    assetGrouplist.remark = raw.remark;
                },
                end: function (dom) {
                    $("#addAssetGroupBox").hide();
                }
            });
        }
    })
}
// 删除、批量删除
if(permissionSetArr.indexOf('assetgroup::delete::assetgroup') > -1){
    permissionSetAssetGroup.push({
                type: 'button',
                icon: 'icon-delete',
                title: '删除',
                disabled:true,
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
                        id:'assetGroupDeleteWindow',
                        content:'确定要删除吗?',
                        btn:[{
                            text:'删除',
                            class:'gd-btn-danger',
                            enter:true, // 响应回车
                            disabled:false,
                            action:function (dom) {
                                $.ajax({
                                    url:baseUrl + "/assetgroup/assetgroup/delete?ids=" + deleteIds,
                                    type:'DELETE',
                                    success:function (data) {
                                        if(data.resultCode =='0'){
                                            gd.showSuccess("删除成功");
                                            gd.table("assetGroupListTable").reload();
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

    permissionSetAssetGroupTable.push({
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
                                                url:baseUrl + "/assetgroup/assetgroup/"+ cell,
                                                type:"DELETE",
                                                contentType:"application/json",//设置请求参数类型为json字符串
                                                dataType:"json",
                                                success:function(res){
                                                    if(res.resultCode == '0'){
                                                        gd.showSuccess("删除成功");
                                                        gd.table('assetGroupListTable').reload();
                                                    }else{
                                                        gd.showError(res.resultMsg);
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
    })
}

permissionSetAssetGroup.push({
    type: 'searchbox',
    placeholder: "名称",
    action: function (val) {
        gd.table('assetGroupListTable').reload(false,{searchStr:val},false)
    }
})

var assetGrouplist = new Vue({
    el: '#contentDiv',
    methods: {
    },
    data: {
        groupId:'',
        groupName:'',
        remark:'',
        groupBelong:'',
        treeWinConfig:{
            id: 'assetGroupTree',
            accordion: true,
            linkable: false,
            accordion: false,
            data:[],
            onSelect: function(node){
                assetGrouplist.groupId = node.id;
                assetGrouplist.groupBelong = node.name;
                assetGrouplist.$refs.groupTreeSeletct.isDroped=false;
            },
            onChange:function(node){
            },
            ready:function(data){
            }
        },
        toolbarConfig:permissionSetAssetGroup,
        //表格配置
        tableConfig: {
            id: 'assetGroupListTable',
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
                //其它ajax参数同jquery
                url: baseUrl + '/assetgroup/getAssetgroupInPage',
                //改变从服务器返回的数据给table
                dataSrc: function (data) {
                    // console.log(data);
                    data.rows = data.rows.map(function (obj) {
                        return [
                            obj.id,
                            obj.name,
                            obj.pname,
                            obj.assetCount,
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
                    align: 'center',
                    change: function (checkedData, checkedRawData) {
                        setToolBtnDisable(assetGrouplist.toolbarConfig,'icon-delete',checkedData.length==0);
                    }
                },
                {
                    name: 'name',//本列如果有排序或高级搜索，必须要有name
                    head: '名称',
                    ellipsis: false,//可以禁用text ellipsis,默认为true
                    title: function (cell, row, raw) {
                        return cell
                    },
                    render: function (cell, row, raw) {//自定义表格内容
                        return cell;
                    }
                },
                {
                    name: 'name',
                    head: '所属组',
                    title: function (cell, row, raw) {
                        return cell
                    }
                },
                {
                    name: 'department',
                    head: '设备数'
                },
                {
                    name: 'authenticationMethod',
                    head: '备注'
                },
                {
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    width: 120,
                    operates: permissionSetAssetGroupTable
                }
            ]
        }
    }
})