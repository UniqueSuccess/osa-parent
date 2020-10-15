'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

exports.get = get;
exports.post = post;
exports.query = query;
exports.getMaxZIndex = getMaxZIndex;
exports.clone = clone;
exports.toggleFullscreen = toggleFullscreen;
exports.getBrowserInfo = getBrowserInfo;
exports.padLeft = padLeft;
exports.newDate = newDate;
exports.isDate = isDate;
//console.log简化
window.log = console.log;
/**
 * 通用Get Ajax
 */
function get(url, data, callback) {
    if (typeof data === 'function') {
        callback = data;
        data = '';
    }
    $.ajax({
        type: 'get',
        url: url,
        data: data,
        dataType: 'json',
        success: function success(response, status, xhr) {
            callback(response, status, xhr);
        },
        error: function error(xhr, errorText, errorStatus) {
            callback({
                resultCode: -1,
                resultMsg: errorText
            }, errorStatus, xhr);
        }
    });
}
/**
 * 通用Post Ajax
 */
function post(url, data, callback) {
    $.ajax({
        type: 'post',
        url: url,
        data: data,
        dataType: 'json',
        success: function success(response, status, xhr) {
            callback(response, status, xhr);
        },
        error: function error(xhr, errorText, errorStatus) {
            callback({
                resultCode: -1,
                resultMsg: errorText
            }, errorStatus, xhr);
        }
    });
}
/**
 * 获取url传参
 */
function query(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var param = window.location.search.substr(1).match(reg);
    if (param !== null) {
        return unescape(param[2]);
    } else {
        return '';
    }
}
/**
 * 获取页面中最大zIndex
 */
function getMaxZIndex() {
    var maxZ = Math.max.apply(null, $.map($('body *'), function (e, n) {
        if ($(e).css('position') != 'static') return parseInt($(e).css('z-index')) || 0;
    }));
    return maxZ;
}
/**
 * 数据深复制
 */
function clone(data) {
    if ($.isArray(data)) {
        return $.extend(true, [], data);
    } else if ($.isPlainObject(data)) {
        return $.extend(true, {}, data);
    } else {
        return data;
    }
}
/**
 * 切换全屏
 */
function toggleFullscreen() {
    var fullscreenNode = $('.gd-topbar').find('i.icon-fullscreen-hex');
    if (document.fullscreenElement || document.msFullscreenElement || document.mozFullScreenElement || document.webkitFullscreenElement) {
        if (document.exitFullscreen) {
            document.exitFullscreen();
        } else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
        } else if (document.webkitCancelFullScreen) {
            document.webkitCancelFullScreen();
        } else if (document.msExitFullscreen) {
            document.msExitFullscreen();
        }
        fullscreenNode.attr('title', '全屏');
    } else {
        var docElm = document.documentElement;
        if (docElm.requestFullscreen) {
            docElm.requestFullscreen();
        } else if (docElm.mozRequestFullScreen) {
            docElm.mozRequestFullScreen();
        } else if (docElm.webkitRequestFullScreen) {
            docElm.webkitRequestFullScreen();
        } else if (docElm.msRequestFullscreen) {
            docElm.msRequestFullscreen();
        }
        fullscreenNode.attr('title', '退出全屏');
    }
}
/**
 * 检测浏览器版本
 */
function getBrowserInfo() {
    var ua = window.navigator.userAgent.toLowerCase();
    var match = '';
    var reg = {
        msie: /(msie\s|trident.*rv:)([\w.]+)/,
        edge: /(edge)\/([\d.]+)/,
        firefox: /(firefox)\/([\w.]+)/,
        opera: /(opera).+version\/([\w.]+)/,
        chrome: /(chrome)\/([\w.]+)/,
        safari: /version\/([\w.]+).*(safari)/
    };
    match = reg.msie.exec(ua);
    if (match != null) {
        return { browser: 'ie', version: parseInt(match[2]) || 0 };
    }
    match = reg.edge.exec(ua);
    if (match != null) {
        return { browser: 'edge', version: parseInt(match[2]) || 0 };
    }
    match = reg.firefox.exec(ua);
    if (match != null) {
        return { browser: 'firefox', version: parseInt(match[2]) || 0 };
    }
    match = reg.opera.exec(ua);
    if (match != null) {
        return { browser: 'opera', version: parseInt(match[2]) || 0 };
    }
    match = reg.chrome.exec(ua);
    if (match != null) {
        return { browser: 'chrome', version: parseInt(match[2]) || 0 };
    }
    match = reg.safari.exec(ua);
    if (match != null) {
        return { browser: 'safari', version: parseInt(match[2]) || 0 };
    }
}
/**
 * 数字位数不足前面补0
 */
function padLeft(num, length) {
    return (Array(length).join('0') + num).slice(-length);
}
/**
 * 创建日期对象
 */
function newDate(value) {
    if (value !== undefined) {
        if (typeof value === 'string') {
            value = value.replace(/\-/g, '/');
        }
        return new Date(value);
    } else {
        return new Date();
    }
}
//判断是否是有效日期
function isDate(date) {
    return date instanceof Date && date.toString() !== 'Invalid Date';
}
/**
 * 字符串格式化
 */
String.prototype.format = function (args) {
    var result = this;
    var reg = '';
    if (arguments.length > 0) {
        if (arguments.length == 1 && (typeof args === 'undefined' ? 'undefined' : _typeof(args)) == 'object') {
            for (var key in args) {
                if (args[key] != undefined) {
                    reg = new RegExp('({' + key + '})', 'g');
                    result = result.replace(reg, args[key]);
                }
            }
        } else {
            for (var i = 0; i < arguments.length; i++) {
                if (arguments[i] != undefined) {
                    reg = new RegExp('({)' + i + '(})', 'g');
                    result = result.replace(reg, arguments[i]);
                }
            }
        }
    }
    return result;
};
/**
 * 日期格式化
 */
Date.prototype.format = function (fmt) {
    if (!fmt) {
        fmt = 'yyyy-MM-dd';
    }
    var o = {
        'M+': this.getMonth() + 1,
        'd+': this.getDate(),
        'h+': this.getHours(),
        'm+': this.getMinutes(),
        's+': this.getSeconds(),
        'q+': Math.floor((this.getMonth() + 3) / 3),
        S: this.getMilliseconds()
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    for (var k in o) {
        if (new RegExp('(' + k + ')').test(fmt)) fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
    }return fmt;
};
/**
 * 日期获取真实月份
 */
Date.prototype.getRealMonth = function () {
    return this.getMonth() + 1;
};
/**
 * 日期设置真实月份
 */
Date.prototype.setRealMonth = function (month) {
    this.setMonth(month - 1);
};
/**
 * 表单序列化为JSON
 */
$.fn.serializeJSON = function () {
    var serializeObj = {};
    var array = this.serializeArray();
    var str = this.serialize();
    $(array).each(function () {
        if (serializeObj[this.name]) {
            if ($.isArray(serializeObj[this.name])) {
                serializeObj[this.name].push(this.value);
            } else {
                serializeObj[this.name] = [serializeObj[this.name], this.value];
            }
        } else {
            serializeObj[this.name] = this.value;
        }
    });
    return serializeObj;
};
//# sourceMappingURL=gd_tools.js.map