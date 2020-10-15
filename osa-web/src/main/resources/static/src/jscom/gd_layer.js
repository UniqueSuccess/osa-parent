'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

exports.showLayer = showLayer;
exports.showConfirm = showConfirm;
exports.closeLayer = closeLayer;
exports.closeAllLayer = closeAllLayer;
exports.closeConfirm = closeConfirm;
var layers = []; //保存弹窗，用于全部关闭
/**
 * 显示弹窗
 */
function showLayer(config) {
    if (config.id !== undefined) {
        $('#' + config.id).remove();
    }
    var dom = createLayerDom(config);
    $('body').append(dom);
    var domBody = dom.find('.gd-layer-body');
    var domContent = dom.find('.gd-layer-content');
    var width = domBody.width();
    var height = domBody.height();
    if (config.size instanceof Array && config.size.length == 2) {
        config.size[0] = parseFloat(config.size[0]);
        config.size[1] = parseFloat(config.size[1]);
        domBody.css({
            width: config.size[0],
            height: config.size[1]
        });
        width = config.size[0];
        height = config.size[1];
        if (!config.title && !(config.btn && config.btn.length > 0)) {
            domContent.css('height', config.size[1]);
        } else if (!config.title) {
            domContent.css('height', config.size[1] - 62);
        } else if (!(config.btn && config.btn.length > 0)) {
            domContent.css('height', config.size[1] - 56);
        }
    } else {
        config.size = [width, height];
    }
    var pos = getLayerPosition(width, height);
    if (config.title !== undefined) {
        domBody.addClass('gd-layer-show').css({
            left: pos.left,
            top: pos.top,
            visibility: 'visible'
        });
    } else {
        domBody.addClass('gd-layer-show-center').css({
            left: '50%',
            top: '50%',
            visibility: 'visible'
        });
    }
    setTimeout(function () {
        dom.find('.gd-layer-body').removeClass('gd-layer-show');
    }, 300);
    bindEvents(dom);
    if (dom.config.success !== undefined) {
        dom.config.success(dom);
    }
    dom.focus();
    if (dom.config.autoFocus !== false) {
        var inputs = dom.find('input[type="text"],input[type="password"],textarea');
        $.each(inputs, function (i, el) {
            if (!el.disabled && !el.readOnly) {
                el.focus();
                return false;
            }
        });
    }
    layers.push(dom);
    return dom;
}
/**
 * 显示确认框
 */
function showConfirm(config) {
    var dom = createConfirmDom(config);
    $('body').append(dom);
    var domBody = dom.find('.gd-layer-body');
    var textWidth = domBody.find('.gd-layer-confirm-text').width();
    if (textWidth > parseFloat(domBody.find('.gd-confirm-content').css('max-width')) - 120) {
        domBody.find('.gd-confirm-content').css('text-align', 'left');
    }
    domBody.find('.gd-confirm-content').html(domBody.find('.gd-layer-confirm-text').html());
    var width = domBody.width();
    var height = domBody.height();
    config.size = [width, height];
    var pos = getLayerPosition(width, height);
    domBody.css({
        left: pos.left,
        top: pos.top,
        visibility: 'visible'
    });
    setTimeout(function () {
        domBody.removeClass('gd-layer-show');
    }, 300);
    bindEvents(dom);
    dom.focus();
    if (dom.config.success !== undefined) {
        dom.config.success(dom);
    }
    return dom;
}
/**
 * 创建dom
 */
function createConfirmDom(config) {
    var btnHtml = '';
    if (config.btn && config.btn.length > 0) {
        btnHtml += '<div class="gd-confirm-footer">';
        config.btn.forEach(function (o, i) {
            btnHtml += '<button class="gd-layer-btn ' + (o.class || (i === config.btn.length - 1 && config.btn.length > 1 ? 'gd-btn-cancel' : 'gd-btn')) + '"\n                ' + (o.enter === true ? 'enter' : '') + '>' + o.text + '</button>';
        });
        btnHtml += '</div>';
    }
    var id = config.id === undefined ? '' : 'id="' + config.id + '"';
    var dom = $('\n    <div class="gd-layer gd-confirm" ' + id + ' style="z-index:' + (gd.getMaxZIndex() + 1000) + '">\n        <div class="gd-layer-body gd-layer-show">\n            <div class="gd-confirm-header"></div>\n            <div class="gd-layer-content gd-confirm-content"><div class="gd-layer-confirm-text"></div></div>\n            ' + btnHtml + '\n        </div>\n        <div class="gd-layer-shade"></div>\n    </div>\n    ');
    if (config.content !== undefined) {
        var content = config.content;
        if ((typeof content === 'undefined' ? 'undefined' : _typeof(content)) == 'object') {
            dom.placeholder = $('[gd-layer-placeholder]').length;
            $(config.content).replaceWith('<template gd-layer-placeholder="' + dom.placeholder + '"></template>');
            $(dom).find('.gd-layer-confirm-text').wrapInner($(config.content));
        } else {
            $(dom).find('.gd-layer-confirm-text').append(config.content);
        }
    }
    dom.attr('tabindex', dom.css('z-index'));
    dom.config = config;
    setApi(dom);
    return dom;
}
/**
 * 创建Layer dom
 */
function createLayerDom(config) {
    var closeHtml = '';
    var titleHtml = '';
    if (config.title) {
        titleHtml += '<div class="gd-layer-header">\n                        <div class="gd-layer-title">' + config.title + '</div>\n                        <i class="gd-layer-close icon-close"></i>\n                      </div>';
    } else {
        closeHtml = '<i class="gd-layer-close-btn icon-close"></i>';
    }
    var btnHtml = '';
    if (config.btn && config.btn.length > 0) {
        btnHtml += '<div class="gd-layer-footer">';
        config.btn.forEach(function (o, i) {
            btnHtml += '<button class="gd-layer-btn ' + (o.class || (i === config.btn.length - 1 && config.btn.length > 1 ? 'gd-btn-cancel' : 'gd-btn')) + '"\n                ' + (o.enter === true ? 'enter' : '') + '>' + o.text + '</button>';
        });
        btnHtml += '</div>';
    }

    var id = config.id === undefined ? '' : 'id="' + config.id + '"';
    var dom = $('\n    <div class="gd-layer" ' + id + ' style="z-index:' + (gd.getMaxZIndex() + 1000) + '">\n        <div class="gd-layer-body">\n            ' + titleHtml + closeHtml + '\n            <div class="gd-layer-content"></div>\n            ' + btnHtml + '\n        </div>\n        <div class="gd-layer-shade"></div>\n    </div>\n    ');
    if (config.content !== undefined) {
        var content = config.content;
        if ((typeof content === 'undefined' ? 'undefined' : _typeof(content)) == 'object') {
            dom.placeholder = $('[gd-layer-placeholder]').length;
            $(config.content).replaceWith('<template gd-layer-placeholder="' + dom.placeholder + '"></template>');
            $(dom).find('.gd-layer-content').wrapInner($(config.content));
        } else {
            $(dom).find('.gd-layer-content').append(config.content);
        }
    } else if (config.url !== undefined) {
        $.ajax({
            type: 'get',
            url: config.url,
            success: function success(data) {
                $(dom).find('.gd-layer-content').append(data);
                if (typeof config.ready === 'function') {
                    config.ready(dom);
                }
            }
        });
    }
    dom.attr('tabindex', dom.css('z-index'));
    dom.config = config;
    setApi(dom);
    return dom;
}
/**
 * 设置对外接口
 */
function setApi(dom) {
    //设置content
    dom.setContent = function (content) {
        dom.find('.gd-layer-content').html(content);
        dom.config.content = content;
    };
    //关闭
    dom.close = function () {
        closeLayer(dom);
    };
}
/**
 * 获取弹窗位置
 */
function getLayerPosition(width, height) {
    var layerWidth = parseFloat(width);
    var layerHeight = parseFloat(height);
    var left = (window.innerWidth - layerWidth) / 2 + 'px';
    var top = (window.innerHeight - layerHeight) / 2 + 'px';
    return { left: left, top: top };
}
/**
 * 事件绑定
 */
function bindEvents(dom) {
    var isMouseDown = false,
        startX = 0,
        startY = 0,
        curLeft = 0,
        curTop = 0;

    var layerBody = $(dom).find('.gd-layer-body');
    $(window).resize(function () {
        //窗口缩放重新调整位置
        var pos = getLayerPosition(dom.config.size[0], dom.config.size[1]);
        $(layerBody).css({
            left: pos.left,
            top: pos.top
        });
    });
    $(document)
    //鼠标移动
    .on('mousemove', function (e) {
        var left = curLeft + (e.clientX - startX);
        var top = curTop + (e.clientY - startY);
        left = Math.max(left, 0);
        left = Math.min(left, window.innerWidth - dom.config.size[0]);
        top = Math.max(top, 0);
        top = Math.min(top, window.innerHeight - dom.config.size[1]);
        if (isMouseDown) {
            $(layerBody).css({ left: left, top: top });
        }
    })
    //鼠标放开
    .on('mouseup', function (e) {
        isMouseDown = false;
        $('body').removeClass('gd-cursor-move');
    });

    $(dom)
    //阴影点击
    .on('click', '.gd-layer-shade', function () {
        $(layerBody).addClass('gd-layer-shake');
        setTimeout(function () {
            $(layerBody).removeClass('gd-layer-shake');
        }, 300);
    })
    //鼠标按下
    .on('mousedown', '.gd-layer-header,.gd-confirm-header', function (e) {
        isMouseDown = true;
        curLeft = parseFloat($(layerBody).css('left'));
        curTop = parseFloat($(layerBody).css('top'));
        startX = e.clientX;
        startY = e.clientY;
        $('body').addClass('gd-cursor-move');
    })
    //阻止冒泡
    .on('mousemove', '.gd-layer-close', function () {
        return false;
    })
    //底部按钮点击
    .on('click', '.gd-layer-btn', function () {
        var index = $(dom).find('.gd-layer-btn').index(this);
        if (typeof dom.config.btn[index].action == 'function') {
            if (dom.config.btn[index].action(dom) !== false) {
                gd.closeLayer(dom);
            }
        }
    })
    //关闭
    .on('click', '.gd-layer-close,.gd-layer-close-btn', function () {
        closeLayer(dom);
        return false;
    })
    //回车
    .on('keydown', function (e) {
        if (e.key === 'Enter') {
            dom.find('.gd-layer-btn[enter]:first').click();
        }
    });
}

/**
 * 关闭弹窗
 */
function closeLayer(dom) {
    dom.find('.gd-layer-body').removeClass('gd-layer-show gd-layer-show-center');
    if (dom.config.title !== undefined || dom.is('.gd-confirm')) {
        $(dom).addClass('gd-layer-hide');
        /**
         * @Description: 修改bug
         * @reason：transorm 元素下,fixed 定位失效。关闭layer  select 的下拉框位置偏移
         * 2018/9/6
        */
        /*************    START      ***********/
        $(dom).find('.gd-select-drop').hide();
        /*************    END      ***********/
    } else {
        $(dom).addClass('gd-layer-hide-center');
    }

    setTimeout(function () {
        if (dom.placeholder !== undefined) {
            $('template[gd-layer-placeholder="' + dom.placeholder + '"]').replaceWith(dom.find('.gd-layer-content').children());
        }
        $(dom).remove();
        if (dom.config.end !== undefined) {
            dom.config.end(dom);
        }
        var index = layers.indexOf(dom);
        if (index > -1) {
            layers.splice(index, 1);
        }
        $('.gd-tooltip').remove();
    }, 300);
}
/**
 * 关闭所有弹窗
 */
function closeAllLayer() {
    layers.forEach(function (dom) {
        closeLayer(dom);
    });
}
/**
 * 关闭确认框
 */
function closeConfirm(dom) {
    closelayer(dom);
}
//# sourceMappingURL=gd_layer.js.map