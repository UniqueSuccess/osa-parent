'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.showLoading = showLoading;
exports.closeLoading = closeLoading;
/**
 * 显示Loading
 */
function showLoading(text) {
    closeLoading();
    var dom = createDom(text);
    dom.css('z-index', gd.getMaxZIndex() + 10000);
    $('body').append(dom);
    return;
}

/**
 * 创建dom
 */
function createDom(text) {
    if (typeof text !== 'string' && typeof text !== 'number') {
        text = '加载中…';
    }
    var dom = $('\n    <div class="gd-loading">\n        <div class=\'gd-loading-body\'>\n            <div class=\'gd-loading-img\'></div>\n            <div class="gd-loading-text">' + text + '</div>\n        </div>\n    </div>\n    ');
    return dom;
}

/**
 * 关闭Loading
 */
function closeLoading() {
    $('.gd-loading').remove();
}
//# sourceMappingURL=gd_loading.js.map