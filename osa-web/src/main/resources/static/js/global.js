/**
 * 些文件中做一些全局性的操作
 */
var baseUrl = '/osa';
$(function() {
    if ($('#framework').length > 0) {
        renderFramework();
    }
});

// 登录超时重定向
$.ajaxSetup({
    complete: function(xhr, textStatus) {
        if (xhr.getResponseHeader('isRedirect') == 'yes') {
            location.href = '/osa/login';
            return;
        }
        if (xhr.status == '201') {
            location.href = '/osa/login';
            return;
        }
    }
});

var currentUser = '';
//渲染框架
function renderFramework() {
    var topContent = new Vue({
        el: '#framework',
        data: {
            pwLength: '',
            leftMenuConfig: {
                api: baseUrl + '/navigation/getNavigationTreeByUser', //接口地址
                text: '',
                textStyle: {
                    color: '#7B8189',
                    fontSize: '26px',
                    fontWeight: 'bold'
                },
                //data: menuData, //api和data二选一，data是菜单的json数据,如果同时定义，以data优先
                logo: baseUrl + '/images/osa_menu_logo.png', //logo地址
                apiCallback: function(data) {
                    //回调函数可对接口数据进行加工，可选
                    return data;
                },
                ready: function(data) {
                    //菜单加载完成回调
                    //log(data);
                }
                /*
                logoStyle: {
                    //设置logo的样式，可选
                    backgroundPosition: '10px'
                },
                mode: 'load', //可以传入'load'或'iframe'来启用load模式与iframe模式，默认是常规模式
                //changeUrl: true, //load与iframe模式下，是否改变url,默认为false
                action: function(data, param) {
                    //load与iframe模式下,菜单点击后的回调，
                    //load模式下，第一个参数为返回的load内容，第二个参数为当前菜单的参数，
                    //iframe模式下，第一个参数为url，第二个参数为当前菜单的参数
                    console.log(data);
                    console.log(param);
                }
                */
            },
            topbarConfig: [
                {
                    icon: 'icon-account-hex',
                    text: '',
                    dropItems: [
                        {
                            text: '修改密码', //下拉框的文本
                            action: function(data) {
                                var validate = '';
                                $('#showUserInfo').show();
                                gd.showLayer({
                                    id: 'showUserInfo_layer',
                                    title: '修改密码',
                                    content: $('#showUserInfo'),
                                    size: [530, 320],
                                    btn: [
                                        {
                                            text: '确定',
                                            enter: true, //响应回车
                                            action: function(dom) {
                                                var result = validate.valid();
                                                if (result) {
                                                    var addObject = $('#showUserInfo form').serializeJSON();
                                                    addObject.oldPwd = encrypt(addObject.oldPwd).toUpperCase();
                                                    addObject.newPwd = encrypt(addObject.newPwd).toUpperCase();
                                                    addObject.guid = currentUser;
                                                    $.ajax({
                                                        type: 'PUT',
                                                        url: baseUrl + '/user/updateUserPwd',
                                                        data: addObject,
                                                        success: function(res) {
                                                            if (res.resultCode == 0) {
                                                                gd.showSuccess('修改成功！');
                                                                $.get(baseUrl + '/logout', function(res) {
                                                                    if (!res.resultCode) {
                                                                        window.location.href = baseUrl;
                                                                    }
                                                                });
                                                            } else {
                                                                gd.showError(res.resultMsg);
                                                            }
                                                        }
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
                                    success: function() {
                                        // 密码配置
                                        gd.get(baseUrl + '/system/systemSet/platform/security', function(msg) {
                                            if (msg.resultCode == 0) {
                                                topContent.pwLength = msg.data.minLength;
                                                validate = gd.validate('#showUserInfo_layer form', {
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
                        },
                        {
                            text: '退出',
                            action: function() {
                                $.get(baseUrl + '/logout', function(res) {
                                    if (!res.resultCode) {
                                        window.location.href = baseUrl;
                                    }
                                });
                            }
                        }
                    ]
                }
            ]
        },
        mounted: function() {
            var browserInfo = gd.getBrowserInfo();
            if (!(browserInfo.browser == 'ie' && browserInfo.version < 11)) {
                this.topbarConfig.unshift({
                    icon: 'icon-fullscreen-hex',
                    title: '全屏',
                    action: function() {
                        gd.toggleFullscreen();
                    }
                });
            }
            gd.get(baseUrl + '/user/getCurrentUser', {}, function(msg) {
                'icon-account-hex';
                $.each(topContent.topbarConfig, function(_, obj) {
                    if (obj.icon == 'icon-account-hex') {
                        obj.text = msg.data.username;
                        return false;
                    }
                });
                // topContent.topbarConfig[1].text = msg.data.username;
                currentUser = msg.data.guid;
            });
            gd.get(baseUrl + '/system/systemSet/platform/systemName', function(msg) {
                topContent.leftMenuConfig.text = msg.data;
            });
        }
    });
}

var permissionSetArr = [];

$.ajax({
    type: 'GET',
    url: baseUrl + '/permission/findUserPermissions/2',
    async: false,
    success: function(res) {
        if (res.resultCode == 0) {
            var data = res.data;
            permissionSetArr = [];
            data.map(function(item, index) {
                permissionSetArr.push(item.code);
            });
        }
    }
});

//判断是否有授权
function hasPermission(str) {
    return permissionSetArr.indexOf(str) > -1;
}

//设置工具按钮的禁用
function setToolBtnDisable(config, icon, isDisable) {
    var icons = icon.split(',');
    $.each(config, function(_, tool) {
        if (icons.indexOf(tool.icon) > -1) {
            Vue.set(tool, 'disabled', isDisable);
        }
    });
}
//ie10无location.origin
if (typeof location.origin == 'undefined') {
    location.origin = location.protocol + '//' + location.host;
}
