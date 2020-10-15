var localClient = 'http://127.0.0.1:33000';
var app = new Vue({
    el: '#app',
    data: {
        //工具栏配置
        toolbarConfig: [
            {
                type: 'button',
                icon: 'icon-system',
                title: '批量配置',
                disabled: true,
                action: function() {
                    batchConfig();
                }
            }
        ],
        treeConfig: {
            id: 'assetTypeTree',
            showCheckBox: true, //默认是false;显示checkbox
            data: [],
            onChange: function(nodes) {
                // 点击复选框时触发  返回所有选中复选框的数据
                var types = nodes
                    .map(function(m) {
                        return m.id;
                    })
                    .join(',');
                gd.table('grandTable').reload(1, { assetTypeIds: types });
            }
        },
        tableConfig: {
            id: 'grandTable', //给table一个id,调用gd.tableReload('demoTable');可重新加载表格数据并保持当前页码，gd.tableReload('demoTable'，1)，第二个参数可在加载数据时指定页码
            length: 50, //每页多少条,默认50，可选
            enableJumpPage: false, //启用跳页，默认false，可选
            enableLengthMenu: true, //启用可选择每页多少条，默认true，可选
            enablePaging: true, //启用分页,默认true，可选
            orderColumn: 'id', //排序列
            orderType: 'desc', //排序规则，desc或asc,默认desc
            fillBlank: '--',
            ajax: {
                //其它ajax参数同jquery
                url: baseUrl + '/granted/getGrantedsByCurrentUser4SSOInPage',
                //改变从服务器返回的数据给table
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                            obj.assetId,
                            obj.assettypeName,
                            obj.assetName,
                            obj.assetIp,
                            obj.accountName,
                            obj.assetId
                        ];
                    });
                    return data;
                },
                //请求参数
                data: {}
            },
            columns: [
                {
                    name: 'assetId',
                    type: 'checkbox',
                    width: '60',
                    class: 'assetId',
                    align: 'center',
                    change: function(data) {
                        app.toolbarConfig[0].disabled = data.length === 0;
                    }
                },
                {
                    name: 'assettypeName',
                    head: '设备类型',
                    filterName: 'assetTypeId',
                    filters: '#tree_box',
                    class: 'assettypeName',
                    title: function(cell, row, raw) {
                        return raw.assettypeName;
                    },
                    render: function(cell, row, raw) {
                        return '<img src="{0}/images/asset_type/{1}.png" alt="{2}" assettypeIcon="{3}" assetId="{4}" accountId="{5}">'.format(
                            baseUrl || '',
                            raw.assettypeIcon || '',
                            raw.assettypeName || '',
                            raw.assettypeIcon || '',
                            raw.assetId || '',
                            raw.accountId || ''
                        );
                    }
                },
                {
                    name: 'assetName',
                    head: '设备名称',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'assetIp',
                    head: '设备IP',
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell, row, raw) {
                        return cell;
                    }
                },
                {
                    name: 'accountName',
                    head: '账号',
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
                            icon: 'icon-system',
                            title: '配置', //设置图标title
                            action: function(cell, row, raw) {
                                loginConfig(raw);
                            }
                        },
                        {
                            icon: 'icon-quick-login',
                            title: '快速登录', //设置图标title
                            action: function(cell, row, raw) {
                                $.get({
                                    url:
                                        baseUrl +
                                        '/ssoConfiguration/findSSOConfigurationByAssetIdAndAccountIdWithCurrentUser',
                                    data: { assetId: raw.assetId, accountId: raw.accountId },
                                    async: false,
                                    success: function(msg) {
                                        if (msg.resultCode == 0) {
                                            var config = {
                                                assetId: raw.assetId,
                                                accountId: raw.accountId,
                                                configuration: msg.data.configuration
                                            };
                                            getOsaUser(config, raw);
                                        } else {
                                            gd.showError('登录失败！' + (msg.resultMsg || ''));
                                        }
                                    },
                                    error: function(xhr) {}
                                });
                            }
                        }
                    ]
                }
            ]
        }
    },
    mounted: function() {
        getAssetType();
    }
});

// 登录配置
function loginConfig(data, batchData) {
    var configApp = null;
    var validate = null;
    gd.showLayer({
        id: 'loginConfigWind',
        title: '登录配置',
        content: $('#temp_config_layer').html(),
        size: [580, 600],
        btn: [
            {
                text: '立即登录',
                enter: true,
                class: batchData ? 'none' : '',
                action: function(dom) {
                    if (!validate.valid()) {
                        return false;
                    }
                    var config = getConfig(data, configApp[configApp.assetType]);
                    getOsaUser(config, data);
                    dom.close();
                    return false;
                }
            },
            {
                text: '保存',
                action: function(dom) {
                    if (!validate.valid()) {
                        return false;
                    }
                    var config = getConfig(data, configApp[configApp.assetType]);
                    log(config);
                    if (batchData) {
                        var param = {
                            batchInfo: JSON.stringify(batchData),
                            configuration: config.configuration
                        };
                        gd.post(baseUrl + '/ssoConfiguration/batchSaveConfigureSSO4CurrentUser', param, function(msg) {
                            if (msg.resultCode == 0) {
                                gd.showSuccess('保存成功！');
                                dom.close();
                            } else {
                                gd.showError('保存失败！' + (msg.resultMsg || ''));
                            }
                        });
                    } else {
                        gd.post(baseUrl + '/ssoConfiguration/saveConfigureSSO4CurrentUser', config, function(msg) {
                            if (msg.resultCode == 0) {
                                gd.showSuccess('保存成功！');
                                dom.close();
                            } else {
                                gd.showError('保存失败！' + (msg.resultMsg || ''));
                            }
                        });
                    }
                    return false;
                }
            },
            {
                text: '取消',
                action: function(dom) {}
            }
        ],
        success: function() {
            configApp = new Vue({
                el: '#config_app',
                data: {
                    assetType: getAssetTypeByIcon(data.assettypeIcon),
                    batchData: batchData,
                    assetId: data.assetId,
                    assetIp: data.assetIp,
                    assetName: data.assetName,
                    assettypeIcon: data.assettypeIcon,
                    accountId: data.accountId,
                    accountName: data.accountName,
                    driverList: {}, //磁盘列表
                    shareDriverArr: [], //共享本地驱动器,数组
                    linux: {
                        logonMethod: '', //登录方式
                        protocol: '', //协议
                        logonTools: '', //登录工具
                        port: '', //端口
                        logonToolsType: '', //登录工具
                        openType: '' //打开形式
                    },
                    windows: {
                        logonMethod: '', //登录方式
                        protocol: '', //协议
                        port: '', //端口
                        resolution: '', //分辨率
                        shareDriver: [], //共享本地驱动器
                        shareShearPlate: '' //共享剪切版
                    },
                    net: {},
                    remoteApp: {
                        port: '', //端口
                        protocol: '', //协议
                        publishId: '', //应用程序发布器id
                        publishName: '', //应用程序发布器名称
                        ssoRuleId: '', //连接工具id
                        ssoRuleName: '' //连接工具名称
                    }
                },
                methods: {
                    linuxLogonToolsTypeChange: function() {
                        this[configApp.assetType].logonMethod = 'character';
                        this[configApp.assetType].protocol = 'ssh';
                        this[configApp.assetType].port = 22;
                    },
                    linuxLogonMethodChange: function(data) {
                        var map = {
                            character: 'ssh',
                            graphical: 'vnc'
                        };
                        this[configApp.assetType].protocol = map[data.value];
                        if (map[data.value] == 'ssh') {
                            this[configApp.assetType].port = 22;
                        } else if (map[data.value] == 'vnc') {
                            this[configApp.assetType].port = 5900;
                        }
                    },
                    protocolChange: function(e) {
                        var map = {
                            ssh: 22,
                            telnet: 23,
                            rdp: 3389,
                            vnc: 5900,
                            xwindow: 6000
                        };
                        Vue.set(configApp[configApp.assetType], 'port', map[$(e.currentTarget).val()]);
                    }
                },
                watch: {
                    shareDriverArr: function(val) {
                        if (getAssetTypeByIcon(data.assettypeIcon) == 'windows') {
                            configApp[configApp.assetType].shareDriver = val.join(';');
                            log(configApp[configApp.assetType].shareDriver);
                        }
                    }
                },
                mounted: function() {
                    gd.get(
                        baseUrl + '/ssoConfiguration/findSSOConfigurationByAssetIdAndAccountIdWithCurrentUser',
                        { assetId: this.assetId, accountId: this.accountId },
                        function(msg) {
                            if (msg.resultCode == 0) {
                                var configuration = JSON.parse(msg.data.configuration);
                                configApp[configApp.assetType] = configuration;
                                if (getAssetTypeByIcon(data.assettypeIcon) == 'windows') {
                                    if (configuration.shareDriver.trim()) {
                                        configApp.shareDriverArr = configuration.shareDriver.trim().split(';');
                                    }

                                    $.post({
                                        url: localClient + '/getdriver',
                                        dataType: 'JSON',
                                        success: function(msg) {
                                            //msg = JSON.parse(msg);
                                            if (msg.status == 0) {
                                                configApp.driverList = msg.drivers;
                                                for (var i = configApp.shareDriverArr.length; i > -1; i--) {
                                                    if (
                                                        Object.keys(configApp.driverList).indexOf(
                                                            configApp.shareDriverArr[i]
                                                        ) == -1
                                                    ) {
                                                        configApp.shareDriverArr.splice(i, 1);
                                                    }
                                                }
                                            } else {
                                                gd.showError('读取本地驱动器失败！');
                                            }
                                        },
                                        error: function(xhr) {
                                            gd.showError('读取本地驱动器失败！');
                                        }
                                    });
                                }
                                if (configApp[configApp.assetType].logonToolsType == 'web') {
                                    configApp[configApp.assetType].logonTools = 'XShell';
                                    configApp[configApp.assetType].openType = 'tab';
                                }
                            } else {
                                gd.showError('数据加载失败！' + (msg.resultMsg || ''));
                            }
                        }
                    );

                    validate = gd.validate('#config_app');
                }
            });
        }
    });
}
//获取配置
function getConfig(raw, conf) {
    var configuration = gd.clone(conf);
    if (configuration.logonToolsType == 'web') {
        //web方式不存在这两个参数
        delete configuration.logonTools;
        delete configuration.openType;
    }
    if (raw.assettypeIcon == 'windows' && configuration.protocol == 'vnc') {
        //windows vnc方式不存在这些参数
        delete configuration.shareDriver;
        delete configuration.shareShearPlate;
    }
    var config = {
        assetId: raw.assetId,
        accountId: raw.accountId,
        configuration: JSON.stringify(configuration)
    };
    return config;
}
//获取用户身份
function getOsaUser(config, raw) {
    var getOsaUserId = function() {
        $.post({
            url: baseUrl + '/session/connectWithSsoConfiguration',
            type: 'post',
            data: config,
            async: false,
            success: function(msg) {
                if (msg.resultCode == 0) {
                    remoteLogin(config, raw, msg.data);
                } else {
                    gd.showError('登录失败！' + (msg.resultMsg || ''));
                }
            },
            error: function(xhr) {
                gd.showError('登录失败！');
            }
        });
    };
    $.post({
        url: localClient + '/getunique',
        data: JSON.stringify({ assetId: config.assetId }),
        async: false,
        dataType: 'json',
        success: function(msg) {
            //config.devunique = JSON.parse(msg).devunique;
            config.devunique = msg.devunique;
            getOsaUserId();
        },
        error: function(xhr) {
            gd.showError('登录失败！');
        }
    });
}
//远程登录
function remoteLogin(config, raw, osauser) {
    var param = {};
    var configuration = JSON.parse(config.configuration);
    if (getAssetTypeByIcon(raw.assettypeIcon) == 'linux') {
        if (configuration.logonToolsType == 'web') {
            param.osauser = osauser;
            param.resolution = configuration.resolution;
            window.open(baseUrl + '/sso/remote?' + $.param(param));
        } else {
            //linux客户端登录
            param.sessionid = osauser;
            $.post({
                url: localClient + '/reqlinuxtool',
                data: JSON.stringify(param),
                success: function(msg) {
                    log(msg);
                },
                error: function(xhr) {
                    log(xhr);
                }
            });
        }
    } else if (getAssetTypeByIcon(raw.assettypeIcon) == 'windows') {
        if (configuration.protocol === 'rdp') {
            param.sessionid = osauser;
            $.post({
                url: localClient + '/startrdp',
                data: JSON.stringify(param),
                success: function(msg) {
                    log(msg);
                },
                error: function(xhr) {
                    log(xhr);
                }
            });
        } else {
            param.osauser = osauser;
            param.resolution = configuration.resolution;
            window.open(baseUrl + '/sso/remote?' + $.param(param));
        }
    } else if (getAssetTypeByIcon(raw.assettypeIcon) == 'remoteApp') {
        param.sessionid = osauser;
        $.post({
            url: localClient + '/' + configuration.url,
            data: JSON.stringify(param),
            success: function(msg) {
                log(msg);
            },
            error: function(xhr) {
                log(xhr);
            }
        });
    } else if (getAssetTypeByIcon(raw.assettypeIcon) == 'net') {
    }
}
function getAssetTypeByIcon(icon) {
    var type = '';
    var remoteApps = ['cs', 'bs', 'DB2', 'informix', 'mysql', 'oracle', 'SQL_srr', 'sybase'];
    if (icon == 'Linux') {
        type = 'linux';
    } else if (icon == 'windows') {
        type = 'windows';
    } else if (icon == 'net') {
        type = 'net';
    } else if (remoteApps.indexOf(icon) > -1) {
        type = 'remoteApp';
    }
    return type;
}
/**
 * 加载设备类型
 */
function getAssetType() {
    gd.get(baseUrl + '/asset/getAssetTypeTree', function(msg) {
        var treeData = msg.map(function(m) {
            m.expand = m.pId == null;
            return m;
        });
        gd.tree('assetTypeTree').setData(treeData);
    });
}
//批量配置
function batchConfig() {
    var data = [];
    $('.assetId input:checked').each(function(_, el) {
        var target = $(el)
            .closest('tr')
            .find('.assettypeName img');
        data.push({
            assetId: target.attr('assetId'),
            accountId: target.attr('accountId'),
            assettypeIcon: target.attr('assettypeIcon')
        });
    });
    if (data.length == 0) {
        gd.showError('请先选中设备！');
        return false;
    }
    var assettypeIcon = '';
    $.each(data, function(index, d) {
        if (index == 0) {
            assettypeIcon = d.assettypeIcon;
        }
        if (assettypeIcon !== d.assettypeIcon) {
            gd.showError('不同类型的设备不能进行批量配置！');
            return false;
        }
    });
    loginConfig(data[0], data);
}
