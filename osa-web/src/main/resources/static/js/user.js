var treeData = '';
var treeAllData = '';
var treeAssetGroupData = '';

var permissionSetUser = [];
var permissionSetUserTable = [];

// 新建
if (permissionSetArr.indexOf('user::add::user') > -1) {
    permissionSetUser.push({
        type: 'button',
        icon: 'icon-add',
        title: '添加',
        action: function() {
            userlist.dataukeyIdArr = [];
            userlist.usergroups = [];
            userlist.user_info = {
                username: '',
                pwStatus: true,
                password: '',
                confirm_password: '',
                authenticationMethod: 1,
                name: '',
                userStrategy: 1,
                roles: '',
                email: '',
                phone: '',
                remark: '',
                status: 11,
                ukeyId:'',
            };
            $('#addUserBox').show();

            $('#level').removeClass('pw-weak');
            $('#level').removeClass('pw-medium');
            $('#level').removeClass('pw-strong');
            $('#level').addClass(' pw-defule');

            var validate = '';
            userlist.addDom = gd.showLayer({
                id: 'adduser_layer',
                title: '新建用户',
                content: $('#addUserBox'),
                size: [550, 600],
                // autoFocus:true,
                btn: [
                    {
                        text: '确定',
                        enter: true, //响应回车
                        action: function(dom) {
                            //参数为当前窗口dom对象
                            var result = validate.valid(); //获得整体的校验结果

                            if (result) {
                                var addObject = $('#adduser_layer form').serializeJSON();
                                addObject.password = encrypt(addObject.password).toUpperCase();
                                addObject.usergroupIds = userlist.nodeObj.join(',');
                                addObject.roleType = 'normal';
                                $.ajax({
                                    type: 'post',
                                    url: baseUrl + '/user/user',
                                    data: addObject,
                                    success: function(res) {
                                        gd.closeLoading();
                                        if (!res.resultCode) {
                                            gd.showSuccess('创建用户成功！');
                                            gd.table('userlistTable').reload();
                                        } else {
                                            gd.showError(res.resultMsg);
                                        }
                                        $('#addUserBox').hide();
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
                        action: function() {
                            userlist.addDom.close();
                            $('#addUserBox').hide();
                        }
                    }
                ],
                success: function(dom) {
                    //参数为当前窗口dom对象
                    userlist.pwStatusChange();
                    $('#userNameBox').show();
                    $.ajax({
                        url: baseUrl + '/usergroup/list',
                        dataType: 'json',
                        success: function(data) {
                            gd.tree('windowTree').setData(data);
                        }
                    });
                    //获取密码配置
                    gd.get(baseUrl + '/system/systemSet/platform/security', function(msg) {
                        if (msg.resultCode == 0) {
                            userlist.pwLength = msg.data.minLength;
                            validate = gd.validate('#userForm', {
                                rules: [
                                    {
                                        name: 'passwordRule',
                                        msg: '必填项',
                                        valid: function(value, el) {
                                            var isDefault = $('input[name=defaultPassword]').prop('checked');
                                            if (!value && !isDefault) {
                                                return false;
                                            } else {
                                                return true;
                                            }
                                        }
                                    },
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
                },
                end: function() {
                    $('#addUserBox').hide();
                }
            });
        }
    });
}

// 导入
if (permissionSetArr.indexOf('user::import::user') > -1) {
    permissionSetUser.push({
        type: 'button',
        icon: 'icon-import',
        title: '导入',
        action: function() {
            var importData = null;
            gd.showLayer({
                id: 'importUser_layer',
                title: '导入用户',
                content: $('#importUserBox').html(),
                size: [616, 420],
                btn: [
                    {
                        text: '确定',
                        enter: true,
                        action: function(dom) {
                            if ($('#uploadForm #incTrueFile').get(0).files.length == 0) {
                                gd.showWarning('请选择文件');
                                return false;
                            }
                            var formData = new FormData($('#uploadForm')[0]);
                            formData.append('flag', importData.importIndex);
                            gd.showLoading('正在导入中...');
                            $.ajax({
                                url: baseUrl + '/asset/import/user',
                                type: 'post',
                                async: true,
                                cache: false,
                                contentType: false,
                                processData: false,
                                dataType: 'json',
                                data: formData,
                                success: function(msg) {
                                    gd.closeLoading();
                                    if (msg.resultCode == '0') {
                                        gd.closeAllLayer();
                                        gd.showSuccess('导入成功！');
                                        gd.table('userlistTable').reload();
                                    } else {
                                        gd.showError(msg.resultMsg);
                                    }
                                },
                                error: function(e) {
                                    gd.closeLoading();
                                }
                            });
                            return false;
                        }
                    },
                    {
                        text: '取消',
                        action: function() {}
                    }
                ],
                success: function(dom) {
                    importData = new Vue({
                        el: '#importUser',
                        data: {
                            importMode: ['全量导入', '增量导入'],
                            importIndex: 'ALL'
                        },
                        mounted: function() {
                            $('#uploadForm #incTrueFile').on('change', function() {
                                $('#uploadForm #incCopyFile').val($(this).val());
                            });
                        },
                        methods: {
                            radioChange: function(index) {
                                var _self = this;
                                if (index == 0) {
                                    _self.importIndex = 'ALL';
                                } else {
                                    _self.importIndex = 'PART';
                                }
                            },
                            downModel: function() {
                                location.href = baseUrl + '/asset/import/user/template';
                            }
                        }
                    });
                },
                end: function() {}
            });
        }
    });
}

// 导出
if (permissionSetArr.indexOf('user::export::user') > -1) {
    permissionSetUser.push({
        type: 'button',
        icon: 'icon-export',
        title: '导出',
        action: function() {
            var exportData = gd.table('userlistTable').getAjaxParam(true);

            var allCheckedData = gd.table('userlistTable').getCheckedData();

            // 暂存批量删除id
            var exportIds = '';
            if (allCheckedData.length>0){
                exportIds = [];
                for (var i = 0; i < allCheckedData.length; i++) {
                    exportIds.push(allCheckedData[i][0]);
                }
                exportIds = exportIds.join(',');
            }
            location.href = baseUrl + '/asset/export/user?' + $.param(exportData)+'&userIds=' + exportIds;
        }
    });
}

// 编辑
if (permissionSetArr.indexOf('user::update::update') > -1) {
    permissionSetUserTable.push({
        icon: 'icon-edit',
        title: '编辑', //设置图标title
        action: function(cell, row, raw) {
            //动作函数,cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据
            if (raw.defaultPassword == 'false' || raw.defaultPassword == 'off') {
                raw.defaultPassword = false;
            } else {
                raw.defaultPassword = true;
            }
            userlist.dataukeyIdArr = [];

            userlist.user_info = {
                username: raw.username,
                pwStatus: raw.defaultPassword,
                password: '',
                confirm_password: '',
                authenticationMethod: raw.authenticationMethod,
                name: raw.name,
                userStrategy: raw.strategy,
                email: raw.email,
                phone: raw.phone,
                status: raw.status,
                ukeyId:'',
            };

            if(raw.authenticationMethod == 3){
                
                userlist.authenticationChange();
                userlist.dataukeyIdArr.push({
                    value: raw.ukeyId,
                    name: raw.ukeyName
                })
                userlist.user_info.ukeyId =  raw.ukeyId;
            }
            userlist.nodeObjName = raw.usergroupsName.split(',');
            userlist.usergroups = raw.usergroupsName;

            $('#addUserBox').show();

            $('#level').removeClass('pw-weak');
            $('#level').removeClass('pw-medium');
            $('#level').removeClass('pw-strong');
            $('#level').addClass(' pw-defule');

            $.get(baseUrl + '/usergroup/list', { guid: cell }, function(data) {
                gd.tree('windowTree').setData(data);
                userlist.usergroupsArr = [];
                data.map(function(obj, index) {
                    if (obj.checked) {
                        userlist.usergroupsArr.push(obj.id);
                    }
                });
                userlist.nodeObj = userlist.usergroupsArr;
            });
            var validate = '';
            userlist.editDom = gd.showLayer({
                id: 'edituser_layer',
                title: '编辑用户',
                content: $('#addUserBox'),
                size: [550, 600],
                btn: [
                    {
                        text: '确定',
                        enter: true, //响应回车
                        action: function(dom) {
                            //参数为当前窗口dom对象
                            var rulesCon = '';

                            var result = validate.valid(); //获得整体的校验结果
                            if (result) {
                                var addObject = $('#edituser_layer form').serializeJSON();
                                if(addObject.password){
                                    addObject.password = encrypt(addObject.password).toUpperCase();
                                }
                                addObject.usergroupIds = userlist.nodeObj.join(',');
                                addObject.roleType = 'normal';
                                addObject.guid = cell;
                                $.ajax({
                                    type: 'PUT',
                                    url: baseUrl + '/user/user',
                                    data: addObject,
                                    success: function(res) {
                                        gd.closeLoading();
                                        if (!res.resultCode) {
                                            gd.showSuccess('编辑用户成功！');
                                            gd.table('userlistTable').reload();
                                        } else {
                                            gd.showError(res.resultMsg);
                                        }
                                        $('#addUserBox').hide();
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
                        action: function() {
                            $('#addUserBox').hide();
                        }
                    }
                ],
                success: function(dom) {
                    //参数为当前窗口dom对象
                    $('#userNameBox').hide();
                    userlist.pwStatusChange();
                    //获取密码配置
                    gd.get(baseUrl + '/system/systemSet/platform/security', function(msg) {
                        if (msg.resultCode == 0) {
                            userlist.pwLength = msg.data.minLength;
                            validate = gd.validate('#userForm', {
                                rules: [
                                    {
                                        name: 'passwordRule',
                                        msg: '必填项',
                                        valid: function(value, el) {
                                           return true;
                                        }
                                    },
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
                },
                end: function() {
                    $('#addUserBox').hide();
                }
            });
        }
    });
}

// 授权
if (permissionSetArr.indexOf('user::granted::asset') > -1) {
    permissionSetUserTable.push({
        icon: 'icon-authorize',
        text: '授权设备',
        title: '授权',
        action: function(cell, row, raw) {
            gd.showLayer({
                id: 'authorizedAddBox_layer',
                title: '授权设备',
                content: $('#authorizedAssetBox').html(),
                size: [1000, 600],
                btn: [],
                success: function(dom) {
                    var authorized = new Vue({
                        el: '#authorizedBox',
                        data: {
                            toolbarAssetConfig: [
                                {
                                    type: 'button',
                                    icon: 'icon-add',
                                    title: '添加设备权限',
                                    action: function() {
                                        $.ajax({
                                            url: baseUrl + '/granted/getGrantedTree?guid=' + cell,
                                            dataType: 'json',
                                            success: function(data) {
                                                var authorizedAssetAdd = '';
                                                treeAllData = data;
                                                gd.showLayer({
                                                    id: 'authorizedAssetAdd_layer',
                                                    title: '添加设备',
                                                    content: $('#authorizedAssetAddBox').html(),
                                                    size: [740, 530],
                                                    btn: [
                                                        {
                                                            text: '申请',
                                                            enter: true, //响应回车
                                                            action: function(dom) {
                                                                //参数为当前窗口dom对象
                                                                var userObj = {
                                                                    userType: 1,
                                                                    userId: cell,
                                                                    usergroupId: ''
                                                                };

                                                                var grantedObj = {
                                                                    grantedType: 3,
                                                                    assetaccounts: authorizedAssetAdd.assetAccountArr.join(
                                                                        ','
                                                                    ),
                                                                    assetgroups: ''
                                                                };

                                                                $.ajax({
                                                                    type: 'post',
                                                                    url: baseUrl + '/granted/grantedAsset4User',
                                                                    data: {
                                                                        userJson: JSON.stringify(userObj),
                                                                        grantedJson: JSON.stringify(grantedObj)
                                                                    },
                                                                    success: function(res) {
                                                                        if (!res.resultCode) {
                                                                            gd.showSuccess('操作成功！');
                                                                            gd.table('assetAuthlistTable').reload(
                                                                                false
                                                                            );
                                                                        } else {
                                                                            gd.showError(res.resultMsg);
                                                                        }
                                                                        $('#authorizedAddBox').hide();
                                                                    },
                                                                    error: function(xhr, errorText, errorStatus) {}
                                                                });
                                                            }
                                                        },
                                                        {
                                                            text: '取消',
                                                            action: function() {
                                                                $('#authorizedAddBox').hide();
                                                            }
                                                        }
                                                    ],
                                                    success: function(dom) {
                                                        authorizedAssetAdd = new Vue({
                                                            el: '#authorizedAssetAdd',
                                                            data: {
                                                                assetAccountList: [],
                                                                assetAuthListLen: 0,
                                                                treeAuthConfig: {
                                                                    id: 'authorizedTree', //树的id，用于提供API
                                                                    accordion: true,
                                                                    data: treeAllData,
                                                                    showCheckBox: true, //默认是false;显示checkbox
                                                                    linkable: true,
                                                                    onSelect: function(node) {
                                                                        // 点击树节点时触发
                                                                        // gd.table('userlistTable').reload(false,{usergroupPid:node.id},false)
                                                                    },
                                                                    onChange: function(nodes) {
                                                                        // 点击复选框时触发
                                                                        authorizedAssetAdd.assetAccountList = [];
                                                                        authorizedAssetAdd.assetAccountArr = [];
                                                                        nodes.map(function(item, index) {
                                                                            if (
                                                                                item.id.indexOf('account') > -1 &&
                                                                                !item.ignore
                                                                            ) {
                                                                                var assetCon = gd
                                                                                    .tree('authorizedTree')
                                                                                    .getNode(item.pId);
                                                                                var obj = {
                                                                                    assetName: assetCon.name,
                                                                                    accountName: item.name
                                                                                };
                                                                                authorizedAssetAdd.assetAccountArr.push(
                                                                                    item.id
                                                                                );
                                                                                authorizedAssetAdd.assetAccountList.push(
                                                                                    obj
                                                                                );
                                                                            }
                                                                        });
                                                                        authorizedAssetAdd.assetAuthListLen =
                                                                            authorizedAssetAdd.assetAccountList.length;
                                                                    },
                                                                    ready: function(nodes) {}
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                                // gd.tree('authorizedTree').setData(treeAllData);
                                            }
                                        });
                                    }
                                }
                            ],
                            toolbarAssetGroupConfig: [
                                {
                                    type: 'button',
                                    icon: 'icon-add',
                                    title: '添加设备组权限',
                                    action: function() {
                                        // 设备组树
                                        $.ajax({
                                            url: baseUrl + '/granted/getGrantedTree',
                                            data: { guid: cell, isGroup: true },
                                            dataType: 'json',
                                            success: function(data) {
                                                var authorizedAssetGroupAdd = '';
                                                treeAssetGroupData = data;
                                                gd.showLayer({
                                                    id: 'authorizedAssetGroupAdd_layer',
                                                    title: '添加设备组',
                                                    content: $('#authorizedGroupAddBox').html(),
                                                    size: [740, 530],
                                                    btn: [
                                                        {
                                                            text: '申请',
                                                            enter: true, //响应回车
                                                            action: function(dom) {
                                                                //参数为当前窗口dom对象
                                                                var userObj = {
                                                                    userType: 1,
                                                                    userId: cell,
                                                                    usergroupId: ''
                                                                };
                                                                var grantedObj = {
                                                                    grantedType: 1,
                                                                    assetaccounts: '',
                                                                    assetgroups: authorizedAssetGroupAdd.assetGroupArr.join(
                                                                        ','
                                                                    )
                                                                };

                                                                $.ajax({
                                                                    type: 'post',
                                                                    url: baseUrl + '/granted/grantedAsset4User',
                                                                    data: {
                                                                        userJson: JSON.stringify(userObj),
                                                                        grantedJson: JSON.stringify(grantedObj)
                                                                    },
                                                                    success: function(res) {
                                                                        if (!res.resultCode) {
                                                                            gd.showSuccess('操作成功！');
                                                                            gd.table('assetGroupAuthlistTable').reload(
                                                                                false
                                                                            );
                                                                        } else {
                                                                            gd.showError(res.resultMsg);
                                                                        }
                                                                    },
                                                                    error: function(xhr, errorText, errorStatus) {}
                                                                });
                                                            }
                                                        },
                                                        {
                                                            text: '取消',
                                                            action: function() {}
                                                        }
                                                    ],
                                                    success: function(dom) {
                                                        authorizedAssetGroupAdd = new Vue({
                                                            el: '#authorizedGroupAdd',
                                                            data: {
                                                                assetGroupAuthList: [],
                                                                assetGroupArr: [],
                                                                assetGroupAuthListLen: 0,
                                                                treeAuthGroupConfig: {
                                                                    id: 'authorizedGroupTree', //树的id，用于提供API
                                                                    accordion: true,
                                                                    data: treeAssetGroupData,
                                                                    showCheckBox: true, //默认是false;显示checkbox
                                                                    linkable: true,
                                                                    onSelect: function(node) {
                                                                        // 点击树节点时触发
                                                                    },
                                                                    onChange: function(nodes) {
                                                                        // 点击复选框时触发

                                                                        authorizedAssetGroupAdd.assetGroupAuthList = [];
                                                                        authorizedAssetGroupAdd.assetGroupArr = [];
                                                                        nodes.map(function(item, index) {
                                                                            if (
                                                                                !item.ignore &&
                                                                                item.id.indexOf('nihility') == -1
                                                                            ) {
                                                                                var obj = {
                                                                                    assetGroupName: item.name
                                                                                };
                                                                                authorizedAssetGroupAdd.assetGroupArr.push(
                                                                                    item.id
                                                                                );
                                                                                authorizedAssetGroupAdd.assetGroupAuthList.push(
                                                                                    obj
                                                                                );
                                                                            }
                                                                        });

                                                                        authorizedAssetGroupAdd.assetGroupAuthListLen =
                                                                            authorizedAssetGroupAdd.assetGroupAuthList.length;
                                                                    },
                                                                    ready: function(data) {
                                                                        //树的数据改变后，dom渲染完成后触发，data为树的数据
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            ],
                            tableAssetConfig: {
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
                                    url: baseUrl + '/granted/getAssestAccountsInPage?userGuid=' + cell,
                                    dataSrc: function(data) {
                                        data.rows = data.rows.map(function(obj) {
                                            return [
                                                obj.status,
                                                obj.assetname,
                                                obj.assetip,
                                                obj.accountname,
                                                obj.assettypename,
                                                obj.assetgroupname,
                                                obj.trusteeship,
                                                obj.id
                                            ];
                                        });
                                        return data;
                                    }
                                },
                                columns: [
                                    {
                                        name: 'username', //本列如果有排序或高级搜索，必须要有name
                                        head: '状态',
                                        align: 'center',
                                        width: '80',
                                        render: function(cell, row, raw) {
                                            //自定义表格内容
                                            var html = '';
                                            if (cell == '0') {
                                                html += '<span class="auth-status-mark status-mark-wait">待审批</span>';
                                            } else if (cell == '1') {
                                                html += '<span class="auth-status-mark status-mark-auth">已授权</span>';
                                            }
                                            return html;
                                        }
                                    },
                                    {
                                        name: 'username', //本列如果有排序或高级搜索，必须要有name
                                        head: '设备名称',
                                        title: function(cell, row, raw) {
                                            //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell;
                                        },
                                        render: function(cell, row, raw) {
                                            //自定义表格内容
                                            return cell;
                                        }
                                    },
                                    {
                                        name: 'username', //本列如果有排序或高级搜索，必须要有name
                                        head: '设备IP',
                                        title: function(cell, row, raw) {
                                            //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell;
                                        },
                                        render: function(cell, row, raw) {
                                            //自定义表格内容
                                            return cell;
                                        }
                                    },
                                    {
                                        name: 'username', //本列如果有排序或高级搜索，必须要有name
                                        head: '设备账号',
                                        title: function(cell, row, raw) {
                                            //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell;
                                        },
                                        render: function(cell, row, raw) {
                                            //自定义表格内容
                                            return cell;
                                        }
                                    },
                                    {
                                        name: 'username', //本列如果有排序或高级搜索，必须要有name
                                        head: '设备类型',
                                        title: function(cell, row, raw) {
                                            //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell;
                                        },
                                        render: function(cell, row, raw) {
                                            //自定义表格内容
                                            return cell;
                                        }
                                    },
                                    {
                                        name: 'username', //本列如果有排序或高级搜索，必须要有name
                                        head: '设备组',
                                        title: function(cell, row, raw) {
                                            //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell;
                                        },
                                        render: function(cell, row, raw) {
                                            //自定义表格内容
                                            return cell;
                                        }
                                    },
                                    {
                                        name: 'username', //本列如果有排序或高级搜索，必须要有name
                                        head: '账号权限',
                                        title: function(cell, row, raw) {
                                            //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                                            return cell;
                                        },
                                        render: function(cell, row, raw) {
                                            //自定义表格内容
                                            return cell;
                                        }
                                    },
                                    {
                                        name: 'operate',
                                        head: '操作',
                                        align: 'center',
                                        width: 150,
                                        operates: [
                                            {
                                                icon: 'icon-reset',
                                                text: '撤销',
                                                disabled: function(cell, row, raw) {
                                                    if (raw.status == '0') {
                                                        return false;
                                                    } else {
                                                        return true;
                                                    }
                                                },
                                                action: function(cell, row, raw) {
                                                    var dom = gd.showConfirm({
                                                        id: 'resetAuthwind',
                                                        content: '确定要撤销吗?',
                                                        btn: [
                                                            {
                                                                text: '确定',
                                                                class: 'gd-btn-danger',
                                                                enter: true, //响应回车
                                                                action: function(dom) {
                                                                    $.ajax({
                                                                        url:
                                                                            baseUrl +
                                                                            '/granted/revokeGrantedById/' +
                                                                            cell,
                                                                        type: 'PUT',
                                                                        contentType: 'application/json', //设置请求参数类型为json字符串
                                                                        dataType: 'json',
                                                                        success: function(res) {
                                                                            if (res.resultCode == '0') {
                                                                                gd.showSuccess('操作成功');
                                                                                gd.table('assetAuthlistTable').reload();
                                                                            } else {
                                                                                gd.showMsg(res.resultMsg, {
                                                                                    time: 5000
                                                                                });
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
                                            },
                                            {
                                                icon: 'icon-delete',
                                                text: '删除',
                                                disabled: function(cell, row, raw) {
                                                    if (raw.status == '0') {
                                                        return true;
                                                    } else {
                                                        return false;
                                                    }
                                                },
                                                action: function(cell, row, raw) {
                                                    var dom = gd.showConfirm({
                                                        id: 'assetAuthwind',
                                                        content: '确定要删除吗?',
                                                        btn: [
                                                            {
                                                                text: '删除',
                                                                class: 'gd-btn-danger',
                                                                enter: true, //响应回车
                                                                action: function(dom) {
                                                                    $.ajax({
                                                                        url: baseUrl + '/granted/deleteById/' + cell,
                                                                        type: 'DELETE',
                                                                        contentType: 'application/json', //设置请求参数类型为json字符串
                                                                        dataType: 'json',
                                                                        success: function(res) {
                                                                            if (res.resultCode == '0') {
                                                                                gd.table('assetAuthlistTable').reload();
                                                                            } else {
                                                                                gd.showMsg(res.resultMsg, {
                                                                                    time: 5000
                                                                                });
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
                                            }
                                        ]
                                    }
                                ]
                            },

                            tableAssetGroupConfig: {
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
                                    url: baseUrl + '/granted/getAssestgroupsInPage?userGuid=' + cell,
                                    dataSrc: function(data) {
                                        data.rows = data.rows.map(function(obj) {
                                            return [obj.status, obj.assetgroupname, obj.id];
                                        });
                                        return data;
                                    }
                                },
                                columns: [
                                    {
                                        name: 'status',
                                        head: '状态',
                                        align: 'center',
                                        width: '120',
                                        title: function(cell, row, raw) {
                                            return cell;
                                        },
                                        render: function(cell, row, raw) {
                                            var html = '';
                                            if (cell == '0') {
                                                html += '<span class="auth-status-mark status-mark-wait">待审批</span>';
                                            } else if (cell == '1') {
                                                html += '<span class="auth-status-mark status-mark-auth">已授权</span>';
                                            }
                                            return html;
                                        }
                                    },
                                    {
                                        name: 'assetgroupname',
                                        head: '设备组名称',
                                        title: function(cell, row, raw) {
                                            return cell;
                                        },
                                        render: function(cell, row, raw) {
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
                                                action: function(cell, row, raw) {
                                                    gd.showLayer({
                                                        id: 'assetDetail_layer',
                                                        title: '设备详情',
                                                        content: $('#assetDetailBox').html(),
                                                        size: [860, 500],
                                                        btn: [],
                                                        success: function(dom) {
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
                                                                            url:
                                                                                baseUrl +
                                                                                '/assetgroup/' +
                                                                                raw.assetgroupId +
                                                                                '/account',
                                                                            dataSrc: function(data) {
                                                                                data.rows = data.rows.map(function(
                                                                                    obj
                                                                                ) {
                                                                                    return [
                                                                                        obj.assetName,
                                                                                        obj.assetIp,
                                                                                        obj.username,
                                                                                        obj.assetTypeName,
                                                                                        obj.assetGroupName,
                                                                                        obj.trusteeshipName
                                                                                    ];
                                                                                });
                                                                                return data;
                                                                            }
                                                                        },
                                                                        columns: [
                                                                            {
                                                                                name: 'assetname',
                                                                                head: '设备名称',
                                                                                width: '150',
                                                                                title: function(cell, row, raw) {
                                                                                    return cell;
                                                                                },
                                                                                render: function(cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            },
                                                                            {
                                                                                name: 'status',
                                                                                head: '设备IP',
                                                                                align: 'center',
                                                                                width: '120',
                                                                                title: function(cell, row, raw) {
                                                                                    return cell;
                                                                                },
                                                                                render: function(cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            },
                                                                            {
                                                                                name: 'status',
                                                                                head: '设备账号',
                                                                                align: 'center',
                                                                                title: function(cell, row, raw) {
                                                                                    return cell;
                                                                                },
                                                                                render: function(cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            },
                                                                            {
                                                                                name: 'status',
                                                                                head: '设备类型',
                                                                                align: 'center',
                                                                                title: function(cell, row, raw) {
                                                                                    return cell;
                                                                                },
                                                                                render: function(cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            },
                                                                            {
                                                                                name: 'status',
                                                                                head: '设备组',
                                                                                align: 'center',
                                                                                title: function(cell, row, raw) {
                                                                                    return cell;
                                                                                },
                                                                                render: function(cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            },
                                                                            {
                                                                                name: 'status',
                                                                                head: '账号权限',
                                                                                align: 'center',
                                                                                width: '120',
                                                                                title: function(cell, row, raw) {
                                                                                    return cell;
                                                                                },
                                                                                render: function(cell, row, raw) {
                                                                                    return cell;
                                                                                }
                                                                            }
                                                                        ]
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            },
                                            {
                                                icon: 'icon-reset',
                                                text: '撤销',
                                                disabled: function(cell, row, raw) {
                                                    if (raw.status == '0') {
                                                        return false;
                                                    } else {
                                                        return true;
                                                    }
                                                },
                                                action: function(cell, row, raw) {
                                                    var dom = gd.showConfirm({
                                                        id: 'resetAuthwind',
                                                        content: '确定要撤销吗?',
                                                        btn: [
                                                            {
                                                                text: '确定',
                                                                class: 'gd-btn-danger',
                                                                enter: true, //响应回车
                                                                action: function(dom) {
                                                                    $.ajax({
                                                                        url:
                                                                            baseUrl +
                                                                            '/granted/revokeGrantedById/' +
                                                                            cell,
                                                                        type: 'PUT',
                                                                        contentType: 'application/json', //设置请求参数类型为json字符串
                                                                        dataType: 'json',
                                                                        success: function(res) {
                                                                            if (res.resultCode == '0') {
                                                                                gd.showSuccess('操作成功');
                                                                                gd.table(
                                                                                    'assetGroupAuthlistTable'
                                                                                ).reload();
                                                                            } else {
                                                                                gd.showMsg(res.resultMsg, {
                                                                                    time: 5000
                                                                                });
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
                                            },
                                            {
                                                icon: 'icon-delete',
                                                text: '删除',
                                                disabled: function(cell, row, raw) {
                                                    if (raw.status == '0') {
                                                        return true;
                                                    } else {
                                                        return false;
                                                    }
                                                },
                                                action: function(cell, row, raw) {
                                                    var dom = gd.showConfirm({
                                                        id: 'assetGroupAuthwind',
                                                        content: '确定要删除吗?',
                                                        btn: [
                                                            {
                                                                text: '删除',
                                                                class: 'gd-btn-danger',
                                                                enter: true, //响应回车
                                                                action: function(dom) {
                                                                    $.ajax({
                                                                        url: baseUrl + '/granted/deleteById/' + cell,
                                                                        type: 'DELETE',
                                                                        contentType: 'application/json', //设置请求参数类型为json字符串
                                                                        dataType: 'json',
                                                                        success: function(res) {
                                                                            if (res.resultCode == '0') {
                                                                                gd.table(
                                                                                    'assetGroupAuthlistTable'
                                                                                ).reload();
                                                                            } else {
                                                                                gd.showMsg(res.resultMsg, {
                                                                                    time: 5000
                                                                                });
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
                                            }
                                        ]
                                    }
                                ]
                            }
                        }
                    });
                }
            });
        }
    });
}

permissionSetUserTable.push({
    icon: 'icon-lecture',
    text: '详情',
    title: '详情',
    action: function(cell, row, raw) {
        gd.showLayer({
            id: 'authorizedDetail_layer',
            title: '授权详情',
            content: $('#authorizedDetailBox').html(),
            size: [1200, 600],
            btn: [],
            success: function(dom) {
                var authorizedTable = new Vue({
                    el: '#assetDetail',
                    data: {
                        toolbarAuthorizedTableConfig: [
                            {
                                type: 'button',
                                icon: 'icon-export',
                                title: '导出',
                                action: function() {
                                    var exportData = gd.table('authorizedDetailTable').getAjaxParam(true);
                                    location.href =
                                        baseUrl + '/granted/detail/export?userGuid=' + cell + '&' + $.param(exportData);
                                }
                            },
                            {
                                type: 'searchbox',
                                placeholder: '',
                                action: function(val) {
                                    gd.table('authorizedDetailTable').reload(false, { searchStr: val }, false);
                                }
                            }
                        ],
                        authorizedTableConfig: {
                            id: 'authorizedDetailTable',
                            length: 20,
                            curPage: 1,
                            lengthMenu: [10, 30, 50, 100],
                            enableJumpPage: false,
                            enableLengthMenu: true,
                            enablePaging: true,
                            columnResize: true,
                            fillBlank: '--',
                            ajax: {
                                url: baseUrl + '/granted/detailInPage?userGuid=' + cell,
                                dataSrc: function(data) {
                                    data.rows = data.rows.map(function(obj) {
                                        return [
                                            obj.status,
                                            obj.assetName,
                                            obj.assetIp,
                                            obj.account,
                                            obj.assetType,
                                            obj.groupName,
                                            obj.grantWays,
                                            obj.trusteeship
                                        ];
                                    });
                                    return data;
                                }
                            },
                            columns: [
                                {
                                    name: 'username',
                                    head: '状态',
                                    align: 'center',
                                    width: '80',
                                    render: function(cell, row, raw) {
                                        return '<span class="auth-status-mark status-mark-auth">' + cell + '</span>';
                                    }
                                },
                                {
                                    name: 'username',
                                    head: '设备名称',
                                    title: function(cell, row, raw) {
                                        return cell;
                                    },
                                    render: function(cell, row, raw) {
                                        return cell;
                                    }
                                },
                                {
                                    name: 'username',
                                    head: '设备IP',
                                    title: function(cell, row, raw) {
                                        return cell;
                                    },
                                    render: function(cell, row, raw) {
                                        return cell;
                                    }
                                },
                                {
                                    name: 'username',
                                    head: '设备账号',
                                    title: function(cell, row, raw) {
                                        return cell;
                                    },
                                    render: function(cell, row, raw) {
                                        return cell;
                                    }
                                },
                                {
                                    name: 'username',
                                    head: '设备类型',
                                    title: function(cell, row, raw) {
                                        return cell;
                                    },
                                    render: function(cell, row, raw) {
                                        return cell;
                                    }
                                },
                                {
                                    name: 'username',
                                    head: '设备组',
                                    title: function(cell, row, raw) {
                                        return cell;
                                    },
                                    render: function(cell, row, raw) {
                                        return cell;
                                    }
                                },
                                {
                                    name: 'username',
                                    head: '授权来源',
                                    title: function(cell, row, raw) {
                                        return cell;
                                    },
                                    render: function(cell, row, raw) {
                                        var html = '';
                                        cell.map(function(item, index) {
                                            var from = item.from;
                                            var to = item.to;
                                            html += from + '<span class="icon-arrow-right"></span>' + to;
                                            if (index != cell.length && cell.length != 1) {
                                                html += ';';
                                            }
                                        });
                                        return html;
                                    }
                                },
                                {
                                    name: 'username',
                                    head: '账号权限',
                                    title: function(cell, row, raw) {
                                        return cell;
                                    },
                                    render: function(cell, row, raw) {
                                        return cell;
                                    }
                                }
                            ]
                        }
                    }
                });
            }
        });
    }
});

// 删除、批量删除
if (permissionSetArr.indexOf('user::delete::one') > -1) {
    permissionSetUserTable.push({
        icon: 'icon-delete',
        text: '删除',
        title: '删除',
        action: function(cell, row, raw) {
            var dom = gd.showConfirm({
                id: 'wind',
                content: '确定要删除吗?',
                btn: [
                    {
                        text: '删除',
                        class: 'gd-btn-danger',
                        enter: true, //响应回车
                        action: function(dom) {
                            $.ajax({
                                url: baseUrl + '/user/user/' + cell,
                                type: 'DELETE',
                                contentType: 'application/json', //设置请求参数类型为json字符串
                                dataType: 'json',
                                success: function(res) {
                                    if (res.resultCode == '0') {
                                        gd.table('userlistTable').reload();
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

// 批量删除
if (permissionSetArr.indexOf('user::delete::batch') > -1) {
    permissionSetUser.push({
        type: 'button',
        icon: 'icon-delete',
        title: '删除',
        disabled:true,
        action: function() {
            var allCheckedData = gd.table('userlistTable').getCheckedData();
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
                id: 'userDeleteWindow',
                content: '确定要删除吗?',
                btn: [
                    {
                        text: '删除',
                        class: 'gd-btn-danger',
                        enter: true, // 响应回车
                        disabled: false,
                        action: function(dom) {
                            $.ajax({
                                url: baseUrl + '/user/user/deleteUsersByIds?userIds=' + deleteIds,
                                type: 'DELETE',
                                success: function(data) {
                                    gd.showSuccess('删除成功');
                                    gd.table('userlistTable').reload(1);
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
}

permissionSetUser.push({
    type: 'searchbox',
    placeholder: '用户名/姓名',
    action: function(val) {
        gd.table('userlistTable').reload(false, { searchStr: val }, false);
    }
});

$(function() {
    $.ajax({
        url: baseUrl + '/usergroup/list',
        dataType: 'json',
        success: function(data) {
            treeData = data;
            gd.tree('usergroupTree').setData(data);
        }
    });
});

var userlist = new Vue({
    el: '#contentDiv',
    mounted: function() {
        $.get(baseUrl + '/strategy/getStrategyAll', function(res) {
            $.each(res.data, function(index, item) {
                userlist.dataBaseList.push({
                    value: item.guid,
                    name: item.name
                });
            });
        });
    },
    methods: {
        pwStatusChange: function() {
            userlist.changeEditStat();
        },
        changeEditStat: function() {
            if (!userlist.user_info.pwStatus) {
                $('input#pwUser,#equalPwUser').removeAttr('disabled');
            } else {
                $('input#pwUser,#equalPwUser').attr('disabled', 'disabled');
                this.user_info.password = '';
                this.user_info.confirm_password = '';
            }
        },
        pwKeyupValidate: function(data) {
            var strongRegex = new RegExp('^(?=.{8,})(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W).*$', 'g');
            var mediumRegex = new RegExp(
                '^(?=.{7,})(((?=.*[A-Z])(?=.*[a-z]))|((?=.*[A-Z])(?=.*[0-9]))|((?=.*[a-z])(?=.*[0-9]))).*$',
                'g'
            );
            var enoughRegex = new RegExp('(?=.{6,}).*', 'g');

            var val = userlist.user_info.password;

            if (false == enoughRegex.test(val)) {
                $('#level').removeClass('pw-weak');
                $('#level').removeClass('pw-medium');
                $('#level').removeClass('pw-strong');
                $('#level').addClass(' pw-defule');
                //密码小于六位的时候，密码强度图片都为灰色
            } else if (strongRegex.test(val)) {
                $('#level').removeClass('pw-weak');
                $('#level').removeClass('pw-medium');
                $('#level').removeClass('pw-strong');
                $('#level').addClass(' pw-strong');
                //密码为八位及以上并且字母数字特殊字符三项都包括,强度最强
            } else if (mediumRegex.test(val)) {
                $('#level').removeClass('pw-weak');
                $('#level').removeClass('pw-medium');
                $('#level').removeClass('pw-strong');
                $('#level').addClass(' pw-medium');
                //密码为七位及以上并且字母、数字、特殊字符三项中有两项，强度是中等
            } else {
                $('#level').removeClass('pw-weak');
                $('#level').removeClass('pw-medium');
                $('#level').removeClass('pw-strong');
                $('#level').addClass('pw-weak');
                //如果密码为6为及以下，就算字母、数字、特殊字符三项都包括，强度也是弱的
            }
            return true;
        },
        authenticationChange:function(){
            userlist.dataukeyIdArr = [];
            console.log('daole');
            $.get(baseUrl + '/system/systemSet/ukey/unused', function(res) {
                $.each(res.data, function(index, item) {
                    userlist.dataukeyIdArr.push({
                        value: item.id,
                        name: item.name
                    });
                });
            });
        }
    },
    data: {
        usergroupPid: 'export', //导出使用
        dataBaseList: [],
        userCell: '',
        usergroups: '',
        dataukeyIdArr:[],
        user_info: {
            username: '',
            pwStatus: true,
            password: '',
            confirm_password: '',
            authenticationMethod: 1,
            name: '',
            userStrategy: 1,
            roles: '',
            email: '',
            phone: '',
            remark: '',
            ukeyId:''
        },
        pwLength: 6,
        //工具栏配置
        treeConfig: {
            id: 'usergroupTree', //树的id，用于提供API
            accordion: true,
            data: [],
            onSelect: function(node) {
                // 点击树节点时触发
                userlist.usergroupPid = node.id;
                gd.table('userlistTable').reload(false, { usergroupPid: node.id }, false);
            },
            onChange: function(nodes) {
                // 点击复选框时触发
            },
            ready: function(data) {
                //树的数据改变后，dom渲染完成后触发，data为树的数据
            }
        },
        treeWindowConfig: {
            id: 'windowTree',
            accordion: true,
            showCheckBox: true,
            linkable: false,
            accordion: false,
            data: [],
            onSelect: function(node) {},
            onChange: function(node) {
                var nodeObj = gd.tree('windowTree').getCheckedNodes();
                userlist.nodeObj = [];
                userlist.nodeObjName = [];
                nodeObj.map(function(obj) {
                    userlist.nodeObj.push(obj.id);
                    userlist.nodeObjName.push(obj.name);
                });

                userlist.usergroups = userlist.nodeObjName.join(';');
            },
            ready: function(data) {}
        },

        toolbarConfig: permissionSetUser,

        //表格配置
        tableConfig: {
            id: 'userlistTable',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            orderColumn: 'lastLoginTime',
            orderType: 'desc',
            filterImmediately: true,
            fillBlank: '--',
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/user/getUsersInPage',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.guid,
                            obj.status,
                            obj.username,
                            obj.name,
                            obj.usergroupsName,
                            obj.authenticationMethod,
                            obj.lastLoginTime,
                            obj.guid
                        ];
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
                    width: '38', //列宽
                    align: 'center', //对齐方式，默认left，与class不同，class只影响内容，align会影响内容和表头
                    change: function(checkedData, checkedRawData) {
                        //复选框改变，触发事件，返回所有选中的列的数据,第1个参数为加工后的表格数据，第2个参数为未加工的表格数据
                        setToolBtnDisable(userlist.toolbarConfig,'icon-delete',checkedData.length==0);
                    }
                },
                {
                    name: 'status',
                    head: '状态',
                    width: '90',
                    align: 'center',
                    filterName: 'status',
                    filters: [
                        {
                            label: '正常',
                            value: '11'
                        },
                        {
                            label: '停用',
                            value: '12'
                        },
                        {
                            label: '锁定',
                            value: '10'
                        }
                    ],
                    render: function(cell, row, raw) {
                        if (cell == '11') {
                            return '<span class="bar-status-mark status-mark-normal" data-value="11">正常</span>';
                        } else if (cell == '12') {
                            return '<span class="bar-status-mark status-mark-stop" data-value="12">停用</span>';
                        } else {
                            return '<span class="bar-status-mark status-mark-lock" data-value="10">锁定</span>';
                        }
                    }
                },
                {
                    name: 'username', //本列如果有排序或高级搜索，必须要有name
                    head: '用户名',
                    width: '140',
                    title: function(cell, row, raw) {
                        //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据,也可以直接传一个true,将以cell作为title
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        //自定义表格内容
                        return cell;
                    }
                },
                {
                    name: 'name',
                    head: '姓名',
                    width: '140',
                    //ellipsis: false，可以禁用text ellipsis,默认为true
                    title: function(cell, row, raw) {
                        //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据
                        return cell;
                    }
                },
                {
                    name: 'department',
                    head: '用户组',
                    title: function(cell, row, raw) {
                        //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据
                        return cell;
                    }
                },
                {
                    name: 'authenticationMethod',
                    head: '认证方式',
                    width: '240',
                    filterName: 'authenticationMethod',
                    filters: [
                        {
                            label: '密码',
                            value: '1'
                        },
                        // {
                        //     label: '密码+短信平台',
                        //     value: '2'
                        // },
                        {
                            label: '密码+第三方USBKEY',
                            value: '3'
                        }
                    ],
                    render: function(cell, row, raw) {
                        //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据
                        if (cell == 1) {
                            return '密码';
                        } else if (cell == 2) {
                            return '密码+短信平台';
                        } else {
                            return '密码+第三方USBKEY';
                        }
                    }
                },
                {
                    name: 'lastLoginTime',
                    head: '最近登录时间',
                    width: '180',
                    orderable: true,
                    show: true
                },
                {
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    width: 200,
                    operates: permissionSetUserTable
                }
            ]
        }
    }
});
