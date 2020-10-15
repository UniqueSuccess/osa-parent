'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.showTip = showTip;
exports.closeTip = closeTip;
exports.showSuccess = showSuccess;
exports.showWarning = showWarning;
exports.showError = showError;
exports.showMsg = showMsg;
exports.closeMsg = closeMsg;
var successDom = '',
    wraningDom = '',
    errorDom = '';

$(function () {
    //鼠标划过显示tooltip
    $('body').on('mouseenter', '[tooltip]', function () {
        var _this = $(this);
        var msg = $(this).attr('tooltip').trim();
        var tips = showTip(_this, msg, { time: 0 });
        $(this).one('mouseleave', function () {
            closeTip(tips);
        });
    });
});
//显示tip信息
function showTip(el, msg, config) {
    var dom = $('\n    <div class="gd-tooltip">\n        <div class="gd-tooltip-text">' + msg + '</div>\n        <div class="gd-tooltip-trangle"></div>\n    </div>\n    ');
    dom.css({
        left: 0,
        top: 0,
        zIndex: gd.getMaxZIndex() + 1
    });
    //支持设置id
    if (typeof config !== 'undefined' && config.id !== undefined) {
        closeTip($('#' + config.id));
        dom.attr('id', config.id);
    }
    $('body').append(dom);
    var pad = 5; //距窗口边缘5px
    var margin = 10; //距目标元素5px
    var elWidth = $(el).outerWidth(); //目标元素宽度
    var elHeight = $(el).outerHeight(); //目标元素高度
    var elLeft = $(el).offset().left; //目标元素left位置
    var elTop = $(el).offset().top; //目标元素top位置

    var tipWidth = dom.width(); //tip宽度
    var tipHeight = dom.height(); //tip高度
    var position = 'top';
    if (typeof config !== 'undefined' && config.position !== undefined) {
        //如果设置的位置，以设置的为准
        position = config.position;
    } else {
        //判定显示位置
        if (elTop - tipHeight - margin > pad) {
            position = 'top';
        } else if (elLeft + elWidth + tipWidth + margin + pad < window.innerWidth) {
            position = 'right';
        } else if (elLeft - tipWidth - margin > pad) {
            position = 'left';
        } else if (elTop + elHeight + tipHeight + margin + pad < window.innerHeight) {
            position = 'bottom';
        }
    }
    var offset = 0; //偏移纠正
    if (position === 'top') {
        var tipLeft = elLeft + (elWidth - tipWidth) / 2; //tip left位置
        var tipRight = window.innerWidth - tipLeft - tipWidth; //tip right位置
        var tipTop = elTop - tipHeight - margin; //tip top位置
        if (tipLeft < pad) {
            offset = pad - tipLeft;
        } else if (tipRight < pad) {
            offset = tipRight - pad;
        }
        dom.css({
            left: tipLeft + offset,
            top: tipTop
        });
        dom.find('.gd-tooltip-trangle').css('margin-left', -offset);
    } else if (position === 'bottom') {
        var _tipLeft = elLeft + (elWidth - tipWidth) / 2; //tip left位置
        var _tipRight = window.innerWidth - _tipLeft - tipWidth; //tip right位置
        var _tipTop = elTop + elHeight + margin; //tip top位置
        if (_tipLeft < pad) {
            offset = pad - _tipLeft;
        } else if (_tipRight < pad) {
            offset = _tipRight - pad;
        }
        dom.css({
            left: _tipLeft + offset,
            top: _tipTop
        });
        dom.find('.gd-tooltip-trangle').css('margin-left', -offset);
    } else if (position === 'right') {
        var _tipLeft2 = elLeft + elWidth + margin; //tip left位置
        var _tipTop2 = elTop - (tipHeight - elHeight) / 2; //tip top位置
        var tipBottom = window.innerHeight - _tipTop2 - tipHeight; //tip bottom位置
        if (_tipTop2 < pad) {
            offset = pad - _tipTop2;
        } else if (tipBottom < pad) {
            offset = tipBottom - pad;
        }
        dom.css({
            left: _tipLeft2,
            top: _tipTop2 + offset
        });
        dom.find('.gd-tooltip-trangle').css('margin-top', -offset);
    } else {
        var _tipLeft3 = elLeft - tipWidth - margin; //tip left位置
        var _tipTop3 = elTop - (tipHeight - elHeight) / 2; //tip top位置
        var _tipBottom = window.innerHeight - _tipTop3 - tipHeight; //tip bottom位置
        if (_tipTop3 < pad) {
            offset = pad - _tipTop3;
        } else if (_tipBottom < pad) {
            offset = _tipBottom - pad;
        }
        dom.css({
            left: _tipLeft3,
            top: _tipTop3 + offset
        });
        dom.find('.gd-tooltip-trangle').css('margin-top', -offset);
    }
    $(dom).addClass('gd-tooltip-show').find('.gd-tooltip-trangle').addClass('gd-tooltip-trangle-' + position);
    if (typeof config !== 'undefined' && config.time !== undefined) {
        if (config.time > 0) {
            setTimeout(function () {
                gd.closeTip(dom);
            }, config.time);
        }
    } else {
        setTimeout(function () {
            gd.closeTip(dom);
        }, 3000);
    }
    //滚动和缩放窗口，关闭tips
    $(el).parents().scroll(function () {
        closeTip(dom);
    });
    $(window).resize(function () {
        closeTip(dom);
    });
    return dom;
}
//关闭tip信息
function closeTip(dom) {
    $(dom).remove();
}
//显示成功提示
function showSuccess(msg, config) {
    return insertInfo(config, msg, 'icon-success');
}
//显示警告提示
function showWarning(msg, config) {
    return insertInfo(config, msg, 'icon-notice');
}
//显示错误提示
function showError(msg, config) {
    return insertInfo(config, msg, 'icon-error');
}
//显示普通信息
function showMsg(msg, config) {
    config = $.extend({}, config);
    if (config.time) {
        config.closeBtn = false;
    } else {
        config.closeBtn = true;
        config.time = 0;
    }
    config.time = parseInt(config.time / 100) * 100;
    var closeHtml = config.closeBtn ? '<i class="gd-msg-close icon-close"></i>' : '';
    var time = config.time / 1000 > 100 ? '100+&nbsp;s' : config.time / 1000 + '&nbsp;s';
    var timeHtml = config.time > 0 ? '<span class="gd-msg-time">' + time + '</span>' : '';
    var btnHtml = '';
    if (config.btn && config.btn.length > 0) {
        btnHtml += '<div class="gd-msg-btn-box">';
        config.btn.forEach(function (o, i) {
            btnHtml += '<button class="gd-msg-btn ' + (o.class || (i === config.btn.length - 1 ? 'gd-btn-cancel' : 'gd-btn')) + '">\n            ' + o.text + '\n            </button>';
        });
        btnHtml += '</div>';
    }
    var dom = $('\n    <div class="gd-msg">\n    ' + closeHtml + timeHtml + '<div class="gd-msg-text">' + msg + '</div>' + btnHtml + '\n    </div>\n    ');
    insertMsg(dom, config);
    dom.find('.gd-msg-close').click(function (e) {
        return closeMsg(dom);
    });
    $(dom).find('.gd-msg-btn').one('click', function () {
        var index = $(dom).find('.gd-msg-btn').index(this);
        if (typeof config.btn[index].action == 'function') {
            config.btn[index].action(dom);
        }
        gd.closeMsg(dom);
    });
    return dom;
}
//将提示信息插入到dom中
function insertInfo(config, msg, icon) {
    $('.gd-info').remove();
    config = $.extend({ time: 3000 }, config);
    config.time = parseInt(config.time / 100) * 100;
    var dom = $('\n    <div class="gd-info">\n        <i class="gd-info-icon ' + icon + '"></i><span class="gd-info-text">' + msg + '</span>\n    </div>\n    ');
    $(dom).css({
        zIndex: gd.getMaxZIndex() + 1000
    });
    $('body').append(dom);
    if (config.time > 0) {
        setTimeout(function () {
            closeMsg(dom);
        }, config.time);
    }
    return dom;
}
//将消息插入到dom中
function insertMsg(dom, config) {
    if (config.id !== undefined) {
        $('#' + config.id).remove();
        dom.attr('id', config.id);
    }
    var top = 60;
    $('.gd-msg').each(function (i, el) {
        $(el).animate({ top: top }, 300);
        var curHeight = $(el).outerHeight();
        top += curHeight + 20;
    });
    $(dom).css({
        top: top,
        zIndex: gd.getMaxZIndex() + 1000
    });
    $('body').append(dom);
    if (config.time > 0) {
        setTimeout(function () {
            var timer = setInterval(function () {
                config.time -= 100;
                if (config.time % 1000 === 0) {
                    $(dom).find('.gd-msg-time').html(config.time / 1000 > 100 ? '100+&nbsp;s' : config.time / 1000 + '&nbsp;s');
                }
                if (config.time < 1) {
                    $(dom).animate({ right: -320 }, 300, function () {
                        closeMsg(dom);
                    });
                    clearInterval(timer);
                }
            }, 100); //100ms为单位，太小了不精确
        }, 600);
    }
}
//关闭消息
function closeMsg(dom, callback) {
    if (dom.is('.gd-info')) {
        dom.addClass('gd-info-hide');
        setTimeout(function () {
            dom.remove();
        }, 200);
    } else {
        $(dom).animate({ right: -320 }, 300, function () {
            $(dom).remove();
            var toTop = 60;
            $('.gd-msg').each(function (i, el) {
                $(el).animate({ top: toTop }, 300);
                var curHeight = $(el).outerHeight();
                toTop += curHeight + 20;
            });
            if (typeof callback === 'function') {
                callback();
            }
        });
    }
}
//# sourceMappingURL=gd_msg.js.map