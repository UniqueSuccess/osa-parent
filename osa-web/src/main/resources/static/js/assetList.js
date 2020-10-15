var treeData = ''; //树结构
var addAssetVue = ''; // 新建编辑设备
var addRuleVue = ''; //新建规则
var editRuleVue = ''; //编辑规则
var typelistArr = []; //设备类型

// 权限
var permissionSetAsset = [];
var permissionSetAssetTable = [];

// 新建
if (permissionSetArr.indexOf('asset::add::asset') > -1) {
    permissionSetAsset.push({
        type: 'button',
        icon: 'icon-add',
        title: '添加',
        action: function() {
            $.get(baseUrl + '/asset/getAssetType', function(res) {
                typelistArr = [];
                $.each(res.data, function(index, item) {
                    typelistArr.push({
                        value: item.id,
                        name: item.name,
                        style: item.style
                    });
                });

                gd.showLayer({
                    id: 'addasset_layer',
                    title: '新建设备',
                    content: $('#allAssetBox').html(),
                    size: [775, 600],
                    btn: [
                        {
                            text: '确定',
                            enter: true, //响应回车
                            action: function(dom) {
                                var formBox = addAssetVue.idBox;
                                var validate = gd.validate('#' + formBox + 'Form', {});
                                var result = validate.valid(); //获得整体的校验结果

                                if (result) {
                                    var addObject = $('#' + addAssetVue.idBox + 'Asset form').serializeJSON();
                                    var idBoxArr = ['windows', 'net'];
                                    // var idBoxArr = ['windows', 'bs', 'net'];

                                    var extraObj = null;
                                    addObject.groupId = addAssetVue.asset_info.groupId;
                                    addObject.accounts = addAssetVue.accountArr;

                                    if (idBoxArr.indexOf(addAssetVue.idBox) < 0) {
                                        extraObj = {
                                            type: addObject.type,
                                            publish: addObject.publish,
                                            operationTool: addObject.operationTool
                                        };
                                        if (addAssetVue.idBox == 'db') {
                                            extraObj.dbName = addObject.dbName;
                                            extraObj.port = addObject.port;
                                        }
                                    }
                                    addObject.extra = extraObj;

                                    delete addObject.publish;
                                    delete addObject.operationTool;
                                    delete addObject.dbName;
                                    delete addObject.port;

                                    if (addAssetVue.idBox == 'windows') {
                                        if (addAssetVue.asset_info.isPublish) {
                                            addObject.isPublish = 1;
                                            addObject.ssoRules = addAssetVue.toolRuleArr;
                                        } else {
                                            addObject.isPublish = 0;
                                        }
                                    }

                                    $.ajax({
                                        type: 'post',
                                        url: baseUrl + '/asset/asset',
                                        data: JSON.stringify(addObject),
                                        contentType: 'application/json',
                                        dataType: 'json',
                                        success: function(res) {
                                            if (!res.resultCode) {
                                                gd.showSuccess('创建设备成功！');
                                                gd.table('assetlistTable').reload();
                                            } else {
                                                gd.showError(res.resultMsg);
                                            }
                                            $('#addAssetBox').hide();
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
                                $('#addAssetBox,#addRuleBox,#addAccountBox').hide();
                            }
                        }
                    ],
                    success: function(dom) {
                        //参数为当前窗口dom对象
                        assetVueFunction();
                        gd.tree('assetgroupsWinTree').setData(treeData);
                        addAssetVue.typeChange(typelistArr[0].value);
                    },
                    end: function(dom) {
                        //参数为当前窗口dom对象
                        $('#addAssetBox,#addRuleBox,#addAccountBox').hide();
                    }
                });
            });
        }
    });
}

// 编辑
if (permissionSetArr.indexOf('asset::update::asset') > -1) {
    permissionSetAssetTable.push({
        icon: 'icon-edit',
        title: '编辑', //设置图标title
        action: function(cell, row, raw) {
            $.ajax({
                url: baseUrl + '/asset/asset/' + cell,
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.resultCode == '0') {
                        var raw = res.data;
                        typelistArr = [];
                        $.get(baseUrl + '/asset/getAssetType', function(res) {
                            $.each(res.data, function(index, item) {
                                typelistArr.push({
                                    value: item.id,
                                    name: item.name,
                                    style: item.style
                                });
                            });
                            gd.showLayer({
                                id: 'editasset_layer',
                                title: '编辑设备',
                                content: $('#allAssetBox').html(),
                                size: [775, 600],
                                btn: [
                                    {
                                        text: '确定',
                                        enter: true, //响应回车
                                        action: function(dom) {
                                            var validate = gd.validate('#addAssetBox', {});
                                            var result = validate.valid(); //获得整体的校验结果

                                            if (result) {
                                                var addObject = $(
                                                    '#' + addAssetVue.idBox + 'Asset form'
                                                ).serializeJSON();
                                                var idBoxArr = ['windows', 'net'];
                                                // var idBoxArr = ['windows', 'bs', 'net'];
                                                var extraObj = null;
                                                addObject.groupId = addAssetVue.asset_info.groupId;
                                                addObject.accounts = addAssetVue.accountArr;

                                                if (idBoxArr.indexOf(addAssetVue.idBox) < 0) {
                                                    extraObj = {
                                                        type: addObject.type,
                                                        publish: addObject.publish,
                                                        operationTool: addObject.operationTool
                                                    };
                                                    if (addAssetVue.idBox == 'db') {
                                                        extraObj.dbName = addObject.dbName;
                                                        extraObj.port = addObject.port;
                                                    }
                                                }

                                                addObject.extra = extraObj;

                                                delete addObject.publish;
                                                delete addObject.operationTool;
                                                delete addObject.dbName;
                                                delete addObject.port;

                                                addObject.id = cell;

                                                if (addAssetVue.idBox == 'windows') {
                                                    if (addAssetVue.asset_info.isPublish) {
                                                        addObject.isPublish = 1;
                                                        addObject.ssoRules = addAssetVue.toolRuleArr;
                                                    } else {
                                                        addObject.isPublish = 0;
                                                    }
                                                }

                                                $.ajax({
                                                    type: 'put',
                                                    url: baseUrl + '/asset/asset',
                                                    data: JSON.stringify(addObject),
                                                    contentType: 'application/json',
                                                    dataType: 'json',
                                                    success: function(res) {
                                                        if (!res.resultCode) {
                                                            gd.showSuccess(res.resultMsg);
                                                            gd.table('assetlistTable').reload();
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
                                        action: function() {
                                            $('#addAssetBox,#addRuleBox,#addAccountBox').hide();
                                        }
                                    }
                                ],
                                success: function(dom) {
                                    //参数为当前窗口dom对象

                                    assetVueFunction();
                                    gd.tree('assetgroupsWinTree').setData(treeData);
                                    (addAssetVue.accountArr = raw.accounts),
                                        (addAssetVue.asset_info = {
                                            name: raw.name,
                                            ip: raw.ip,
                                            account: raw.account,
                                            password: raw.password,
                                            type: raw.type,
                                            groupId: raw.groupId,
                                            remark: raw.remark,
                                            encode: raw.encode,
                                            groupName: raw.groupName,
                                            checkboxAccount: ''
                                        });
                                    addAssetVue.typeChange(addAssetVue.asset_info.type,'edit');

                                    if (addAssetVue.idBox == 'db' || addAssetVue.idBox == 'cs' || addAssetVue.idBox == 'bs') {
                                        // 应用程序发布器+连接工具
                                        addAssetVue.csPostToolListVal = raw.extra.publish;

                                        addAssetVue.csPostToolChange(addAssetVue.csPostToolListVal);
                                        addAssetVue.csPostToolRule = raw.extra.operationTool;
                                    }

                                    if (addAssetVue.idBox == 'db') {
                                        addAssetVue.asset_info.dbName = raw.extra.dbName;
                                        addAssetVue.asset_info.port = raw.extra.port;
                                    }

                                    if (addAssetVue.idBox == 'windows') {
                                        addAssetVue.asset_info.isPublish = raw.isPublish;
                                        if (addAssetVue.asset_info.isPublish) {
                                            $('#appPostRule').show();
                                        }
                                        addAssetVue.propertyArr = raw.ssoRules;
                                        addAssetVue.propertyRuleArr = raw.ssoRules.rule;
                                        addAssetVue.toolRuleArr = raw.ssoRules;
                                    }
                                },
                                end: function(dom) {
                                    //参数为当前窗口dom对象
                                    $('#addAssetBox,#addRuleBox,#addAccountBox').hide();
                                }
                            });
                        });
                    }
                }
            });
        }
    });
}

// 删除
if (permissionSetArr.indexOf('asset::delete::asset') > -1) {
    permissionSetAssetTable.push({
        icon: 'icon-delete',
        text: '删除',
        title: '删除',
        action: function(cell, row, raw) {
            var delCon;
            if (raw.granted) {
                delCon = '该设备存在授权，确定要删除设备授权和设备？';
            } else {
                delCon = '确定要删除吗？';
            }
            var dom = gd.showConfirm({
                id: 'wind',
                content: delCon,
                btn: [
                    {
                        text: '删除',
                        class: 'gd-btn-danger',
                        enter: true,
                        action: function(dom) {
                            $.ajax({
                                url: baseUrl + '/asset/asset/' + cell,
                                type: 'DELETE',
                                contentType: 'application/json',
                                dataType: 'json',
                                success: function(res) {
                                    if (res.resultCode == '0') {
                                        // 删除带授权的设备
                                        gd.showSuccess(res.resultMsg);
                                        gd.table('assetlistTable').reload();
                                    } else {
                                        gd.showError(res.resultMsg, { time: 5000 });
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

// 导入
if (permissionSetArr.indexOf('asset::import::asset') > -1) {
    permissionSetAsset.push({
        type: 'button',
        icon: 'icon-import',
        title: '导入',
        action: function() {
            var importData = null;
            gd.showLayer({
                id: 'importDevice',
                title: '导入设备',
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
                                url: baseUrl + '/asset/import',
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
                                        gd.table('assetlistTable').reload(1);
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
                                location.href = baseUrl + '/asset/import/template';
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
if (permissionSetArr.indexOf('asset::export::asset') > -1) {
    permissionSetAsset.push({
        type: 'button',
        icon: 'icon-export',
        title: '导出',
        action: function() {
            var exportData = gd.table('assetlistTable').getAjaxParam(true);


            var allCheckedData = gd.table('assetlistTable').getCheckedData();
            var exportIds = '';
            if(allCheckedData.length > 0){
                exportIds = [];
                for (var i = 0; i < allCheckedData.length; i++) {
                    exportIds.push(allCheckedData[i][0]);
                }
                exportIds = exportIds.join(',');
            }

            location.href = baseUrl + '/asset/export?' + $.param(exportData)+'&assetIds=' + exportIds;

        }
    });
}

// 批量删除
if (permissionSetArr.indexOf('asset::delete::batchasset') > -1) {
    permissionSetAsset.push({
        type: 'button',
        icon: 'icon-delete',
        title: '删除',
        disabled:true,
        action: function() {
            var allCheckedData = gd.table('assetlistTable').getCheckedData();
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
                id: 'assetDeleteWindow',
                content: '确定要删除吗?',
                btn: [
                    {
                        text: '删除',
                        class: 'gd-btn-danger',
                        enter: true, // 响应回车
                        disabled: false,
                        action: function(dom) {
                            $.ajax({
                                url: baseUrl + '/asset/asset/delete?ids=' + deleteIds,
                                type: 'DELETE',
                                success: function(data) {
                                    if (data.resultCode == '0') {
                                        gd.showSuccess(data.resultMsg);
                                        gd.table('assetlistTable').reload(1);
                                    } else {
                                        gd.showSuccess('删除失败');
                                    }
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

permissionSetAsset.push({
    type: 'searchbox',
    placeholder: '',
    action: function(val) {
        gd.table('assetlistTable').reload(false, { searchStr: val }, false);
    }
});

var addAssetMethods = {
    // 批量
    accountChange: function(data) {
        if (addAssetVue.asset_info.checkboxAccount) {
            $('#accountCheckTable input[type=checkbox]').prop('checked', true);
            setToolBtnDisable(addAssetVue.toolbarWinConfig,'icon-delete',false);
        } else {
            $('#accountCheckTable input[type=checkbox]').prop('checked', false);
            setToolBtnDisable(addAssetVue.toolbarWinConfig,'icon-delete',true);
        }
    },

    // 每个
    accountEveryChange: function(data) {
        var length = $('#accountCheckTable input[type=checkbox]').length;
        var checkLength = $('#accountCheckTable input[type=checkbox]:checked').length;
        if (length == checkLength) {
            addAssetVue.asset_info.checkboxAccount = true;
        } else {
            addAssetVue.asset_info.checkboxAccount = false;
        }
        setToolBtnDisable(addAssetVue.toolbarWinConfig,'icon-delete',length==0);
    },

    // 账号删除
    deleteAccount: function(data) {
        addAssetVue.accountArr.splice(data, 1);
    },

    // 删除规则
    deleteRule: function(data) {
        addAssetVue.toolRuleArr.splice(data, 1);
    },

    // 编辑规则
    editRule: function(data) {
        gd.showLayer({
            id: 'editRuleBox_layer',
            title: '编辑规则',
            content: $('#addRuleBox').html(),
            size: [720, 600],
            btn: [
                {
                    text: '确定',
                    enter: true, //响应回车
                    action: function(dom) {
                        var obj = {
                            toolType: editRuleVue.rule_info.toolType,
                            toolTypeName: editRuleVue.rule_info.toolTypeName,
                            name: editRuleVue.rule_info.toolName,
                            rule: editRuleVue.rule_info.ruleCon
                        };
                        // addAssetVue.$set(addAssetVue.toolRuleArr,data,obj);
                        addAssetVue.$set(addAssetVue.toolRuleArr[data], 'toolType', editRuleVue.rule_info.toolType);
                        addAssetVue.$set(
                            addAssetVue.toolRuleArr[data],
                            'toolTypeName',
                            editRuleVue.rule_info.toolTypeName
                        );
                        addAssetVue.$set(addAssetVue.toolRuleArr[data], 'name', editRuleVue.rule_info.toolName);
                        addAssetVue.$set(addAssetVue.toolRuleArr[data], 'rule', editRuleVue.rule_info.ruleCon);
                    }
                },
                {
                    text: '取消',
                    action: function() {
                        $('#addRuleBox').hide();
                    }
                }
            ],
            success: function(dom) {
                editRuleVue = new Vue({
                    el: '#addRule',
                    methods: editRuleMethods,
                    data: {
                        propertyNameList: addAssetVue.propertyNameList,
                        toolTypeList: addAssetVue.toolTypeList,
                        propertyArr: addAssetVue.toolRuleArr[data].ruleAttrs,
                        propertyRuleArr: addAssetVue.toolRuleArr[data].rule.split(';'),
                        rule_info: {
                            propertyNickname: '',
                            propertyName: '',
                            propertyVal: '',
                            toolType: addAssetVue.toolRuleArr[data].toolType,
                            toolTypeName: addAssetVue.toolRuleArr[data].toolTypeName,
                            toolName: addAssetVue.toolRuleArr[data].name,
                            ruleCon: addAssetVue.toolRuleArr[data].rule
                        }
                    }
                });
            },
            end: function(dom) {}
        });
    },

    // 应用发布器开关
    postOnOff: function(data) {
        if (addAssetVue.asset_info.isPublish) {
            $('#appPostRule').show();
        } else {
            $('#appPostRule').hide();
        }
    },

    // 工具类型改变
    typeChange: function(data,operateType) {
        for (var i = 0; i < addAssetVue.typeList.length; i++) {
            if (addAssetVue.typeList[i].value == data) {
                addAssetVue.idBox = addAssetVue.typeList[i].style;
                addAssetVue.asset_info.type = addAssetVue.typeList[i].value;
            }
        }
        $('.assetForm,#appPostRule').hide();
        $('#' + addAssetVue.idBox + 'Asset').show();
        if (addAssetVue.idBox == 'cs' || addAssetVue.idBox == 'db' || addAssetVue.idBox == 'bs') {
            // 应用程序发布器
            addAssetVue.csPostToolList = [];
            addAssetVue.csPostToolListVal = '';
            $.get(baseUrl + '/asset/publishList', function(res) {
                if (res.data.length != 0) {
                    $.each(res.data, function(index, item) {
                        addAssetVue.csPostToolList.push({
                            value: item.id,
                            name: item.name
                        });
                    });
                    if(!operateType){
                        addAssetVue.csPostToolListVal = addAssetVue.csPostToolList[0].value;
                        addAssetVue.csPostToolListId = addAssetVue.csPostToolList[0].value;
                        addAssetVue.csPostToolChange(addAssetVue.csPostToolListId);
                    }
                }
            });
        }

        if (addAssetVue.idBox == 'windows') {
            // $("#appPostRule").show();
            // 工具类型
            $.get(baseUrl + '/asset/sso/ruleType', function(res) {
                $.each(res.data, function(index, item) {
                    addAssetVue.toolTypeList.push({
                        value: item.value,
                        name: item.name
                    });
                });
            });

            $.get(baseUrl + '/asset/sso/ruleAttr', function(res) {
                $.each(res.data, function(index, item) {
                    addAssetVue.propertyNameList.push({
                        value: item.value,
                        name: item.name,
                        nickname: item.nickname
                    });
                });
            });
        } else {
            $('#appPostRule').hide();
        }
    },
    csPostToolChange: function(data) {
        addAssetVue.csPostToolListId = data;
        addAssetVue.operationToolList = [];
        addAssetVue.csPostToolRule = '';
        $.get(baseUrl + '/asset/getOperationToolList/' + addAssetVue.csPostToolListId, function(res) {
            $.each(res.data, function(index, item) {
                addAssetVue.operationToolList.push({
                    value: item.id,
                    ruleName: item.name
                });
            });
        });
    }
};

var addRuleMethods = {
    addProperty: function(data) {
        var addObject = $('#addAccount_layer form').serializeJSON();
        var ruleAttrs = [];
        var obj = {
            name: addRuleVue.rule_info.propertyName,
            content: addRuleVue.rule_info.propertyVal
        };
        addRuleVue.propertyNameList.map(function(data, index) {
            if (data.name == addRuleVue.rule_info.propertyName) {
                addRuleVue.rule_info.propertyNickname = data.nickname;
            }
        });
        var objStr = addRuleVue.rule_info.propertyNickname + '=' + addRuleVue.rule_info.propertyVal;

        addRuleVue.propertyArr.push(obj);
        addRuleVue.propertyRuleArr.push(objStr);
        addRuleVue.rule_info.ruleCon = addRuleVue.propertyRuleArr.join(';');
    },
    // 向上排序
    upProperty: function(data) {
        if (data == 0) {
            return false;
        } else {
            var tempKey = addRuleVue.propertyArr[data];
            addRuleVue.$set(addRuleVue.propertyArr, data, addRuleVue.propertyArr[data - 1]);
            addRuleVue.$set(addRuleVue.propertyArr, data - 1, tempKey);
            var tempRuleKey = addRuleVue.propertyRuleArr[data];
            addRuleVue.$set(addRuleVue.propertyRuleArr, data, addRuleVue.propertyRuleArr[data - 1]);
            addRuleVue.$set(addRuleVue.propertyRuleArr, data - 1, tempRuleKey);
            addRuleVue.rule_info.ruleCon = addRuleVue.propertyRuleArr.join(';');
        }
    },
    // 向下排序
    downProperty: function(data) {
        if (data == addRuleVue.propertyArr.length) {
            return false;
        } else {
            var tempKey = addRuleVue.propertyArr[data];
            addRuleVue.$set(addRuleVue.propertyArr, data, addRuleVue.propertyArr[data + 1]);
            addRuleVue.$set(addRuleVue.propertyArr, data + 1, tempKey);
            var tempRuleKey = addRuleVue.propertyRuleArr[data];
            addRuleVue.$set(addRuleVue.propertyRuleArr, data, addRuleVue.propertyRuleArr[data + 1]);
            addRuleVue.$set(addRuleVue.propertyRuleArr, data + 1, tempRuleKey);
            addRuleVue.rule_info.ruleCon = addRuleVue.propertyRuleArr.join(';');
        }
    },
    // 属性删除
    deleteProperty: function(data) {
        addRuleVue.propertyArr.splice(data, 1);
        addRuleVue.propertyRuleArr.splice(data, 1);
        addRuleVue.rule_info.ruleCon = addRuleVue.propertyRuleArr.join(';');
    }
};

var editRuleMethods = {
    addProperty: function(data) {
        var addObject = $('#addAccount_layer form').serializeJSON();
        var obj = {
            name: editRuleVue.rule_info.propertyName,
            content: editRuleVue.rule_info.propertyVal
        };
        editRuleVue.propertyNameList.map(function(data, index) {
            if (data.name == editRuleVue.rule_info.propertyName) {
                editRuleVue.rule_info.propertyNickname = data.nickname;
            }
        });
        var objStr = editRuleVue.rule_info.propertyNickname + '=' + editRuleVue.rule_info.propertyVal;
        editRuleVue.propertyArr.push(obj);
        editRuleVue.propertyRuleArr.push(objStr);
        editRuleVue.rule_info.ruleCon = editRuleVue.propertyRuleArr.join(';');
    },
    // 向上排序
    upProperty: function(data) {
        if (data == 0) {
            return false;
        } else {
            var tempKey = editRuleVue.propertyArr[data];
            editRuleVue.$set(editRuleVue.propertyArr, data, editRuleVue.propertyArr[data - 1]);
            editRuleVue.$set(editRuleVue.propertyArr, data - 1, tempKey);
            var tempRuleKey = editRuleVue.propertyRuleArr[data];
            editRuleVue.$set(editRuleVue.propertyRuleArr, data, editRuleVue.propertyRuleArr[data - 1]);
            editRuleVue.$set(editRuleVue.propertyRuleArr, data - 1, tempRuleKey);
            editRuleVue.rule_info.ruleCon = editRuleVue.propertyRuleArr.join(';');
        }
    },
    // 向下排序
    downProperty: function(data) {
        if (data == editRuleVue.propertyArr.length) {
            return false;
        } else {
            var tempKey = editRuleVue.propertyArr[data];
            editRuleVue.$set(editRuleVue.propertyArr, data, editRuleVue.propertyArr[data + 1]);
            editRuleVue.$set(editRuleVue.propertyArr, data + 1, tempKey);
            var tempRuleKey = editRuleVue.propertyRuleArr[data];
            editRuleVue.$set(editRuleVue.propertyRuleArr, data, editRuleVue.propertyRuleArr[data + 1]);
            editRuleVue.$set(editRuleVue.propertyRuleArr, data + 1, tempRuleKey);
            editRuleVue.rule_info.ruleCon = editRuleVue.propertyRuleArr.join(';');
        }
    },
    // 属性删除
    deleteProperty: function(data) {
        editRuleVue.propertyArr.splice(data, 1);
        editRuleVue.propertyRuleArr.splice(data, 1);
        editRuleVue.rule_info.ruleCon = editRuleVue.propertyRuleArr.join(';');
    }
};

function assetVueFunction() {
    addAssetVue = new Vue({
        el: '#addAssetBox',
        mounted: function() {},
        methods: addAssetMethods,
        data: {
            idBox: '',
            toolTypeList: [],
            propertyNameList: [],
            csPostToolList: [],
            csPostToolListVal: '',
            csPostToolRule: '',
            operationToolList: [],
            typeList: typelistArr,
            accountArr: [],
            toolRuleArr: [],
            propertyArr: [],
            asset_info: {
                name: '',
                ip: '',
                type: typelistArr[0].value,
                groupId: '',
                account: '',
                passorde: '',
                remark: '',
                encode: '',
                groupName: '',
                isPublish: false,
                checkboxAccount: false,
                accountArr: [],
                dbName: '',
                port: ''
            },
            treeWindowConfig: {
                id: 'assetgroupsWinTree',
                data: [],
                onSelect: function(node) {
                    addAssetVue.asset_info.groupId = node.id;
                    addAssetVue.asset_info.groupName = node.name;
                },
                onChange: function(nodes) {},
                ready: function(data) {}
            },

            toolbarWinConfig: [
                {
                    type: 'button',
                    icon: 'icon-add',
                    title: '添加',
                    action: function() {
                        var contentHtml;
                        if (addAssetVue.idBox == 'windows' || addAssetVue.idBox == 'net') {
                            contentHtml = $('#addAccountBox').html();
                        } else {
                            contentHtml = $('#addAccountBox_other').html();
                        }
                        addAssetVue.addAccountDom = gd.showLayer({
                            id: 'addAccount_layer',
                            title: '添加设备账号',
                            content: contentHtml,
                            size: [460, 300],
                            btn: [
                                {
                                    text: '确定',
                                    enter: true, //响应回车
                                    action: function(dom) {
                                        var validate = gd.validate('#accountForm', {});
                                        var result = validate.valid(); //获得整体的校验结果
                                        if (result) {
                                            var addObject = $('#addAccount_layer form').serializeJSON();
                                            if(addAssetVue.accountArr.some(function(obj){
                                                return obj.username==addObject.username;
                                            })){
                                                gd.showWarning('账号已存在！');
                                                return false;
                                            }else{
                                                addAssetVue.accountArr.push(addObject);
                                            }
                                        } else {
                                            return false;
                                        }
                                    }
                                },
                                {
                                    text: '取消',
                                    action: function() {
                                        $('#addAccountBox').hide();
                                    }
                                }
                            ],
                            success: function(dom) {},
                            end: function(dom) {}
                        });
                    }
                },
                {
                    type: 'button',
                    icon: 'icon-delete',
                    title: '删除',
                    disabled:true,
                    action: function() {
                        var dom = gd.showConfirm({
                            id: 'wind',
                            content: '确定要删除吗?',
                            btn: [
                                {
                                    text: '删除',
                                    class: 'gd-btn-danger',
                                    enter: true, //响应回车
                                    action: function(dom) {
                                        var tabArr = [];
                                        $('#accountCheckTable input[type=checkbox]:checked').map(function(index, item) {
                                            tabArr.push($(item).attr('tab'));
                                        });
                                        for (var i = tabArr.length - 1; i >= 0; i--) {
                                            addAssetVue.accountArr.splice(tabArr[i], 1);
                                        }
                                        // addAssetVue.accountEveryChange();
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
                // {
                //     type: 'searchbox',
                //     placeholder: "",
                //     action: function (val) {
                //         gd.table('assetlistTable').reload(false,{searchStr:val},false)
                //     }
                // }
            ],
            toolbarRuleConfig: [
                {
                    type: 'button',
                    icon: 'icon-add',
                    title: '添加',
                    action: function() {
                        gd.showLayer({
                            id: 'addRuleBox_layer',
                            title: '新建规则',
                            content: $('#addRuleBox').html(),
                            size: [720, 600],
                            btn: [
                                {
                                    text: '确定',
                                    enter: true, //响应回车
                                    action: function(dom) {
                                        var validate = gd.validate('#addRuleForm', {});
                                        var result = validate.valid(); //获得整体的校验结果
                                        if (result) {
                                            addAssetVue.toolTypeList.map(function(item, index) {
                                                if (item.value == addRuleVue.rule_info.toolType) {
                                                    addRuleVue.rule_info.toolTypeName = item.name;
                                                }
                                            });

                                            var obj = {
                                                toolType: addRuleVue.rule_info.toolType,
                                                toolTypeName: addRuleVue.rule_info.toolTypeName,
                                                name: addRuleVue.rule_info.toolName,
                                                rule: addRuleVue.rule_info.ruleCon,
                                                ruleAttrs: addRuleVue.propertyArr
                                            };

                                            // addAssetVue.propertyArr = addRuleVue.propertyArr;
                                            addAssetVue.toolRuleArr.push(obj);
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
                                addRuleVue = new Vue({
                                    el: '#addRule',
                                    methods: addRuleMethods,
                                    data: {
                                        propertyNameList: addAssetVue.propertyNameList,
                                        toolTypeList: addAssetVue.toolTypeList,
                                        propertyArr: [],
                                        propertyRuleArr: [],
                                        rule_info: {
                                            propertyNickname: '',
                                            propertyName: '',
                                            propertyVal: '',
                                            toolType: '',
                                            toolTypeName: '',
                                            toolName: '',
                                            ruleCon: ''
                                        }
                                    }
                                });
                            },
                            end: function(dom) {}
                        });
                    }
                },
                {
                    type: 'button',
                    icon: 'icon-import',
                    title: '导入',
                    action: function() {
                        var importData = null;
                        var ruleLayer = gd.showLayer({
                            id: 'importRule',
                            title: '导入规则',
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
                                        gd.showLoading('正在导入中...');
                                        $.ajax({
                                            url: baseUrl + '/asset/import/rule',
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
                                                    gd.closeLayer(ruleLayer);
                                                    gd.showSuccess('导入成功！');
                                                    //导入成功后添加数据到表格
                                                    var obj = {
                                                        toolType: msg.data.toolType,
                                                        toolTypeName: msg.data.toolTypeName,
                                                        name: msg.data.name,
                                                        rule: msg.data.rule,
                                                        ruleAttrs: msg.data.ruleAttrs
                                                    };
                                                    addAssetVue.toolRuleArr.push(obj);
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
                                        $('#importRule .mode-choose').hide();
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
                                            location.href = baseUrl + '/asset/import/rule/template';
                                        }
                                    }
                                });
                            },
                            end: function() {}
                        });
                    }
                }
                // {
                //     type: 'searchbox',
                //     placeholder: "",
                //     action: function (val) {
                //         gd.table('assetlistTable').reload(false,{searchStr:val},false)
                //     }
                // }
            ]
        },
    });
}

$(function() {
    $.ajax({
        url: baseUrl + '/assetgroup/getAssetgroupListByPid',
        dataType: 'json',
        success: function(data) {
            treeData = data;
            gd.tree('assetgroupsTree').setData(data);
        }
    });

    $.ajax({
        url: baseUrl + '/asset/getAssetTypeTree',
        dataType: 'json',
        success: function(data) {
            gd.tree('assetTypeTree').setData(data);
        }
    });
});

var assetlist = new Vue({
    el: '#contentDiv',
    methods: {},
    data: {
        treeConfig: {
            id: 'assetgroupsTree',
            accordion: true,
            data: [],
            onSelect: function(node) {
                gd.table('assetlistTable').reload(false, { groupId: node.id }, false);
            },
            onChange: function(nodes) {},
            ready: function(data) {}
        },
        assetTypeTreeConfig: {
            id: 'assetTypeTree',
            showCheckBox: true,
            data: [],
            onSelect: function(n) {},
            onChange: function(n) {
                var ids = n.map(function(node) {
                    return node.id;
                });
                gd.table('assetlistTable').reload(1, { assetType: ids.join(',') });
            }
        },
        toolbarConfig: permissionSetAsset,
        tableConfig: {
            id: 'assetlistTable',
            length: 50,
            curPage: 1,
            lengthMenu: [20, 30, 50, 100],
            enableJumpPage: false,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            fillBlank: '--',
            excludeSearchKey:['groupId'],
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/asset/getAssetsInPage',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [obj.id, obj.typeName, obj.name, obj.ip, obj.groupName, obj.remark, obj.id];
                    });
                    return data;
                },
                data: {
                    groupId: '',
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
                        setToolBtnDisable(assetlist.toolbarConfig,'icon-delete',checkedData.length==0);
                    }
                },
                {
                    name: 'assetType',
                    head: '类型',
                    width: '130',
                    title: true,
                    filters: '#assetTypeTreeBox',
                    render: function(cell, row, raw) {
                        return '<img src="' + baseUrl + '/images/asset_type/' + raw.icon + '.png" >';
                    }
                },
                {
                    name: 'name', //本列如果有排序或高级搜索，必须要有name
                    head: '设备名称',
                    width: '200',
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
                    name: 'ip',
                    head: '设备IP',
                    width: '180',
                    //ellipsis: false，可以禁用text ellipsis,默认为true
                    title: function(cell, row, raw) {
                        //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据
                        return cell;
                    }
                },
                {
                    name: 'groupName',
                    head: '设备组',
                    width: '280',
                    title: function(cell, row, raw) {
                        //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据
                        return cell;
                    }
                },
                {
                    name: 'remark',
                    head: '备注',
                    title: true,
                    render: function(cell, row, raw) {
                        //设置title，cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据
                        return cell;
                    }
                },
                {
                    name: 'operate',
                    head: '操作',
                    align: 'center',
                    width: 120,
                    operates: permissionSetAssetTable
                }
            ]
        }
    }
});
