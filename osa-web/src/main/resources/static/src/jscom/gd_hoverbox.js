'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

exports.showHoverBox = showHoverBox;
//显示悬浮窗
function showHoverBox(config) {
    setTimeout(function () {
        bindEvents(dom, config);
    }, 10);
    $('.gd-hoverbox').remove();
    var dom = getDom(config);
    $('body').append(dom);
    setPosition(dom, config);
    $(config.el).addClass('gd-hover');
    return dom;
}

//绑定事件
function bindEvents(dom, config) {
    var isMouseInDom = false; //鼠标是否在dom内
    var isMouseInTarget = true; //鼠标是否在目标元素内
    //点击空白关闭
    $('body').off('click.hoverbox').on('click.hoverbox', function (e) {
        closeHoverBox(dom);
        $('body').off('click.hoverbox');
    });

    //离开目标元素后判断是否要关闭
    $(config.el).off('mouseleave.hoverbox').off('mouseenter.hoverbox').on('mouseleave.hoverbox', function () {
        isMouseInTarget = false;
        setTimeout(function () {
            if (!isMouseInDom && config.autoClose !== false) {
                closeHoverBox(dom);
            }
        }, 10);
    }).on('mouseenter.hoverbox', function () {
        isMouseInTarget = true;
    });
    dom
    //鼠标进入，isMouseInDom为true,防止离开目标元素后关闭
    .on('mouseenter', function () {
        isMouseInDom = true;
    })
    //鼠标移出关闭
    .on('mouseleave', function () {
        isMouseInDom = false;
        setTimeout(function () {
            if (!isMouseInTarget && config.autoClose !== false) {
                closeHoverBox(dom);
            }
        }, 50);
    })
    //点击事件
    .on('click', '.gd-cursor-pointer', function (e) {
        var index = dom.find('.gd-cursor-pointer').index(e.currentTarget);
        if (config.items[index].disabled !== true) {
            if (typeof config.onClick === 'function') {
                config.onClick(gd.clone(config.items[index]), gd.clone(config.items));
            }
            $('.gd-hover').removeClass('gd-hover');
            $(dom).remove();
        }
    })
    //复选框勾选事件
    .on('change', '.gd-hoverbox-item input[type="checkbox"]', function (e) {
        var index = dom.find('.gd-hoverbox-item input[type="checkbox"]').index(e.currentTarget);
        config.items[index].checked = $(e.currentTarget).prop('checked');
        if (typeof config.onChange === 'function') {
            config.onChange(gd.clone(config.items[index]), gd.clone(config.items));
        }
    })
    //阻止冒泡
    .on('click', function (e) {
        e.stopPropagation();
    });
}
/**
 * 设置位置
 */
function setPosition(dom, config) {
    var target = $(config.el);
    var targetTop = target.offset().top;
    var targetLeft = target.offset().left;
    var targetWidth = target.outerWidth();
    var targetHeight = target.outerHeight();
    var domWidth = dom.outerWidth();
    var domHeight = dom.outerHeight();
    var paddingTop = 5; //顶部padding
    var pathOffset = domWidth * 0.3; //人为偏移
    if (config.position === 'left') {
        var offset = Math.min(window.innerHeight - (targetTop + domHeight + 10), 0);
        dom.find('.gd-hoverbox-trangle i').css({
            top: (targetHeight - 12) / 2 - offset + paddingTop
        });
        dom.addClass('gd-hoverbox-left').css({
            left: targetLeft - domWidth - 10,
            top: targetTop + offset - paddingTop
        });
    } else if (config.position === 'right') {
        var _offset = Math.min(window.innerHeight - (targetTop + domHeight + 10), 0);
        dom.find('.gd-hoverbox-trangle i').css({
            top: (targetHeight - 12) / 2 - _offset + paddingTop
        });
        dom.addClass('gd-hoverbox-right').css({
            left: targetLeft + targetWidth + 10,
            top: targetTop + _offset - paddingTop
        });
    } else if (config.position === 'top') {
        var domOffsetLeft = targetLeft - (domWidth - targetWidth) / 2 + pathOffset;
        var domOffsetRight = domOffsetLeft + domWidth;
        var offsetLeft = Math.max(domOffsetLeft, 10);
        if (domOffsetLeft < 10) {
            offsetLeft = 10;
        } else if (window.innerWidth - domOffsetRight < 10) {
            offsetLeft = targetLeft - (domWidth - targetWidth) / 2 - pathOffset;
        } else {
            offsetLeft = domOffsetLeft;
        }
        var _offset2 = offsetLeft - domOffsetLeft + pathOffset;
        dom.addClass('gd-hoverbox-top').find('.gd-hoverbox-trangle i').css({
            left: (domWidth - 12) / 2 - _offset2
        });
        dom.css({
            left: offsetLeft,
            top: targetTop - domHeight - 10
        });
    } else {
        var _domOffsetLeft = targetLeft - (domWidth - targetWidth) / 2 + pathOffset;
        var _domOffsetRight = _domOffsetLeft + domWidth;
        var _offsetLeft = 0;
        if (_domOffsetLeft < 10) {
            _offsetLeft = 10;
        } else if (window.innerWidth - _domOffsetRight < 10) {
            _offsetLeft = targetLeft - (domWidth - targetWidth) / 2 - pathOffset;
        } else {
            _offsetLeft = _domOffsetLeft;
        }
        var _offset3 = _offsetLeft - _domOffsetLeft + pathOffset;
        dom.find('.gd-hoverbox-trangle i').css({
            left: (domWidth - 12) / 2 - _offset3
        });
        dom.addClass('gd-hoverbox-bottom').css({
            left: _offsetLeft,
            top: targetTop + targetHeight + 10
        });
    }
}
//获取dom
function getDom(config) {
    var dom = $('<div class="gd-hoverbox ' + (config.class || '') + '"><div class="gd-hoverbox-trangle"><i></i></div></div>');
    if (config.items instanceof Array) {
        var hasIcon = config.items.some(function (item) {
            return item.icon;
        });
        config.items.forEach(function (item) {
            var iconHtml = hasIcon ? '<i class="' + (item.icon || '') + '"></i>' : '';
            if (config.checkbox == true && item.checked !== true) {
                item.checked = false;
            }
            if (config.checkbox == true) {
                dom.append($('<div class="gd-hoverbox-item" ' + (item.disabled === true ? 'disabled' : '') + '>\n                        ' + iconHtml + '<label class="gd-checkbox">\n                        <input type="checkbox" ' + (item.checked ? 'checked' : '') + ' ' + (item.disabled === true ? 'disabled' : '') + '><i></i>' + item.text + '</label></div>'));
            } else {
                dom.append($('<div class="gd-hoverbox-item gd-cursor-pointer" ' + (item.disabled === true ? 'disabled' : '') + '>\n                    ' + iconHtml + item.text + '</div>'));
            }
        });
    } else if (config.content !== undefined) {
        if (_typeof(config.content) === 'object') {
            dom.placeholder = $('[gd-hoverbox-placeholder]').length;
            $(config.content).after('<template gd-hoverbox-placeholder="' + dom.placeholder + '"></template>');
            dom.append(config.content);
        } else {
            dom.append(config.content);
        }
    }
    dom.css('z-index', gd.getMaxZIndex());
    if (config.size instanceof Array && config.size.length == 2) {
        config.size[0] = parseFloat(config.size[0]);
        config.size[1] = parseFloat(config.size[1]);
        dom.css({
            width: config.size[0],
            height: config.size[1]
        });
    }
    dom.config = config;
    return dom;
}
function closeHoverBox(dom) {
    if (typeof dom.config.onClose === 'function') {
        dom.config.onClose(gd.clone(dom.config.items || dom));
    }
    if (dom.placeholder !== undefined) {
        dom.find('.gd-hoverbox-trangle').remove();
        $('template[gd-hoverbox-placeholder="' + dom.placeholder + '"]').replaceWith(dom.children());
    }
    $(dom).remove();
    $(dom.config.el).removeClass('gd-hover');
    if (typeof dom.config.end === 'function') {
        dom.config.end(dom);
    }
    $('body').off('click.hoverbox');
}
//# sourceMappingURL=gd_hoverbox.js.map