Array.prototype.remove = function(val) { 
    var index = this.indexOf(val); 
    if (index > -1) { 
        this.splice(index, 1); 
    } 
}

var UKeyUniqueId;//usbkey标识
var UKeyDeviceId;//设备标识
var UKeySignTime;//注册时间
var int;
var i = 0;

// ukey启动
function startUp(){
    var $b = $('<a></a>').attr("href", 'PsdOpenUkey://');
    $b[0].click();
}

// ukey启动检测
function testingUkey(){
    $("#usbkeyId").val('');
    $.post({
        url: 'http://127.0.0.1:33000/getukeyinfo',
        success: function (msg) {
            clearInterval(int);
            // msg = JSON.parse(msg);
            if(msg.status == 0){
                if(msg.ukey.length > 1){
                    gd.showError('只允许同一时刻插入一个USBKEY');
                    return false;
                }else{
                    UKeyUniqueId = msg.ukey[0].unqiueid;
                    UKeyDeviceId = msg.ukey[0].deviceid;
                    UKeySignTime = msg.ukey[0].signtime;

                    systemConfigList.USBKeyInfo.ukeyId = UKeyUniqueId;
                    $("#authenticationError").html('');
                    if(!UKeyDeviceId){
                        // ukey设备未注册
                       $("#authenticationError").html('<label class="error">请确认USBKey设备是否已注册</label>')
                    }
                    gd.showSuccess('启动成功')
                }
                
            }else{ // ukey启动正常，设备未插入
                $("#authenticationError").html('<label class="error">请确认是否插入USBKey</label');
            }
        },
        error:function(msg){
            // 首次启动
            i++;
            // 检测失败再次启动ukey
            if(i<2){
                startUp();
                int = setInterval(testingUkey(),1000);
            }else{
               clearInterval(int);
               systemConfigList.USBKeyInfo.ifUsbkeyDownload = true;
               systemConfigList.USBKeyInfo.ifUsbkeyStart = true
               gd.showError('请先下载工具并安装')
            }
        }
    })
}


function initChart(item) {
    var cpuColor = '';
    var memoryColor = '';
    var diskColor = '';
    var mysqlColor = '';

    var cpuValue = Math.round(item.cpu * 100); //cpu
    var memoryValue = Math.round((item.memory.usedMem * 100) / item.memory.totalMem); //内存
    var mysqlValue = Math.round(item.mySQLUsage); //mysql
    var diskValue = Math.round((item.dbUsage.used * 100) / item.dbUsage.total); //硬盘

    systemConfigList.cpu_value = cpuValue;
    systemConfigList.mysql_value = mysqlValue;
    systemConfigList.memory_value = memoryValue;
    systemConfigList.disk_value = diskValue;

    // cpu
    if (cpuValue <= 49) {
        cpuColor = '#6785cf';
    } else if (cpuValue > 49 && cpuValue <= 80) {
        cpuColor = '#f5a623';
    } else {
        cpuColor = '#f33018';
    }

    // mysql
    if (mysqlValue <= 49) {
        mysqlColor = '#6785cf';
    } else if (mysqlValue > 49 && mysqlValue <= 80) {
        mysqlColor = '#f5a623';
    } else {
        mysqlColor = '#f33018';
    }

    // 内存
    if (memoryValue <= 49) {
        memoryColor = '#6785cf';
    } else if (memoryValue > 49 && memoryValue <= 80) {
        memoryColor = '#f5a623';
    } else {
        memoryColor = '#f33018';
    }

    // 硬盘
    if (diskValue <= 49) {
        diskColor = '#6785cf';
    } else if (diskValue > 49 && diskValue <= 80) {
        diskColor = '#f5a623';
    } else {
        diskColor = '#f33018';
    }

    $('#cpu_value').css('color', cpuColor);
    $('#memory_value').css('color', memoryColor);
    $('#disk_value').css('color', diskColor);
    $('#mysql_value').css('color', mysqlColor);

    // cpu
    var cpuChart = {
        type: 'gauge', // pie 环形图|| gauge 计量图
        tooltip: {
            show: false
        },
        x: '50%',
        y: '50%',
        radius: '30%',
        lineWidth: '4%',
        max: 100, // 映射总值
        data: [
            {
                value: cpuValue // 映射计量值
            }
        ],
        label: {
            show: false
        },
        capType: 'round',
        selectedStyle: {
            color: 'white',
            borderWidth: 10
        },
        color: [cpuColor],
        labelCoverTitle: false,
        backgroundArc: '#F2F2F6'
    };
    var cpuChart = new DonutChart('cpu_chart', cpuChart);

    cpuChart.init(function(result) {});

    // mysql
    var mysqlChart = {
        type: 'gauge', // pie 环形图|| gauge 计量图
        tooltip: {
            show: false
        },
        x: '50%',
        y: '50%',
        radius: '30%',
        lineWidth: '4%',
        max: 100, // 映射总值
        data: [
            {
                value: mysqlValue // 映射计量值
            }
        ],
        label: {
            show: false
        },
        capType: 'round',
        selectedStyle: {
            color: 'white',
            borderWidth: 10
        },
        color: [mysqlColor],
        labelCoverTitle: false,
        backgroundArc: '#F2F2F6'
    };
    var mysqlChart = new DonutChart('mysql_chart', mysqlChart);

    mysqlChart.init(function(result) {});

    // 内存
    var memoryChart = {
        type: 'gauge', // pie 环形图|| gauge 计量图
        tooltip: {
            show: false
        },
        x: '50%',
        y: '50%',
        radius: '30%',
        lineWidth: '4%',
        max: 100, // 映射总值
        data: [
            {
                value: memoryValue // 映射计量值
            }
        ],
        label: {
            show: false
        },
        capType: 'round',
        selectedStyle: {
            color: 'white',
            borderWidth: 10
        },
        color: [memoryColor],
        labelCoverTitle: false,
        backgroundArc: '#F2F2F6'
    };
    var memoryChart = new DonutChart('memory_chart', memoryChart);
    memoryChart.init(function(result) {});

    // 硬盘
    var diskChart = {
        type: 'gauge', // pie 环形图|| gauge 计量图
        tooltip: {
            show: false
        },
        x: '50%',
        y: '50%',
        radius: '30%',
        lineWidth: '4%',
        max: 100, // 映射总值
        data: [
            {
                value: diskValue // 映射计量值
            }
        ],
        label: {
            show: false
        },
        capType: 'round',
        selectedStyle: {
            color: 'white',
            borderWidth: 10
        },
        color: [diskColor],
        labelCoverTitle: false,
        backgroundArc: '#F2F2F6'
    };
    var diskChart = new DonutChart('disk_chart', diskChart);
    diskChart.init(function(result) {});
}

var allowSaveNetconfig = false;
var allowSavePlatform = false;
var allowSaveServer = false;
var allowDb = false;
var allowAccess = false;
var allowUsbkey = false;

// 权限
if(permissionSetArr.indexOf('netconfig::select') > -1){
    allowSaveNetconfig = true; //网络配置
}
if(permissionSetArr.indexOf('platform::select') > -1){
    allowSavePlatform = true;//管控平台
}
if(permissionSetArr.indexOf('server::select') > -1){
    allowSaveServer = true;//服务器
}
if(permissionSetArr.indexOf('backup::select') > -1){
    allowDb = true; // 数据库
}
if(permissionSetArr.indexOf('accessControl::select') > -1){
    allowAccess = true
    // 准入配置
}
if(permissionSetArr.indexOf('USBKey::select') > -1){
    // usbkey配置
    allowUsbkey = true
}

var systemConfigList = new Vue({
    el: '#contentDiv',
    methods: {
        //切换tag
        tabChange: function(data) {
            if (data.label == '网络配置') {
                this.meshConfig();
            } else if (data.label == '管控平台') {
                // this.getSyslogData();
            } else if (data.label == '服务器') {
                this.getServerData();
            } else if (data.label == '数据库') {
                gd.table('backup').reload();
            } else if(data.label == '准入配置'){
                this.getAccessAllocation();
            }else if(data.label == 'USBKey配置'){
                testingUkey();// 检测
                gd.table('usbkeyTable').reload();
            }
        },

        meshConfig: function() {
            // 网口配置
            $.get(baseUrl + '/systemSetting/netconfig', function(res) {
                if (res.resultCode == '0') {
                    var data = res.data;
                    if (data.length > 0) {
                        $.each(systemConfigList.eth_info, function(index, item) {
                            item.value = data[index];
                        });
                    }
                    // systemConfigList.eth_info_before_edit = _.cloneDeep(systemConfigList.eth_info);
                }
            });
        },

        saveNetsetConfig: function() {
            var validate = gd.validate('#eth_config', {
                rules: [
                    {
                        name: 'netmask',
                        msg: '子网掩码不合法',
                        valid: function(value, el) {
                            var exp = /^(254|252|248|240|224|192|128|0)\.0\.0\.0|255\.(254|252|248|240|224|192|128|0)\.0\.0|255\.255\.(254|252|248|240|224|192|128|0)\.0|255\.255\.255\.(254|252|248|240|224|192|128|0)$/;
                            var result = value == '' || exp.test(value);
                            return result;
                        }
                    }
                ]
            });

            var result = validate.valid();

            if (result) {
                var dom = gd.showConfirm({
                    id: 'save_eth',
                    content: '保存后，系统将会重启，确定要保存并重启系统吗?',
                    btn: [
                        {
                            text: '确定',
                            class: 'gd-btn-danger', //也可以自定义类
                            enter: true, //响应回车
                            action: function(dom) {
                                // gd.showLoading();
                                var netConfigs = [];
                                $.each(systemConfigList.eth_info, function(index, item) {
                                    var eth_info_value = systemConfigList.eth_info[index].value;
                                    var addr = eth_info_value.addr ? eth_info_value.addr : '0.0.0.0';
                                    var mask = eth_info_value.mask ? eth_info_value.mask : '0.0.0.0';
                                    var gateway = eth_info_value.gateway ? eth_info_value.gateway : '0.0.0.0';
                                    netConfigs.push({
                                        addr: addr,
                                        mask: mask,
                                        gateway: gateway
                                    });
                                });

                                $.ajax({
                                    type: 'post',
                                    url: baseUrl + '/systemSetting/netconfig',
                                    data:{'netConfigs':JSON.stringify(netConfigs)},
                                    success: function(res) {
                                        if (res.resultCode == '0') {
                                            gd.showSuccess('网口设置保存成功,系统正在重启');
                                        } else {
                                            gd.showError(res.resultMsg);
                                        }
                                    }
                                });
                            }
                        },
                        {
                            text: '取消',
                            action: function() {}
                        }
                    ]
                });
            }
        },

        getAccessAllocation:function(){

            $.get(baseUrl + '/system/systemSet/accessControl', function(res) {
                if (res.resultCode == '0') {
                    var data = res.data;
                    systemConfigList.accessAllocationInfo.mac = data.mac;
                    systemConfigList.accessAllocationInfo.status = data.status;
                    systemConfigList.accessAllocationInfo.ports = data.ports;
                    systemConfigList.accessAllocationInfo.ips = data.ips;
                }
            });
        },

        // 删除准入控制IP段
        deleteIp:function(index){
            systemConfigList.accessAllocationInfo.ips.splice(index,1);
        },

        deletePort:function(index){
            systemConfigList.accessAllocationInfo.ports.splice(index,1);
        },

        saveAccessAllocation:function(){
            var validate = gd.validate('#accessAllocationForm');
            var result = validate.valid();
            if(result){
                var params={
                    status:systemConfigList.accessAllocationInfo.status,
                    mac:systemConfigList.accessAllocationInfo.mac,
                    ips:systemConfigList.accessAllocationInfo.ips,
                    ports:systemConfigList.accessAllocationInfo.ports
                }
                $.ajax({
                    type: 'post',
                    url: baseUrl + '/system/systemSet/accessControl',
                    data: JSON.stringify(params),
                    contentType: "application/json",
                    success: function(res) {
                        if (!res.resultCode) {
                            gd.showSuccess('操作成功！');
                        } else {
                            gd.showError(res.resultMsg);
                        }
                    },
                    error: function(xhr, errorText, errorStatus) {}
                });
            }else{
                return false
            }
        },

        // 服务器
        getServerData: function() {
            // 设备负载
            $.get(baseUrl + '/system/systemSet/computerInfo', function(res) {
                if (res.resultCode == '0') {
                    var data = res.data;
                    initChart(data);
                }
            });
            // 网口状态
            $.get(baseUrl + '/system/systemSet/netStatInfo', function(res) {
                if (res.resultCode == '0') {
                    var objData = res.data;
                    $.each(systemConfigList.serverEthStatus, function(index, item) {
                        item.value = objData[item.name];
                    });
                }
            });

            // 第三方服务器
            $.get(baseUrl + '/system/systemSet/serverInfo', function(res) {
                if (res.resultCode == '0') {
                    // 日志服务器
                    systemConfigList.logServerName = res.data.name;
                    systemConfigList.logServerUrl = res.data.url;

                    // 邮箱服务器
                    systemConfigList.emailServerPort = res.data.port;
                    systemConfigList.emailServerSend = res.data.from;
                    systemConfigList.emailServerPwd = res.data.password;
                    systemConfigList.emailServerAddress = res.data.serverAddress;
                }
            });
        },

        saveServer: function() {
            var addObject = $('#serverConfigForm').serializeJSON();
            var addObjectEmail = $('#serverEmailForm').serializeJSON();
            var validate = gd.validate('#serverConfigForm');
            var validateEmail = gd.validate('#serverEmailForm',{
                rules: [
                    {
                        name: 'requiredRule',
                        msg: '必填项',
                        valid: function(value, el) {
                            return true
                        }
                    }
                ]
            });
            var result = validate.valid();
            var resultEmail = validateEmail.valid();
            if (result && resultEmail) {
                var object = $.extend(addObject, addObjectEmail);
                $.ajax({
                    type: 'post',
                    url: baseUrl + '/system/systemSet/serverInfo',
                    data: object,
                    success: function(res) {
                        if (!res.resultCode) {
                            gd.showSuccess('操作成功！');
                        } else {
                            gd.showError(res.resultMsg);
                        }
                    },
                    error: function(xhr, errorText, errorStatus) {}
                });
            }
        },

        sendEmail:function(){
            var validate = gd.validate('#serverEmailForm',{
                rules: [
                    {
                        name: 'requiredRule',
                        msg: '必填项',
                        valid: function(value, el) {
                            if (!value) {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                ]
            })

            var result = validate.valid();
            if(result){
                var addObject = $('#serverEmailForm').serializeJSON();
                addObject.to = systemConfigList.receiveEmail;
                $.ajax({
                    type: 'post',
                    url: baseUrl + '/system/systemSet/mail/test',
                    data: addObject,
                    success: function(res) {
                        if (!res.resultCode) {
                            gd.showSuccess('操作成功！');
                        } else {
                            gd.showError(res.resultMsg);
                        }
                    },
                    error: function(xhr, errorText, errorStatus) {}
                });

            }else{
                return false
            }
        },

        //限制只输入数字
        validNumber: function(e) {
            var target = e.currentTarget;
            var value = target.value;
            setTimeout(function() {
                target.value = Math.abs(parseInt(value) || 0);
            }, 0);
        },
        //选择上传文件
        selectPlatformFile: function(e) {
            var target = e.currentTarget;
            $(target)
                .closest('.j-filebox')
                .find('input[type="file"]')
                .click();
        },
        //文件选择完毕，回显
        selectPlatformFileDone: function(e) {
            var files = e.currentTarget.files;
            var btn = $(e.currentTarget)
                .closest('.j-filebox')
                .find('.j-btn-upload');
            var input = $(e.currentTarget)
                .closest('.j-filebox')
                .find('.j-uploadfile-name');
            var form = $(e.currentTarget)
                .closest('.j-filebox')
                .find('.j-uploadfile-form')
                .get(0);
            var mb = e.currentTarget.getAttribute('maxsize') || 100;
            if (files.length) {
                //限制大小
                if (files[0].size > Math.pow(1024, 2) * mb) {
                    gd.showError('文件大小不得超过' + mb + 'M');
                    input.val('');
                    form.reset();
                    btn.prop('disabled', true);
                } else {
                    input.val(files[0].name);
                    btn.prop('disabled', false);
                }
            } else {
                input.val('');
                form.reset();
                btn.prop('disabled', true);
            }
        },
        //上传文件
        uploadPlatformFile: function(e) {
            var form = $(e.currentTarget)
                .closest('.j-filebox')
                .find('.j-uploadfile-form')
                .get(0);
            var formData = new FormData(form);
            gd.showLoading('上传中...');
            $.post({
                url: baseUrl + '/system/systemSet/uploadClientPackage',
                cache: false,
                data: formData,
                processData: false,
                contentType: false
            })
                .done(function(msg) {
                    gd.showSuccess('上传成功！');
                })
                .fail(function(xhr) {
                    gd.showError('上传失败！');
                })
                .always(function() {
                    gd.closeLoading();
                });
        },
        //上传背景图
        uploadBgImg: function(e) {
            var form = $(e.currentTarget)
                .closest('.j-filebox')
                .find('.j-uploadfile-form')
                .get(0);
            var formData = new FormData(form);
            gd.showLoading('上传中...');
            $.post({
                url: baseUrl + '/system/systemSet/background/upload',
                cache: false,
                data: formData,
                processData: false,
                contentType: false
            })
                .done(function(msg) {
                    gd.showSuccess('上传成功！');
                    setTimeout(function() {
                        systemConfigList.backgroundImgT = Math.random();
                    }, 500);
                })
                .fail(function(xhr) {
                    gd.showError('上传失败！');
                })
                .always(function() {
                    gd.closeLoading();
                });
        },
        //重置背景图
        bgImgRestore: function() {
            gd.showConfirm({
                id: 'wind',
                content: '确定要还原登录背景吗?',
                btn: [
                    {
                        text: '确定',
                        class: 'gd-btn-danger', //也可以自定义类
                        enter: true, //响应回车
                        action: function(dom) {
                            $.get(baseUrl + '/system/systemSet/background/restore')
                                .done(function(msg) {
                                    setTimeout(function() {
                                        systemConfigList.backgroundImgT = Math.random();
                                    }, 500);
                                })
                                .fail(function(xhr) {
                                    gd.showError('还原失败！');
                                });
                        }
                    },
                    {
                        text: '取消',
                        action: function() {}
                    }
                ]
            });
        },
        //保存管控平台数据
        savePlatform: function() {
            if (!window.platformValid.valid()) {
                return;
            }
            var param = {
                security: systemConfigList.security,
                access: systemConfigList.access,
                approval: systemConfigList.approval,
                custom: systemConfigList.custom
            };
            gd.post(baseUrl + '/system/systemSet/platform/save', JSON.stringify(param), function(msg) {
                if (msg.resultCode == 0) {
                    gd.showSuccess('保存成功！');
                } else {
                    gd.showError('保存失败！' + msg.resultMsg);
                }
            });
        },
        downloadTool:function(){
            var src = baseUrl + '/client/download?packageType=1';
            window.location.href = src;
        },
        getKey:function(){
            testingUkey()
        },
        startTool:function(){
            startUp();
            setTimeout(testingUkey(),100);
        },
        addUsbkey:function(){
            $("#authenticationError").html('');
            var usbkeyName = systemConfigList.USBKeyInfo.usbkeyName;

            if(!UKeyUniqueId){
                $("#authenticationError").html('<label class="error">请检测USBKey,自动识别标识</label>').show();
                return false;
            }
            if(!usbkeyName){
                $("#authenticationError").html('<label class="error">不可为空</label>').show();
                return false;
            }
            // 添加usbkey
            $.ajax({
                type:'post',
                url:baseUrl +'/system/systemSet/ukey',
                data:JSON.stringify({name:usbkeyName,sign:UKeyUniqueId}),
                contentType: "application/json",
                success:function(result){
                    if(result.resultCode == 0){
                        systemConfigList.USBKeyInfo.ukeyId = '';
                        systemConfigList.USBKeyInfo.usbkeyName = '';
                        gd.table('usbkeyTable').reload();
                    }else{
                        gd.showError(msg.resultMsg)
                    }
                }
            });
        }
    },
    data: {
        // 设备负载
        cpu_value: '',
        mysql_value: '',
        memory_value: '',
        disk_value: '',
        // 第三方服务器
        logServerName: '',
        logServerUrl: '',

        emailServerPort:'',
        emailServerSend:'',
        emailServerPwd:'',
        emailServerAddress:'',
        receiveEmail:'',

        // 准入配置
        accessAllocationInfo:{
            'mac':'',
            'ports':[],
            'ips':[],
            'status':false
        },
        admissionControlBar:[
            {
                type: 'button',
                icon: 'icon-add',
                title: '新建',
                action: function () {
                    var addIpSegmentVue= '';
                    gd.showLayer({
                        id: 'addIpSegment_layer',
                        title: '新建IP段',
                        content: $('#addIpSegmentBox').html(),
                        size: [550, 350],
                        btn: [
                            {
                                text: '确定',
                                enter: true,//响应回车
                                action: function (dom) {

                                    var validate = gd.validate('#addIpSegmentCon form');
                                    var result = validate.valid(); //获得整体的校验结果
                                    if(result){
                                        var addObject = $('#addIpSegmentCon form').serializeJSON();
                                        var obj={
                                            'ip':addObject.startIp + '-' + addObject.endIp,
                                            'remark':addObject.remark
                                        }
                                        systemConfigList.accessAllocationInfo.ips.push(obj)
                                    }else{
                                        return false
                                    }

                                }
                            },{
                                text: '取消',
                                action: function (dom) {}
                            }
                        ],
                        success:function(){
                            addIpSegmentVue = new Vue({
                                el:'#addIpSegmentCon',
                                data:{}
                            })
                        }
                    })
                }
            },
            {
                type: 'button',
                icon: 'icon-delete',
                title: '删除',
                action: function () {
                    var deleteIds = [];
                    $(".gd-table-body input[type='checkbox']:checked").map(function(index,item){
                        deleteIds.push($(item).data('index'));
                    })
                    var arrNew = [];
                    systemConfigList.accessAllocationInfo.ips.map(function(item,index){
                        if(deleteIds.indexOf(index) <= -1){
                            arrNew.push(item)
                        }
                    })
                    systemConfigList.accessAllocationInfo.ips = arrNew
                }
            }
        ],
        portControlBar:[
            {
                type: 'button',
                icon: 'icon-add',
                title: '新建',
                action: function () {
                    var addAssetVue= '';
                    gd.showLayer({
                        id: 'addPort_layer',
                        title: '新建端口',
                        content: $('#addPortBox').html(),
                        size: [500, 290],
                        btn: [
                            {
                                text: '确定',
                                enter: true,
                                action: function (dom) {
                                    var validate = gd.validate('#addPortCon form');
                                    var result = validate.valid();
                                    if(result){
                                        var addObject = $('#addPortCon form').serializeJSON();
                                        var obj={
                                            'start':addObject.startPort,
                                            'end':addObject.endPort
                                        }
                                        systemConfigList.accessAllocationInfo.ports.push(obj)
                                    }else{
                                        return false
                                    }
                                }
                            },
                            {
                                text: '取消',
                                action: function (dom) {
                                }
                            },
                        ],
                        success:function(){
                            addAssetVue = new Vue({
                                el:'#addPortCon',
                                data:{}
                            })
                        }
                    })
                }
            },
            {
                type: 'button',
                icon: 'icon-delete',
                title: '删除',
                action: function () {
                    var deleteIds = [];
                    $(".gd-table-body input[type='checkbox']:checked").map(function(index,item){
                        deleteIds.push($(item).data('index'));
                    })
                    var arrNew = [];
                    systemConfigList.accessAllocationInfo.ports.map(function(item,index){
                        if(deleteIds.indexOf(index) <= -1){
                            arrNew.push(item)
                        }
                    })
                    systemConfigList.accessAllocationInfo.ports = arrNew
                }
            }
        ],

        // usbkey
        USBKeyInfo:{
            'ifUsbkeyDownload':false,
            'ifUsbkeyStart':false,
            'usbkeyName':'',
            'ukeyId':''
        },
        usbkeyTableConfig: {
            id: 'usbkeyTable',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: true,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            lazy: true,
            fillBlank: '--',
            ajax: {
                url: baseUrl + '/system/systemSet/ukey',
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                        obj.name,
                        obj.sign,
                        obj.createTime,
                        obj.uname,
                        obj.id
                        ];
                    });
                    return data;
                }
            },
            columns: [
                {
                    head: 'USBKey名称',
                    show: true,
                    width:300,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell) {
                        return cell;
                    }
                },
                {
                    head: 'USBKey标识',
                    width: 430,
                    ellipsis: true,
                    render: function(cell) {
                        return cell;
                    }
                },
                {
                    head: '创建时间',
                },
                {
                    head: '绑定用户',
                },
                {
                    name: 'operate',
                    head: '操作',
                    width: 120,
                    align: 'center',
                    operates: [
                        {
                            icon: 'icon-delete',
                            title: '删除', 
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
                                                    url: baseUrl + '/system/systemSet/ukey/' + cell,
                                                    type: 'DELETE',
                                                    contentType: 'application/json', //设置请求参数类型为json字符串
                                                    dataType: 'json',
                                                    success: function(res) {
                                                        if (res.resultCode == '0') {
                                                            gd.table('usbkeyTable').reload();
                                                        } else {
                                                            gd.showError(res.resultMsg);
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
                                })
                            }
                        }
                    ]
                }
            ]
        },

        
        // 权限
        allowSaveNetconfig:allowSaveNetconfig,
        allowSavePlatform:allowSavePlatform,
        allowSaveServer:allowSaveServer,


        eth_info: [
            {
                name: 'eth0',
                gatewayReg: 'gateway0',
                value: {
                    addr: '',
                    mask: '',
                    gateway: ''
                }
            },
            {
                name: 'eth1',
                gatewayReg: 'gateway1',
                value: {
                    addr: '',
                    mask: '',
                    gateway: ''
                }
            },
            {
                name: 'eth2',
                gatewayReg: 'gateway2',
                value: {
                    addr: '',
                    mask: '',
                    gateway: ''
                }
            },
            {
                name: 'eth3',
                gatewayReg: 'gateway2',
                value: {
                    addr: '',
                    mask: '',
                    gateway: ''
                }
            }
        ],
        eth_info_before_edit: [],

        serverEthStatus: [
            {
                name: 'eth0',
                value: ''
            },
            {
                name: 'eth1',
                value: ''
            },
            {
                name: 'eth2',
                value: ''
            },
            {
                name: 'eth3',
                value: ''
            }
        ],
        backUpToolbarConfig: [
            {
                type: 'button',
                icon: 'icon-add',
                title: '添加',
                action: function() {
                    var dom = gd.showLayer({
                        id: 'addBackupConBox_layer',
                        title: '实时备份',
                        size: [520, 300],
                        content: $('#addBackupConBox').html(),
                        btn: [
                            {
                                text: '确定',
                                enter: true,
                                action: function(dom) {
                                    var validate = gd.validate('#addBackupConBox_layer form', {});
                                    var result = validate.valid();
                                    if (result) {
                                        var addObject = $('#addBackupConBox_layer form').serializeJSON();
                                        $.ajax({
                                            type: 'post',
                                            url: baseUrl + '/backUp/addActualBackUp',
                                            data: addObject,
                                            success: function(res) {
                                                if (!res.resultCode) {
                                                    gd.showSuccess('操作成功！');
                                                    gd.table('backup').reload();
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
                                action: function() {}
                            }
                        ],

                        success: function(dom) {
                            var addBackupConBox = new Vue({
                                el: '#addBackupCon',
                                data: {}
                            });
                        }
                    });
                }
            },
            {
                type: 'button',
                icon: 'icon-collect',
                title: '自动备份',
                action: function() {
                    var backupAdd;
                    var dom = gd.showLayer({
                        id: 'auto_backup_layer',
                        title: '自动备份',
                        size: [762, 395],
                        content: $('#auto_backup_box').html(),
                        btn: [
                            {
                                text: '确定',
                                enter: true,
                                action: function(dom) {

                                    if(backupAdd.autoBackupInfo.status){
                                        var validate = gd.validate('#auto_backup form');
                                        var result = validate.valid();
                                        if(result){
                                            if(!backupAdd.autoBackupInfo.time){
                                                gd.showTip($('.gd-timepicker'), '请选择时间', {
                                                    id: 'tips',//如果传一个id，将关闭之前相同id的tip
                                                    time: 3000,//默认在3秒内关闭，可以自定义关闭时间，0为不自动关闭
                                                    position: 'top'//设置位置，默认自动
                                                });
                                                return false;
                                            }
                                            var params = {
                                                cycle:backupAdd.autoBackupInfo.cycle,
                                                day: backupAdd.autoBackupInfo.cycle == 'week' ? backupAdd.autoBackupInfo.weekDay : backupAdd.autoBackupInfo.monthDay,
                                                status:'1',
                                                time:backupAdd.autoBackupInfo.time,
                                            };
                                            $.ajax({
                                                url:baseUrl + '/backUp/addAutoBackUp',
                                                type:'post',
                                                data:params,
                                                success:function(res){
                                                    if(!res.resultCode){
                                                        gd.showSuccess('自动备份设置成功！');
                                                    }else{
                                                        gd.showError(res.resultMsg);
                                                    }
                                                }
                                            })
                                        }
                                    }else{
                                        $.post(baseUrl + '/backUp/addAutoBackUp/',{
                                                status:'0',
                                            },function (res) {
                                                if(!res.resultCode){
                                                    gd.showSuccess('自动备份设置成功！');
                                                }else{
                                                    gd.showError(res.resultMsg);
                                                }
                                            })
                                    }

                                }
                            },
                            {
                                text: '取消',
                                action: function() {}
                            }
                        ],

                        success: function(dom) {
                            backupAdd = new Vue({
                                el: '#auto_backup',
                                methods: {
                                    backupCycleClick: function(val) {
                                        // if(this.beforeEditautoBackupInfo.cycle == 'month'){
                                        //     if(val == 'week'){
                                        //         systemConfigList.autoBackupInfo.weekDay = '1';
                                        //     }else if(val == 'month'){
                                        //         systemConfigList.autoBackupInfo.monthDay = systemConfigList.beforeEditautoBackupInfo.monthDay;
                                        //     }
                                        // }
                                        // if(this.beforeEditautoBackupInfo.cycle == 'week'){
                                        //     if(val == 'week'){
                                        //         systemConfigList.autoBackupInfo.weekDay = systemConfigList.beforeEditautoBackupInfo.weekDay;
                                        //     }else if(val == 'month'){
                                        //         systemConfigList.autoBackupInfo.monthDay = '';
                                        //     }
                                        // }
                                    },
                                    isAutoBackup: function(val) {
                                        if (val) {
                                            $('.set_input_stat input').removeAttr('disabled');
                                            $('.set_input_stat .icon-close').css('pointer-events', '');
                                            $('#autoBackupMonthDayInput').attr('gd-validate', 'valueRange required');
                                        } else {
                                            $('.set_input_stat input').attr('disabled', 'disabled');
                                            $('.set_input_stat .icon-close').css('pointer-events', 'none');
                                            $('#autoBackupMonthDayInput').attr('gd-validate', 'valueRange');
                                        }
                                    }
                                },
                                data: {
                                    beforeEditautoBackupInfo: {},
                                    autoBackupCycleOption: [
                                        {
                                            name: '每周',
                                            value: 'week'
                                        },
                                        {
                                            name: '每月',
                                            value: 'month'
                                        }
                                    ],
                                    autoBackupWeek: [
                                        {
                                            name: '周一',
                                            value: '1'
                                        },
                                        {
                                            name: '周二',
                                            value: '2'
                                        },
                                        {
                                            name: '周三',
                                            value: '3'
                                        },
                                        {
                                            name: '周四',
                                            value: '4'
                                        },
                                        {
                                            name: '周五',
                                            value: '5'
                                        },
                                        {
                                            name: '周六',
                                            value: '6'
                                        },
                                        {
                                            name: '周日',
                                            value: '7'
                                        }
                                    ],
                                    autoBackupInfo: {
                                        cycle: '',
                                        day: '',
                                        status: '',
                                        time: '',
                                        weekDay: '1',
                                        monthDay: ''
                                    },
                                    timePointPickerConfig: {
                                        step: 5,
                                        value: '',
                                        change: function(time) {
                                            backupAdd.autoBackupInfo.time = time;
                                        }
                                    }
                                }
                            });

                            $.ajax({
                                url: baseUrl + '/backUp/getAutoBackUp',
                                type: 'get',
                                success: function(res) {
                                    var data = res.data;
                                    if (data.status == '1') {
                                        backupAdd.autoBackupInfo = {
                                            cycle: data.cycle,
                                            day: '',
                                            status: true,
                                            time: data.time,
                                            weekDay: '',
                                            monthDay: ''
                                        };
                                        if (data.cycle === 'week') {
                                            backupAdd.autoBackupInfo.day = data.day;
                                            backupAdd.autoBackupInfo.weekDay = data.day;
                                        } else {
                                            backupAdd.autoBackupInfo.day = data.day;
                                            backupAdd.autoBackupInfo.monthDay = data.day;
                                        }

                                        backupAdd.timePointPickerConfig.value = backupAdd.autoBackupInfo.time;

                                    } else {
                                        backupAdd.autoBackupInfo = {
                                            cycle: 'week',
                                            day: '',
                                            status: false,
                                            time: '',
                                            weekDay: '1',
                                            monthDay: ''
                                        };

                                        backupAdd.$nextTick(function () {
                                            $('.set_input_stat input').attr('disabled','disabled');
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            },
            {
                type: 'button',
                icon: 'icon-storage',
                title: '数据库维护',
                action: function() {
                    var dom = gd.showLayer({
                        id: 'dbMaintenanceBox_layer',
                        title: '数据库维护',
                        size: [520, 300],
                        content: $('#dbMaintenanceBox').html(),
                        btn: [
                            {
                                text: '确定',
                                action: function(dom) {
                                    var validate = gd.validate('#dbMaintenanceBox_layer form', {});
                                    var result = validate.valid();
                                    if (result) {
                                        var addObject = $('#dbMaintenanceBox_layer form').serializeJSON();
                                        $.ajax({
                                            type: 'post',
                                            url: baseUrl + '/backUp/dbClean',
                                            data: addObject,
                                            success: function(res) {
                                                if (!res.resultCode) {
                                                    gd.showSuccess('操作成功！');
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
                                action: function() {}
                            }
                        ],

                        success: function(dom) {
                            var dbMaintenanceBox = new Vue({
                                el: '#dbMaintenance',
                                data: {
                                    dbMaintenanceDay: ''
                                }
                            });

                            $.ajax({
                                type: 'get',
                                url: baseUrl + '/backUp/getDbCleanSet',
                                success: function(res) {
                                    if (res.resultCode == '0') {
                                        dbMaintenanceBox.dbMaintenanceDay = res.data.day;
                                    }
                                },
                                error: function(xhr, errorText, errorStatus) {}
                            });
                        }
                    });
                }
            },
            {
                type: 'button',
                icon: 'icon-export',
                title: '导出',
                disabled:true,
                action: function() {
                    var selectData = gd.table('backup').getCheckedData();
                    var ids = '';
                    for (var i = 0; i < selectData.length; i++) {
                        ids += selectData[i][0] + ';';
                    }
                    window.location.href = baseUrl + '/backUp/exportBackup?ids=' + ids;
                }
            },
            {
                type: 'button',
                icon: 'icon-delete',
                title: '删除',
                disabled:true,
                action: function() {
                    var selectData = gd.table('backup').getCheckedData();
                    var dom = gd.showConfirm({
                        id: 'delete_user',
                        content: '确定要删除吗?',
                        btn: [
                            {
                                text: '删除',
                                class: 'gd-btn-danger', //也可以自定义类
                                enter: true, //响应回车
                                action: function(dom) {
                                    var ids = '';
                                    var names = '';
                                    for (var i = 0; i < selectData.length; i++) {
                                        ids += selectData[i][0] + ';';
                                        names += selectData[i][1] + ';';
                                    }
                                    $.ajax({
                                        type:'post',
                                        url:baseUrl + '/backUp/deleteBackUp',
                                        data:{'ids':ids},
                                        success:function(res){
                                            if(res.resultCode == 0){
                                                gd.showSuccess('操作成功');
                                                gd.table('backup').reload()
                                            }else{
                                                gd.showError(res.resultMsg)
                                            }
                                        }
                                    })
                                }
                            },
                            {
                                text: '取消',
                                action: function() {}
                            }
                        ]
                    });
                }
            }
        ],
        //表格配置
        backUpTableConfig: {
            id: 'backup',
            length: 50,
            curPage: 1,
            lengthMenu: [10, 30, 50, 100],
            enableJumpPage: true,
            enableLengthMenu: true,
            enablePaging: true,
            columnResize: true,
            lazy: true,
            fillBlank: '--',
            ajax: {
                url: baseUrl + '/backUp/getBackUpList',
                dataSrc: function(data) {
                    data.rows = data.rows.map(function(obj) {
                        return [
                        obj.id,
                        obj.status,
                        obj.name,
                        obj.createTime,
                        obj.mark,
                        obj.id
                        ];
                    });
                    return data;
                }
            },
            columns: [
                {
                    name: 'checkbox',
                    type: 'checkbox',
                    width: '60', //列宽
                    align: 'center',
                    change: function(data) {
                        //复选框改变，触发事件，返回所有选中的列的数据
                        setToolBtnDisable(systemConfigList.backUpToolbarConfig,'icon-delete,icon-export',data.length==0);
                    }
                },
                {
                    head: '状态',
                    show: true,
                    width: 150,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell) {
                        if (cell == '1') {
                            return '已完成';
                        }
                    }
                },
                {
                    head: '备份名称',
                    show: true,
                    title: function(cell, row, raw) {
                        return cell;
                    },
                    render: function(cell) {
                        return cell;
                    }
                },
                {
                    head: '备份时间',
                    width: 260,
                    render: function(cell) {
                        return cell;
                    }
                },
                {
                    head: '备注',
                },
                {
                    name: 'operate',
                    head: '操作',
                    width: 120,
                    align: 'center',
                    operates: [
                        {
                            icon: 'icon-delete',
                            title: '删除', //设置图标title
                            action: function(cell, row, raw) {
                                //动作函数,cell为本格数据，row为本行加工后的数据，raw为本行未加工的数据
                                var dom = gd.showConfirm({
                                    id: 'delete_user',
                                    content: '确定要删除吗?',
                                    btn: [
                                        {
                                            text: '删除',
                                            class: 'gd-btn-danger', //也可以自定义类
                                            enter: true, //响应回车
                                            action: function(dom) {
                                                $.post(
                                                    baseUrl + '/backUp/deleteBackUp',
                                                    {
                                                        ids: raw.id,
                                                        names: raw.name
                                                    },
                                                    function(res) {
                                                        if (res.resultCode == '0') {
                                                            gd.showSuccess('删除成功');
                                                            gd.table('backup').reload();
                                                        } else {
                                                            gd.showError(res.resultMsg);
                                                        }
                                                    }
                                                );
                                            }
                                        },
                                        {
                                            text: '取消',
                                            action: function() {}
                                        }
                                    ],
                                    success: function(dom) {},
                                    end: function(dom) {}
                                });
                            }
                        }
                    ]
                }
            ]
        },
        security: {
            //管控平台密码限制
            number: false,
            capital: false,
            lowercase: false,
            minLength: 0,
            tryCount: 0,
            lockDuration: 0
        },
        access: {
            //管控平台访问控制
            login: {
                username: [],
                ip: []
            },
            resource: {
                ip: []
            }
        },
        approvalExpireResult: [], //管控平台审批结果列表
        approvalExpireTime: [], //管控平台过期时间列表
        approval: {
            //管控平台审批配置
            expireTime: null,
            expireResult: null
        },
        custom: {
            //管控平台自定义
            systemName: '',
            background: null
        },
        backgroundImg: '/osa/images/bg_img.png', //背景图片地址
        backgroundImgT: Math.random() //背景图片后的随机字符串，用以禁缓存
    },
    mounted: function() {
        window.platformValid = gd.validate('#platform');
        //获取管控平台数据
        gd.get(baseUrl + '/system/systemSet/platform', function(msg) {
            if (msg.resultCode == 0) {
                msg.data.approval = msg.data.approval || { expireTime: null, expireResult: null };
                systemConfigList.security = msg.data.security;
                systemConfigList.access = msg.data.access;
                systemConfigList.approval = msg.data.approval;
                systemConfigList.custom = msg.data.custom;
            } else {
                gd.showError('数据加载失败！' + msg.resultMsg);
            }
        });
        gd.get(baseUrl + '/system/systemSet/platform/getApprovalExpire', function(msg) {
            if (msg.resultCode == 0) {
                systemConfigList.approvalExpireResult = msg.data.approvalExpireResult;
                systemConfigList.approvalExpireTime = msg.data.approvalExpireTime;
            } else {
                gd.showError('数据加载失败！' + msg.resultMsg);
            }
        });
    }
});


if(allowSaveNetconfig){
    systemConfigList.meshConfig();
}